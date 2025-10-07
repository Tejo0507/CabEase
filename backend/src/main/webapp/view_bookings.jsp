<%@ page import="java.sql.*" %>
<%@ page import="com.cabease.util.DBConnection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% 
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect("admin_login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>View Bookings</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <h2>All Bookings</h2>
            <a href="admin_dashboard.jsp"><button class="accent">Back to Dashboard</button></a>
        </div>

        <table>
            <thead>
                <tr>
                    <th>User Name</th>
                    <th>Driver Name</th>
                    <th>Pickup</th>
                    <th>Drop</th>
                    <th>Total Cost</th>
                    <th>Booking Time</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    String sql = "SELECT u.name as userName, c.driverName, b.pickup, b.drop_location, b.total_cost, b.booking_time FROM bookings b JOIN users u ON b.user_id = u.id JOIN cabs c ON b.cab_id = c.id ORDER BY b.booking_time DESC";
                    try (Connection conn = DBConnection.getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {

                        if (!rs.isBeforeFirst()) {
                %>
                            <tr><td colspan="6" style="text-align:center;">No bookings found.</td></tr>
                <%      } else {
                            while (rs.next()) {
                %>
                                <tr>
                                    <td><%= rs.getString("userName") %></td>
                                    <td><%= rs.getString("driverName") %></td>
                                    <td><%= rs.getString("pickup") %></td>
                                    <td><%= rs.getString("drop_location") %></td>
                                    <td><%= String.format("%.2f", rs.getDouble("total_cost")) %></td>
                                    <td><%= rs.getTimestamp("booking_time").toString() %></td>
                                </tr>
                <%          }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                %>
                        <tr><td colspan="6" style="text-align:center; color:red;">Error loading data.</td></tr>
                <%  }
                %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>