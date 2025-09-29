package Service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletContext;
import java.util.Properties;

public class MailService {
    private final String smtpHost;
    private final String smtpPort;
    private final String smtpUser;
    private final String smtpPass;
    private final String smtpAuth;
    private final String smtpStartTls;
    private final String fromAddress;

    public MailService(ServletContext context) {
        this.smtpHost = getRequiredParam(context, "mail.smtp.host");
        this.smtpPort = getRequiredParam(context, "mail.smtp.port");
        this.smtpUser = context.getInitParameter("mail.smtp.user");
        this.smtpPass = context.getInitParameter("mail.smtp.pass");
        this.smtpAuth = context.getInitParameter("mail.smtp.auth");
        this.smtpStartTls = context.getInitParameter("mail.smtp.starttls.enable");
        String configuredFrom = context.getInitParameter("mail.from");
        this.fromAddress = configuredFrom != null && !configuredFrom.isEmpty() ? configuredFrom : this.smtpUser;
    }

    private String getRequiredParam(ServletContext ctx, String name) {
        String value = ctx.getInitParameter(name);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Missing required context-param: " + name);
        }
        return value;
    }

    public boolean send(String to, String subject, String htmlContent) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            if (smtpAuth != null) props.put("mail.smtp.auth", smtpAuth);
            if (smtpStartTls != null) props.put("mail.smtp.starttls.enable", smtpStartTls);

            Session session;
            if (smtpUser != null && !smtpUser.isEmpty()) {
                session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUser, smtpPass);
                    }
                });
            } else {
                session = Session.getInstance(props);
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
} 