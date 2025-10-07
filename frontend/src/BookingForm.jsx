import React, { useState } from 'react';

function BookingForm() {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        phone: '',
        pickup: '',
        drop: '',
        distance: '',
        cabType: 'Mini'
    });
    const [bookingResult, setBookingResult] = useState(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevState => ({ ...prevState, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setBookingResult(null);
        try {
            const response = await fetch('http://localhost:8080/api/book', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),
            });

            const result = await response.json();

            if (response.ok) {
                const { message, bookingDetails } = result;
                const details = bookingDetails ? `\nUser: ${bookingDetails.user.name}\nCab: ${bookingDetails.cab.cabType}\nFare: $${bookingDetails.fare}` : '';
                setBookingResult({ type: 'success', message: `${message}${details}` });
            } else {
                setBookingResult({ type: 'error', message: result.message || 'Booking failed.' });
            }
        } catch (error) {
            setBookingResult({ type: 'error', message: 'An error occurred. Please try again.' });
        }
    };

    return (
        <div className="bg-gray-800 p-8 rounded-lg shadow-lg w-full">
            <h2 className="text-2xl font-bold mb-6 text-center">Book Your Cab</h2>
            <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <input type="text" name="name" placeholder="Name" value={formData.name} onChange={handleChange} className="w-full p-3 bg-gray-700 rounded-md border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
                    <input type="email" name="email" placeholder="Email" value={formData.email} onChange={handleChange} className="w-full p-3 bg-gray-700 rounded-md border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
                    <input type="tel" name="phone" placeholder="Phone" value={formData.phone} onChange={handleChange} className="w-full p-3 bg-gray-700 rounded-md border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
                    <input type="text" name="pickup" placeholder="Pickup Location" value={formData.pickup} onChange={handleChange} className="w-full p-3 bg-gray-700 rounded-md border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
                    <input type="text" name="drop" placeholder="Drop Location" value={formData.drop} onChange={handleChange} className="w-full p-3 bg-gray-700 rounded-md border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
                    <input type="number" name="distance" placeholder="Distance (km)" value={formData.distance} onChange={handleChange} className="w-full p-3 bg-gray-700 rounded-md border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500" required />
                </div>
                <select name="cabType" value={formData.cabType} onChange={handleChange} className="w-full p-3 bg-gray-700 rounded-md border border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option>Mini</option>
                    <option>Sedan</option>
                    <option>SUV</option>
                </select>
                <button type="submit" className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-md transition duration-300">Book Now</button>
            </form>
            {bookingResult && (
                <div className={`mt-6 p-4 rounded-md ${bookingResult.type === 'success' ? 'bg-green-500' : 'bg-red-500'} text-white whitespace-pre-wrap`}>
                    {bookingResult.message}
                </div>
            )}
        </div>
    );
}

export default BookingForm;
