<%@ page import="java.util.List" %>
<%@ page import="com.cabease.model.Cab" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% 
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect("admin_login.jsp");
        return;
    }
    List<Cab> cabList = (List<Cab>) request.getAttribute("cabList");
%>
<!DOCTYPE html>
<html>
<head>
    <title>View Cabs</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <h2>Manage Cabs</h2>
            <a href="admin_dashboard.jsp"><button class="accent">Back to Dashboard</button></a>
        </div>

        <table>
            <thead>
                <tr>
                    <th>Cab Type</th>
                    <th>Driver Name</th>
                    <th>Cab Number</th>
                    <th>Price per KM</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% if (cabList != null && !cabList.isEmpty()) { %>
                    <% for (Cab cab : cabList) { %>
                        <tr>
                            <td><%= cab.getCabType() %></td>
                            <td><%= cab.getDriverName() %></td>
                            <td><%= cab.getCabNumber() %></td>
                            <td><%= String.format("%.2f", cab.getPricePerKm()) %></td>
                            <td>
                                <div class="button-group">
                                    <a href="edit-cab?id=<%= cab.getId() %>"><button>Edit</button></a>
                                    <a href="delete-cab?id=<%= cab.getId() %>" onclick="return confirm('Are you sure you want to delete this cab?')"><button class="danger">Delete</button></a>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                <% } else { %>
                    <tr>
                        <td colspan="5" style="text-align:center;">No cabs found. <a href="add_cab.jsp">Add one now</a>.</td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>