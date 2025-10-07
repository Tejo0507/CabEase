package com.cabease.servlet;

import com.cabease.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/add-cab")
public class AddCabServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String cabType = request.getParameter("cabType");
        String driverName = request.getParameter("driverName");
        String cabNumber = request.getParameter("cabNumber");
        double pricePerKm = Double.parseDouble(request.getParameter("pricePerKm"));

        String sql = "INSERT INTO cabs (cabType, driverName, cabNumber, pricePerKm) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cabType);
            pstmt.setString(2, driverName);
            pstmt.setString(3, cabNumber);
            pstmt.setDouble(4, pricePerKm);
            pstmt.executeUpdate();

            response.sendRedirect("view_cabs.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error while adding cab.", e);
        }
    }
}
