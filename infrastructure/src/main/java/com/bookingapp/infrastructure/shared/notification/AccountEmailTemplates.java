package com.bookingapp.infrastructure.shared.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Writes the account emails. They are in French, like the website, and each carries a link to the
 * page of the website that finishes the action.
 */
@Component
public class AccountEmailTemplates {

    private final String frontendUrl;

    public AccountEmailTemplates(@Value("${app.frontend-url}") String frontendUrl) {
        this.frontendUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
    }

    public AccountEmail emailVerification(String firstName, String token) {
        return message("Confirmez votre adresse e-mail", firstName,
                "Bienvenue sur Bookly ! Confirmez votre adresse e-mail pour finaliser votre inscription.",
                "Confirmer mon adresse", link("/verify-email", token),
                "Ce lien est valable 2 jours. Si vous n'avez pas créé de compte, ignorez cet e-mail.");
    }

    public AccountEmail passwordReset(String firstName, String token) {
        return message("Réinitialisez votre mot de passe", firstName,
                "Vous avez demandé à changer de mot de passe. Choisissez-en un nouveau avec le bouton ci-dessous.",
                "Choisir un nouveau mot de passe", link("/reset-password", token),
                "Ce lien est valable 1 heure. Si vous n'êtes pas à l'origine de cette demande, ignorez cet e-mail : "
                        + "votre mot de passe reste inchangé.");
    }

    private String link(String path, String token) {
        return frontendUrl + path + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
    }

    private static AccountEmail message(String subject, String firstName, String intro, String action, String link,
                                        String note) {
        String text = """
                Bonjour %s,

                %s

                %s : %s

                %s

                L'équipe Bookly
                """.formatted(firstName, intro, action, link, note);
        String html = """
                <div style="margin:0;padding:32px 16px;background:#fbf8f3;font-family:Arial,Helvetica,sans-serif;color:#141b1a">
                  <div style="max-width:480px;margin:0 auto;background:#ffffff;border:1px solid #ebe1d1;border-radius:20px;padding:32px">
                    <p style="margin:0 0 24px;font-size:22px;font-weight:bold;color:#d13f15">bookly</p>
                    <p style="margin:0 0 12px;font-size:16px">Bonjour %s,</p>
                    <p style="margin:0 0 24px;font-size:16px;line-height:1.5">%s</p>
                    <p style="margin:0 0 24px">
                      <a href="%s" style="display:inline-block;padding:14px 22px;background:#e8551f;color:#ffffff;font-size:16px;font-weight:bold;text-decoration:none;border-radius:12px">%s</a>
                    </p>
                    <p style="margin:0 0 8px;font-size:13px;line-height:1.5;color:#63706d">%s</p>
                    <p style="margin:0;font-size:13px;line-height:1.5;color:#63706d;word-break:break-all">Le bouton ne fonctionne pas ? Copiez ce lien : %s</p>
                  </div>
                </div>
                """.formatted(HtmlUtils.htmlEscape(firstName), intro, link, action, note, link);
        return new AccountEmail(subject, text, html);
    }
}
