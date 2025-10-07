export const bookCab = async (data) => {
    try {
        const response = await fetch('http://localhost:8080/api/book', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            console.error('Booking failed with status:', response.status);
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('An error occurred during the booking request:', error);
        return null;
    }
};
