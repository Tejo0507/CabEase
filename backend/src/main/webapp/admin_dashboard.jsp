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
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <div class="card">
        <h2 style="text-align: center;">Welcome, <%= session.getAttribute("adminUser") %></h2>
        <div style="display: flex; justify-content: center; gap: 20px; margin-top: 20px;">
            <a href="add_cab.jsp"><button>Add Cab</button></a>
            <a href="view_cabs.jsp"><button>View Cabs</button></a>
            <a href="view_bookings.jsp"><button>View Bookings</button></a>
            <a href="view_users.jsp"><button>View Users</button></a>
            <a href="admin_logout.jsp"><button class="danger">Logout</button></a>
        </div>
    </div>
</div>

</body>
</html>