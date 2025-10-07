import React from 'react';
import BookingForm from './BookingForm';
import AIChatbot from './AIChatbot';

function App() {
    return (
        <div className="bg-gray-900 min-h-screen flex flex-col items-center p-4 text-white">
            <div className="w-full max-w-4xl">
                <h1 className="text-4xl font-bold text-center my-8">Cab Booking Management System</h1>
                <div className="space-y-8">
                    <BookingForm />
                    <AIChatbot />
                </div>
            </div>
        </div>
    );
}

export default App;
