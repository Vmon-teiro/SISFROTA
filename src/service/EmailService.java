package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailService {

    // Preencha com seu e-mail real e a Senha de App de 16 dígitos gerada no Google
    private static final String REMETENTE = "seu.email.real@gmail.com"; 
    private static final String SENHA_APP = "xxxx xxxx xxxx xxxx"; 

    public static boolean enviarRelatorioPorEmail(String destinatario, String caminhoAnexo) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMETENTE, SENHA_APP);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMETENTE, "Gestão Náutica"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Relatório de Custos Operacionais - Gestão Náutica");

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Segue em anexo o relatório atualizado de custos da frota.");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Anexo em PDF
            if (caminhoAnexo != null && !caminhoAnexo.isEmpty()) {
                File arquivo = new File(caminhoAnexo);
                if (arquivo.exists()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(arquivo);
                    multipart.addBodyPart(attachmentPart);
                }
            }

            message.setContent(multipart);
            Transport.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail SMTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}