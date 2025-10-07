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
    <title>View Users</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <h2>All Registered Users</h2>
            <a href="admin_dashboard.jsp"><button class="accent">Back to Dashboard</button></a>
        </div>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    String sql = "SELECT * FROM users ORDER BY id ASC";
                    try (Connection conn = DBConnection.getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {

                        if (!rs.isBeforeFirst()) {
                %>
                            <tr><td colspan="4" style="text-align:center;">No users found.</td></tr>
                <%      } else {
                            while (rs.next()) {
                %>
                                <tr>
                                    <td><%= rs.getInt("id") %></td>
                                    <td><%= rs.getString("name") %></td>
                                    <td><%= rs.getString("email") %></td>
                                    <td><%= rs.getString("phone") %></td>
                                </tr>
                <%          }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                %>
                        <tr><td colspan="4" style="text-align:center; color:red;">Error loading data.</td></tr>
                <%  }
                %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>