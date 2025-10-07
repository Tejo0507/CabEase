package com.cabease.servlet;

import com.cabease.util.DBConnection;
import model.Cab;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/EditCabServlet")
public class EditCabServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String sql = "SELECT * FROM cabs WHERE id = ?";
        Cab cab = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String cabType = rs.getString("cabType");
                String driverName = rs.getString("driverName");
                String cabNumber = rs.getString("cabNumber");
                double pricePerKm = rs.getDouble("pricePerKm");
                cab = new Cab(id, cabType, driverName, cabNumber, pricePerKm);
            }

        } catch (SQLException e) {
            throw new ServletException("Database error while fetching cab for edit.", e);
        }

        request.setAttribute("cab", cab);
        RequestDispatcher dispatcher = request.getRequestDispatcher("edit_cab.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));
        String cabType = request.getParameter("cabType");
        String driverName = request.getParameter("driverName");
        String cabNumber = request.getParameter("cabNumber");
        double pricePerKm = Double.parseDouble(request.getParameter("pricePerKm"));

        String sql = "UPDATE cabs SET cabType = ?, driverName = ?, cabNumber = ?, pricePerKm = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cabType);
            pstmt.setString(2, driverName);
            pstmt.setString(3, cabNumber);
            pstmt.setDouble(4, pricePerKm);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new ServletException("Database error while updating cab.", e);
        }

        response.sendRedirect("view-cabs");
    }
}
