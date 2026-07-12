package lorenzohq.nexus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lorenzohq.nexus.entity.Tickets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromAddress;

    @Async
    public void sendTicketCreatedEmail(Tickets ticket) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(ticket.getUser().getEmail());
            message.setSubject("Ticket created: " + ticket.getSubject());
            message.setText(
                    "Hi " + ticket.getUser().getName() + ",\n\n" +
                            "Your support ticket has been created.\n\n" +
                            "Ticket ID: " + ticket.getId() + "\n" +
                            "Subject: " + ticket.getSubject() + "\n" +
                            "We will get back to you shortly.\n\n" +
                            "Nexus Support"
            );
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send ticket creation email for ticket {}", ticket.getId(), ex);
        }
    }
}
