<%@ page import="com.cabease.model.Cab" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% 
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect("admin_login.jsp");
        return;
    }
    Cab cab = (Cab) request.getAttribute("cab");
    if (cab == null) {
        // Redirect or show an error if cab object is not found
        response.sendRedirect("view_cabs.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Cab</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <div class="card">
        <h2>Edit Cab Details</h2>
        <form action="edit-cab" method="POST">
            <input type="hidden" name="id" value="<%= cab.getId() %>">
            
            <select id="cabType" name="cabType" class="input-field" required>
                <option value="Mini" <%= "Mini".equals(cab.getCabType()) ? "selected" : "" %>>Mini</option>
                <option value="Sedan" <%= "Sedan".equals(cab.getCabType()) ? "selected" : "" %>>Sedan</option>
                <option value="SUV" <%= "SUV".equals(cab.getCabType()) ? "selected" : "" %>>SUV</option>
            </select>
            
            <input type="text" id="driverName" name="driverName" value="<%= cab.getDriverName() %>" class="input-field" required>
            
            <input type="text" id="cabNumber" name="cabNumber" value="<%= cab.getCabNumber() %>" class="input-field" required>
            
            <input type="number" id="pricePerKm" name="pricePerKm" step="0.01" min="0" value="<%= cab.getPricePerKm() %>" class="input-field" required>
            
            <button type="submit">Update Cab</button>
        </form>
        <div style="text-align: center; margin-top: 20px;">
            <a href="view_cabs.jsp"><button class="accent">Back to View Cabs</button></a>
        </div>
    </div>
</div>

</body>
</html>