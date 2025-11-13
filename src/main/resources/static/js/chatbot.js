document.addEventListener('DOMContentLoaded', function() {
    const chatbotToggle = document.getElementById('chatbot-toggle');
    const chatbotWindow = document.getElementById('chatbot-window');
    const chatMessages = document.getElementById('chat-messages');
    const chatInput = document.getElementById('chat-input');
    
    // Car-themed responses for enhanced user experience
    const carResponses = {
        greetings: [
            "🚗 Welcome to CabEase! I'm your premium driving assistant. How can I help you today?",
            "🏎️ Rev up your experience with CabEase! What destination are we cruising to?",
            "🚕 Your luxury ride awaits! Let me help you book the perfect cab."
        ],
        booking: [
            "🚙 Let's get you moving! I can help you book a cab in seconds.",
            "🚗 Ready to hit the road? Tell me your pickup location and I'll find the best ride for you.",
            "🏁 Time to start your journey! Where would you like to go today?"
        ],
        pricing: [
            "💰 Our pricing is as smooth as our ride! Premium service at competitive rates.",
            "🎯 We offer transparent pricing with no hidden charges - just pure driving excellence!",
            "⚡ Our rates are designed to give you maximum value for your premium travel experience."
        ],
        safety: [
            "🛡️ Your safety is our top priority! All our drivers are verified and vehicles are regularly maintained.",
            "🔒 Travel with confidence - our cabs are equipped with GPS tracking and safety features.",
            "👨‍✈️ Our professional drivers undergo thorough background checks and safety training."
        ],
        default: [
            "🚗 I'm here to assist with your cab booking needs. Ask me about booking, pricing, or safety!",
            "🎯 That's an interesting question! Let me help you with CabEase services.",
            "🏎️ I'm your driving companion! Feel free to ask about our premium cab services."
        ]
    };

    chatbotToggle.addEventListener('click', function() {
        chatbotWindow.classList.toggle('d-none');
    });

    chatInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });

    function sendMessage() {
        const message = chatInput.value.trim();
        if (message === '') return;

        // Add user message
        addMessage(message, 'user-message');
        chatInput.value = '';

        // Send to server
        fetch('/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(message),
        })
        .then(response => response.text())
        .then(data => {
            addMessage(data, 'bot-message');
        })
        .catch(error => {
            console.error('Error:', error);
            // Fallback to local car-themed responses
            const response = getCarThemedResponse(message);
            addMessage(response, 'bot-message');
        });
    }

    function addMessage(text, className) {
        const messageDiv = document.createElement('div');
        messageDiv.className = className;
        chatMessages.appendChild(messageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;

        if (className === 'bot-message') {
            typeWriter(messageDiv, text);
        } else {
            messageDiv.textContent = text;
        }
    }

    function typeWriter(element, text) {
        let i = 0;
        element.textContent = '';
        const timer = setInterval(() => {
            if (i < text.length) {
                element.textContent += text.charAt(i);
                i++;
                chatMessages.scrollTop = chatMessages.scrollHeight;
            } else {
                clearInterval(timer);
            }
        }, 50);
    }
    
    // Enhanced car-themed response system
    function getCarThemedResponse(message) {
        const msg = message.toLowerCase();
        
        if (msg.includes('hello') || msg.includes('hi') || msg.includes('hey')) {
            return getRandomResponse(carResponses.greetings);
        } else if (msg.includes('book') || msg.includes('ride') || msg.includes('cab') || msg.includes('taxi')) {
            return getRandomResponse(carResponses.booking);
        } else if (msg.includes('price') || msg.includes('cost') || msg.includes('fare') || msg.includes('rate')) {
            return getRandomResponse(carResponses.pricing);
        } else if (msg.includes('safe') || msg.includes('security') || msg.includes('trust')) {
            return getRandomResponse(carResponses.safety);
        } else {
            return getRandomResponse(carResponses.default);
        }
    }
    
    function getRandomResponse(responses) {
        return responses[Math.floor(Math.random() * responses.length)];
    }
    
    // Initialize with a welcome message
    setTimeout(() => {
        addMessage(getRandomResponse(carResponses.greetings), 'bot-message');
    }, 1000);
});
