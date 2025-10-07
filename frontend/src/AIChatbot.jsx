import React, { useState } from 'react';

function AIChatbot() {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const API_KEY = 'YOUR_API_KEY'; // Replace with your actual Gemini API key

    const handleSend = async () => {
        if (!input.trim()) return;

        const userMessage = { sender: 'user', text: input };
        setMessages(prev => [...prev, userMessage]);

        const prompt = `You are a cab booking assistant. The user said: "${input}". If the user is asking to book a cab, extract pickup location, drop location, and distance. Respond in a conversational manner.`;

        try {
            const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=${API_KEY}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ contents: [{ parts: [{ text: prompt }] }] }),
            });

            if (!response.ok) {
                throw new Error('API request failed');
            }

            const data = await response.json();
            const aiText = data.candidates[0].content.parts[0].text;
            const aiMessage = { sender: 'ai', text: aiText };
            setMessages(prev => [...prev, aiMessage]);

        } catch (error) {
            console.error('Error fetching from Gemini API:', error);
            const errorMessage = { sender: 'ai', text: 'Sorry, I am having trouble connecting. Please try again.' };
            setMessages(prev => [...prev, errorMessage]);
        }

        setInput('');
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSend();
        }
    };

    return (
        <div className="bg-gray-800 p-6 rounded-lg shadow-lg w-full">
            <h2 className="text-2xl font-bold mb-4 text-center">AI Assistant</h2>
            <div className="h-80 bg-gray-700 rounded-md p-4 overflow-y-auto flex flex-col space-y-4">
                {messages.map((msg, index) => (
                    <div key={index} className={`flex ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
                        <div className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${msg.sender === 'user' ? 'bg-blue-600' : 'bg-gray-600'}`}>
                            <p className="text-white">{msg.text}</p>
                        </div>
                    </div>
                ))}
            </div>
            <div className="mt-4 flex">
                <input
                    type="text"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyPress={handleKeyPress}
                    placeholder="Ask to book a cab..."
                    className="flex-grow p-3 bg-gray-700 rounded-l-md border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button onClick={handleSend} className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-r-md transition duration-300">
                    Send
                </button>
            </div>
        </div>
    );
}

export default AIChatbot;
