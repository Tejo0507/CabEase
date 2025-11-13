# CabEase - Cab Booking System

CabEase is a comprehensive web-based cab booking system built with Spring Boot, Thymeleaf, and Bootstrap. It provides a seamless experience for users to book cabs, manage bookings, and for administrators to manage the fleet.

## Features

### User Features
- User registration and authentication
- Browse and book available cabs
- View booking history
- Submit feedback for completed rides
- Real-time booking status updates
- AI-powered chatbot for assistance

### Admin Features
- Cab and driver management
- View all bookings and user feedback
- System administration dashboard

### Technical Features
- Secure authentication with Spring Security
- Email notifications for booking confirmations and cancellations
- Responsive web design with Bootstrap
- RESTful API endpoints
- Unit testing with JUnit and Mockito

## Technology Stack

- **Backend**: Spring Boot, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Database**: H2 (for development), configurable for production
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Git

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/cabease.git
   cd cabease
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Open your browser and go to `http://localhost:8080`
   - Register a new account or login with existing credentials

## Configuration

The application uses `application.properties` for configuration. Key settings include:

- Database configuration
- Email service settings
- Security settings
- AI service API keys

## API Endpoints

- `GET /home` - Home page
- `GET /login` - Login page
- `POST /register` - User registration
- `GET /dashboard` - User/Admin dashboard
- `GET /bookings` - List user bookings
- `POST /bookings` - Create new booking
- `GET /admin/cabs` - Admin cab management
- `POST /api/chat` - Chatbot API

## Project Structure

```
src/
├── main/
│   ├── java/com/cabease/
│   │   ├── config/          # Security and configuration
│   │   ├── controllers/     # Web controllers
│   │   ├── models/          # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── services/        # Business logic
│   │   └── CabEaseApplication.java
│   ├── resources/
│   │   ├── static/          # CSS, JS files
│   │   ├── templates/       # Thymeleaf templates
│   │   └── application.properties
│   └── webapp/              # Web resources (if needed)
└── test/
    └── java/com/cabease/    # Unit tests
```

## Testing

Run the tests with:
```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, email support@cabease.com or create an issue in the repository.
