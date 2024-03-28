package jp.dcworks.engineersgate.contactform.component;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jp.dcworks.engineersgate.contactform.dto.RequestContact;

/**
 * メール送信クラス。
 *
 * @author tomo-sato
 */
public class MailService {

	/**
	 * メール送信を行う。
	 *
	 * @param requestContact
	 * @throws Exception
	 */
	public static void send(RequestContact requestContact) throws Exception {
		// メールサーバ
		String mailhost = "localhost";
		// 送信元メールアドレス
		String from = "tsato+from@localhost";
		// 送信先メールアドレス
		String to = requestContact.getMail();
		// メールタイトル
		String subject = requestContact.getTitle();
		// メール本文
		String text = requestContact.getBody();

		Properties props = System.getProperties();
		props.put("mail.smtp.host", mailhost);

		Session session = Session.getInstance(props, null);
		session.setDebug(true);

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));

		msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to, false));

		msg.setSubject(subject);
		msg.setText(text);
		msg.setSentDate(new Date());

		Transport.send(msg);

		System.out.println("\nMail was sent successfully.");
	}
}
