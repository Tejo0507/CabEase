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
    <title>Add New Cab</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <div class="card">
        <h2>Add New Cab</h2>
        <form action="add-cab" method="POST">
            <select id="cabType" name="cabType" class="input-field" required>
                <option value="" disabled selected>Select Cab Type</option>
                <option value="Mini">Mini</option>
                <option value="Sedan">Sedan</option>
                <option value="SUV">SUV</option>
            </select>
            <input type="text" id="driverName" name="driverName" placeholder="Driver Name" class="input-field" required>
            <input type="text" id="cabNumber" name="cabNumber" placeholder="Cab Number" class="input-field" required>
            <input type="number" id="pricePerKm" name="pricePerKm" placeholder="Price Per Km" step="0.01" min="0" class="input-field" required>
            <button type="submit">Add Cab</button>
        </form>
        <div style="text-align: center; margin-top: 20px;">
            <a href="admin_dashboard.jsp"><button class="accent">Back to Dashboard</button></a>
        </div>
    </div>
</div>

</body>
</html>