<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Login</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <div class="card">
        <h2>Admin Login</h2>
        <% 
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
            <div class="error-message"><%= errorMessage %></div>
        <% 
                session.removeAttribute("errorMessage");
            }
        %>
        <form action="admin-login" method="POST">
            <input type="email" id="email" name="email" placeholder="Email" class="input-field" required>
            <input type="password" id="password" name="password" placeholder="Password" class="input-field" required>
            <button type="submit">Login</button>
        </form>
    </div>
</div>

</body>
</html>