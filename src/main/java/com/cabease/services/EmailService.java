package com.cabease.services;
import com.cabease.models.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
@Service
@Slf4j
public class EmailService {
    @Autowired(required = true)
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${server.port:8080}")
    private String serverPort;
    private final String baseUrl = "http://localhost:";
    @Async
    public void sendBookingConfirmation(Booking booking) {
        log.info("📧 EMAIL SERVICE CALLED - sendBookingConfirmation for booking: {}", booking.getId());
        log.info("📧 MailSender configured: {}", mailSender != null);
        log.info("📧 User email: {}", booking.getUser() != null ? booking.getUser().getEmail() : "NULL");
        try {
            String cancellationToken = generateCancellationToken(booking);
            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("user", booking.getUser());
            context.setVariable("cab", booking.getCab());
            context.setVariable("driver", booking.getCab() != null ? booking.getCab().getDriver() : null);
            context.setVariable("cancellationUrl", generateCancellationUrl(booking.getId(), cancellationToken));
            context.setVariable("bookingDateTime", booking.getBookingDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
            context.setVariable("estimatedFare", String.format("₹%.2f", booking.getTotalFare()));
            context.setVariable("trackingUrl", "http://localhost:" + serverPort + "/ride-tracking?bookingId=" + booking.getId());
            String htmlContent = templateEngine.process("emails/booking-confirmation", context);
            log.info("📧 About to send email to: {}", booking.getUser().getEmail());
            sendEmail(
                booking.getUser().getEmail(),
                "🚗 CabEase Ride Confirmed - Track Your Driver Live!",
                htmlContent
            );
            log.info("📧 Email sent successfully to {} for booking {}",
                    booking.getUser().getEmail(), booking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email for booking {}", booking.getId(), e);
        }
    }
    @Async
    public void sendBookingCancellation(Booking booking) {
        try {
            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("user", booking.getUser());
            context.setVariable("pickupLocation", booking.getPickupLocation());
            context.setVariable("dropLocation", booking.getDropLocation());
            context.setVariable("cancellationDateTime", booking.getCancelledAt() != null ?
                booking.getCancelledAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")) :
                "N/A");
            String htmlContent = templateEngine.process("emails/booking-cancellation", context);
            sendEmail(
                booking.getUser().getEmail(),
                "CabEase Ride Cancelled",
                htmlContent
            );
            log.info("Sent booking cancellation email to {} for booking {}",
                    booking.getUser().getEmail(), booking.getId());
        } catch (Exception e) {
            log.error("Failed to send booking cancellation email for booking {}", booking.getId(), e);
        }
    }
    @Async
    public void sendDriverNotification(Booking booking) {
        try {
            if (booking.getCab() != null && booking.getCab().getDriver() != null &&
                booking.getCab().getDriver().getEmail() != null) {
                Context context = new Context();
                context.setVariable("booking", booking);
                context.setVariable("driver", booking.getCab().getDriver());
                context.setVariable("user", booking.getUser());
                context.setVariable("pickupLocation", booking.getPickupLocation());
                context.setVariable("dropLocation", booking.getDropLocation());
                context.setVariable("estimatedFare", String.format("₹%.2f", booking.getTotalFare()));
                String htmlContent = templateEngine.process("emails/driver-notification", context);
                sendEmail(
                    booking.getCab().getDriver().getEmail(),
                    "New Ride Assignment - Booking ID: " + booking.getId(),
                    htmlContent
                );
                log.info("Sent driver notification email for booking {}", booking.getId());
            }
        } catch (Exception e) {
            log.error("Failed to send driver notification email for booking {}", booking.getId(), e);
        }
    }
    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        log.info("📧 sendEmail method called - To: {}, Subject: {}", to, subject);
        if (mailSender == null) {
            log.error("❌ Mail sender is NULL! Email configuration may be missing or invalid.");
            log.error("❌ Please check application.properties mail settings");
            return;
        }
        log.info("✅ MailSender is configured. Attempting to send email...");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("noreply@cabease.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
        log.info("✅ Email sent successfully to: {}", to);
    }
    private String generateCancellationToken(Booking booking) {
        return UUID.randomUUID().toString() + "-" + booking.getId();
    }
    private String generateCancellationUrl(Long bookingId, String token) {
        return String.format("%s:%s/api/bookings/%d/cancel?token=%s",
                baseUrl, serverPort, bookingId, token);
    }
}
