package info.kornhuber.jobsearch.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetMailService {

    private final JavaMailSender mailSender;
    private final String resetPasswordBaseUrl;

    public PasswordResetMailService(JavaMailSender mailSender,
                                    @Value("${app.frontend.reset-password-url}") String resetPasswordBaseUrl) {
        this.mailSender = mailSender;
        this.resetPasswordBaseUrl = resetPasswordBaseUrl;
    }

    public void sendResetMail(String to, String rawToken) {
        String resetLink = resetPasswordBaseUrl + "?token=" + rawToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Passwort zurücksetzen");
        message.setText("""
                Du hast ein Zurücksetzen deines Passworts angefordert.

                Öffne diesen Link:
                %s

                Der Link ist 30 Minuten gültig.
                Wenn du das nicht warst, kannst du diese E-Mail ignorieren.
                """.formatted(resetLink));

        mailSender.send(message);
    }
}