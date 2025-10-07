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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/view-cabs")
public class ViewCabsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Cab> cabList = new ArrayList<>();
        String sql = "SELECT * FROM cabs ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String cabType = rs.getString("cabType");
                String driverName = rs.getString("driverName");
                String cabNumber = rs.getString("cabNumber");
                double pricePerKm = rs.getDouble("pricePerKm");
                cabList.add(new Cab(id, cabType, driverName, cabNumber, pricePerKm));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error while viewing cabs.", e);
        }

        request.setAttribute("cabList", cabList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("view_cabs.jsp");
        dispatcher.forward(request, response);
    }
}
