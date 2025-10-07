<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>CabEase AI Assistant</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        body {
            display: flex;
            flex-direction: column;
            height: 100vh;
            margin: 0;
            background-color: #F5F7FA;
        }
        .chat-container {
            display: flex;
            flex-direction: column;
            flex-grow: 1;
            width: 100%;
            max-width: 800px;
            margin: 0 auto;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            border-radius: 15px;
            overflow: hidden;
        }
        .chat-header {
            background-color: #1E88E5;
            color: white;
            padding: 15px;
            text-align: center;
        }
        .chat-header h1 {
            margin: 0;
            font-size: 24px;
            animation: glow 1.5s infinite alternate;
        }
        @keyframes glow {
            from {
                text-shadow: 0 0 5px #fff, 0 0 10px #fff, 0 0 15px #FFC107;
            }
            to {
                text-shadow: 0 0 10px #fff, 0 0 20px #FFC107, 0 0 30px #FFC107;
            }
        }
        .chat-area {
            flex-grow: 1;
            padding: 20px;
            overflow-y: auto;
            background-color: #F5F7FA;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }
        .message {
            padding: 10px 15px;
            border-radius: 20px;
            max-width: 70%;
            line-height: 1.5;
        }
        .user-message {
            background-color: #1E88E5;
            color: white;
            align-self: flex-end;
            border-bottom-right-radius: 5px;
        }
        .ai-message {
            background-color: #FFFFFF;
            color: #333;
            align-self: flex-start;
            border: 1px solid #E0E0E0;
            border-bottom-left-radius: 5px;
        }
        .chat-input-bar {
            display: flex;
            padding: 15px;
            background-color: #FFFFFF;
            border-top: 1px solid #E0E0E0;
        }
        #userInput {
            flex-grow: 1;
            border: 1px solid #ccc;
            border-radius: 20px;
            padding: 10px 15px;
            font-size: 16px;
            outline: none;
            transition: border-color 0.3s;
        }
        #userInput:focus {
            border-color: #1E88E5;
        }
        #chatForm button {
            margin-left: 10px;
            border-radius: 50%;
            width: 45px;
            height: 45px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    </style>
</head>
<body>

<div class="chat-container">
    <div class="chat-header">
        <h1>CabEase AI Assistant</h1>
    </div>
    <div class="chat-area" id="chatArea">
        <div class="message ai-message">Hello! How can I assist you with your booking today?</div>
    </div>
    <div class="chat-input-bar">
        <form id="chatForm" style="width:100%; display:flex;">
            <input type="text" id="userInput" name="message" placeholder="Type your message..." autocomplete="off">
            <button type="submit">&#10148;</button>
        </form>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const chatForm = document.getElementById('chatForm');
        const userInput = document.getElementById('userInput');
        const chatArea = document.getElementById('chatArea');

        chatForm.addEventListener('submit', function (event) {
            event.preventDefault();
            const messageText = userInput.value.trim();
            if (!messageText) return;

            appendMessage(messageText, 'user');
            userInput.value = '';

            fetch('ai-chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'message=' + encodeURIComponent(messageText)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok: ' + response.statusText);
                }
                return response.text();
            })
            .then(aiResponse => {
                appendMessage(aiResponse, 'ai');
            })
            .catch(error => {
                console.error('Fetch error:', error);
                appendMessage('Sorry, I am having trouble connecting. Please try again later.', 'ai');
            });
        });

        function appendMessage(text, sender) {
            const messageDiv = document.createElement('div');
            messageDiv.textContent = text;
            messageDiv.className = 'message ' + (sender === 'user' ? 'user-message' : 'ai-message');
            chatArea.appendChild(messageDiv);
            chatArea.scrollTop = chatArea.scrollHeight;
        }
    });
</script>

</body>
</html>