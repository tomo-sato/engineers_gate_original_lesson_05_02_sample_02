package jp.dcworks.engineersgate.contactform.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Contact DTOクラス。
 * コンタクト画面で入力された値を保持するクラス。
 *
 * @author tomo-sato
 */
@Data
public class RequestContact implements Serializable {

	/** ご氏名 */
	@NotBlank(message = "ご氏名は必須項目となります。")
	@Size(max = 32, message = "ご氏名は最大{max}文字です。")
	private String name;

	/** メールアドレス */
	@NotBlank(message = "メールアドレスは必須項目となります。")
	@Size(max = 256, message = "名前は最大{max}文字です。")
	private String mail = "";

	/** タイトル */
	@NotBlank(message = "タイトルは必須項目となります。")
	@Size(max = 32, message = "名前は最大{max}文字です。")
	private String title = "";

	/** 問い合わせ内容 */
	@NotBlank(message = "問い合わせ内容は必須項目となります。")
	@Size(max = 1000, message = "問い合わせ内容は最大{max}文字です。")
	private String body;

}
