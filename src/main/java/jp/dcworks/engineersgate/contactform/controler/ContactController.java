package jp.dcworks.engineersgate.contactform.controler;

import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jp.dcworks.engineersgate.contactform.component.MailService;
import jp.dcworks.engineersgate.contactform.dto.RequestContact;
import lombok.extern.log4j.Log4j2;

/**
 * コンタクト画面コントローラー。
 *
 * @author tomo-sato
 */
@Log4j2
@Controller
@RequestMapping("/")
public class ContactController {

	/**
	 * [GET]初期表示処理。
	 * 　　・入力画面を表示する。
	 *
	 * @param model 画面データ受け渡しオブジェクト
	 * @return テンプレートpath
	 */
	@GetMapping("contact")
	public String contact(Model model) {

		if (!model.containsAttribute("requestContact")) {
			model.addAttribute("requestContact", new RequestContact());
		}
		return "/contact";
	}

	/**
	 * [POST]「確認画面へ」ボタン押下時のリクエスト処理。
	 * 　　・入力内容にエラーがあった場合、入力画面を表示する。
	 * 　　・エラーが無かった場合、確認画面を表示する。
	 *
	 * @param requestContact 入力フォームの内容
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 * @param session セッションオブジェクト
	 * @return テンプレートpath
	 */
	@PostMapping("contact")
	public String contact(@Validated @ModelAttribute RequestContact requestContact,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			HttpSession session) {

		log.info("確認画面のアクションが呼ばれました。：requestContact={}", requestContact);

		// バリデーション。
		if (result.hasErrors()) {
			log.warn("バリデーションエラーが発生しました。：requestContact={}, result={}", requestContact, result);

			// エラーメッセージを入力画面渡すFlashオブジェクトにセット。
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.requestContact", result);
			redirectAttributes.addFlashAttribute("requestContact", requestContact);

			// 入力画面へリダイレクト。
			return "redirect:/contact";
		}

		// セッションに保持。メール送信処理で使用。
		session.setAttribute("requestContact", requestContact);

		// 確認画面を表示。
		return "/confirm";
	}


	/**
	 * [POST]メール送信処理。
	 * 　　・セッションから値が取得できなかった場合、もしくはセッションの値が書き換えられていた場合、エラー画面を表示する。
	 * 　　・エラーが無かった場合、確認画面を表示する。
	 *
	 * @param response レスポンス
	 * @param session セッションオブジェクト
	 * @param model 画面データ受け渡しオブジェクト
	 * @return テンプレートpath
	 */
	@PostMapping("send")
	public String send(HttpServletResponse response, HttpSession session, Model model) {

		// セッションチェック（セッションが生成されていなかったり、指定のキーで値が取得できなかった場合はエラー。）
		if (session == null || session.getAttribute("requestContact") == null) {
			log.warn("セッションから値が取得できませんでした。");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return "Forbidden";
		}

		// 確認画面でセットしたセッションから入力値を取得。
		RequestContact requestContact = (RequestContact) session.getAttribute("requestContact");
		// 多重送信されないようにする為、セッションクリア。
		session.invalidate();

		// セッションの値をチェック。
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<RequestContact>> violations = validator.validate(requestContact);

		// バリデーションエラーがあった場合はエラー。
		if (violations != null && !violations.isEmpty()) {
			log.warn("セッションの値が書き換えられた可能性があります。：violations={}, requestContact={}", violations, requestContact);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return "Forbidden";
		}

		log.info("メール送信処理開始。：requestContact={}", requestContact);

		try {
			// メール送信
			MailService.send(requestContact);

			// 画面にセット
			model.addAttribute("requestContact", requestContact);

		} catch (Exception e) {
			e.printStackTrace();

			// メールが送信できない状態は、異常とみなし500エラーとする。
			log.error("メール送信時にエラーが発生しました。：requestContact={}, e={}", requestContact, e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return "Internal server error";
		}

		return "thanks";
	}
}
