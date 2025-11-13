package com.cabease.services.email;
import com.cabease.models.Booking;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
public class EmailService {
    public void sendBookingConfirmation(Booking booking) {
        final String username = "tejo.singaporeserver@gmail.com";
        final String password = "fcuc ozys szjj rvqh";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(booking.getUser().getEmail()));
            message.setSubject("Booking Confirmation - CabEase");
            String emailContent = String.format(
                "Dear %s,\n\nYour cab booking is confirmed!\n\nDetails:\n- Cab Model: %s\n- Cab Number: %s\n- From: %s\n- To: %s\n\nThank you for using CabEase!",
                booking.getUser().getUsername(),
                booking.getCab().getModel(),
                booking.getCab().getCabNumber(),
                booking.getSource(),
                booking.getDestination()
            );
            message.setText(emailContent);
            Transport.send(message);
            System.out.println("Booking confirmation email sent successfully to " + booking.getUser().getEmail());
        } catch (MessagingException e) {
            System.out.println("Failed to send email. Error: " + e.getMessage());
        }
    }
    public void sendCancellationConfirmation(Booking booking) {
        final String username = "tejo.singaporeserver@gmail.com";
        final String password = "fcuc ozys szjj rvqh";
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(booking.getUser().getEmail()));
            message.setSubject("Booking Cancellation - CabEase");
            String emailContent = String.format(
                "Dear %s,\n\nYour cab booking has been cancelled successfully.\n\nCancelled Booking Details:\n" +
                "- Cab Model: %s\n- Cab Number: %s\n- From: %s\n- To: %s\n\n" +
                "If you didn't request this cancellation, please contact our support team immediately.\n\n" +
                "Thank you for using CabEase!",
                booking.getUser().getUsername(),
                booking.getCab().getModel(),
                booking.getCab().getCabNumber(),
                booking.getSource(),
                booking.getDestination()
            );
            message.setText(emailContent);
            Transport.send(message);
            System.out.println("Cancellation confirmation email sent successfully to " + booking.getUser().getEmail());
        } catch (MessagingException e) {
            System.out.println("Failed to send cancellation email. Error: " + e.getMessage());
        }
    }
}
