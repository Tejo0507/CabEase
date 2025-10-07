package com.cabease.servlet;

import com.cabease.util.MailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/book-cab")
public class BookingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("login.jsp"); // Or an error page
            return;
        }

        try {
            int cabId = Integer.parseInt(request.getParameter("cabId"));
            String pickup = request.getParameter("pickup");
            String drop = request.getParameter("drop");
            double distance = Double.parseDouble(request.getParameter("distance"));

            double pricePerKm = getPriceForCab(cabId);
            double totalCost = distance * pricePerKm;

            // Simulate saving to database
            System.out.println("Booking saved for user: " + session.getAttribute("userEmail"));
            System.out.println("Details: Cab ID " + cabId + ", From " + pickup + " to " + drop + ", Cost: " + totalCost);

            String userEmail = (String) session.getAttribute("userEmail");
            String subject = "Your Cab Booking Confirmation";
            String messageBody = String.format(
                "Hello,\n\nYour booking is confirmed!\n\nPickup Location: %s\nDrop Location: %s\nDistance: %.2f km\nTotal Fare: $%.2f\n\nThank you for choosing CabEase!",
                pickup, drop, distance, totalCost
            );

            MailUtil.sendMail(userEmail, subject, messageBody);

            response.sendRedirect("booking_success.jsp");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input format for distance or cab ID.");
            e.printStackTrace();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    private double getPriceForCab(int cabId) {
        // Placeholder for fetching price from a database
        switch (cabId) {
            case 1: // Mini
                return 10.0;
            case 2: // Sedan
                return 15.0;
            case 3: // SUV
                return 20.0;
            default:
                return 12.0;
        }
    }
}
