<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome to CabEase</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        body, html {
            margin: 0;
            padding: 0;
            height: 100%;
            font-family: 'Poppins', sans-serif;
        }
        .hero-section {
            background: url('https://images.pexels.com/photos/386009/pexels-photo-386009.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2') no-repeat center center/cover;
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            color: white;
            position: relative;
        }
        .hero-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
        }
        .nav-bar {
            position: absolute;
            top: 0;
            width: 100%;
            padding: 20px 50px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-sizing: border-box;
            z-index: 2;
        }
        .logo {
            font-size: 28px;
            font-weight: 700;
            color: #FFC107;
        }
        .hero-content {
            position: relative;
            z-index: 1;
        }
        .hero-content h1 {
            font-size: 48px;
            font-weight: 700;
            margin-bottom: 20px;
            color: white;
        }
        .hero-content p {
            font-size: 22px;
            margin-bottom: 40px;
        }
        .hero-buttons .button {
            margin: 0 15px;
            padding: 15px 30px;
            font-size: 18px;
            border-radius: 10px;
        }
        .footer {
            position: absolute;
            bottom: 0;
            width: 100%;
            text-align: center;
            padding: 20px;
            color: #ccc;
            z-index: 1;
        }
    </style>
</head>
<body>

    <div class="hero-section">
        <nav class="nav-bar">
            <div class="logo">CabEase</div>
        </nav>

        <div class="hero-content">
            <h1>Book Smarter. Ride Faster.</h1>
            <p>Experience CabEase.</p>
            <div class="hero-buttons">
                <a href="login.jsp"><button class="button">Book a Cab</button></a>
                <a href="aiassistant.jsp"><button class="button accent">Talk to AI Assistant</button></a>
            </div>
        </div>

        <footer class="footer">
            <p>&copy; 2025 CabEase. All Rights Reserved.</p>
        </footer>
    </div>

</body>
</html>
