# 🚖 CabEase - Modern Cab Booking Platform# CabEase - Cab Booking System



[![Java](https://img.shields.io/badge/Java-23-orange.svg)](https://openjdk.org/)CabEase is a comprehensive web-based cab booking system built with Spring Boot, Thymeleaf, and Bootstrap. It provides a seamless experience for users to book cabs, manage bookings, and for administrators to manage the fleet.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.17-brightgreen.svg)](https://spring.io/projects/spring-boot)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)## Features



CabEase is a full-stack cab booking platform built with Spring Boot, featuring real-time ride tracking, secure payments, and an intuitive user interface. Designed for scalability and ease of use, it offers a complete solution for ride-hailing services.### User Features

- User registration and authentication

## ✨ Key Features- Browse and book available cabs

- View booking history

### 👤 User Experience- Submit feedback for completed rides

- **Smart Booking System** - Quick cab booking with real-time availability- Real-time booking status updates

- **Live Ride Tracking** - Track your driver in real-time with WebSocket integration- AI-powered chatbot for assistance

- **Multiple Vehicle Types** - Choose from Micro, Mini, Sedan, SUV, and Premium options

- **Secure Payments** - Integrated payment gateway with multiple payment options### Admin Features

- **Ride Scheduling** - Book rides in advance with flexible scheduling- Cab and driver management

- **Rating & Feedback** - Rate drivers and provide feedback after rides- View all bookings and user feedback

- **Trip History** - View detailed history of all your bookings- System administration dashboard



### 🔧 Admin Dashboard### Technical Features

- **Fleet Management** - Manage cabs, drivers, and vehicle assignments- Secure authentication with Spring Security

- **Analytics Dashboard** - Track bookings, revenue, and performance metrics- Email notifications for booking confirmations and cancellations

- **User Management** - Monitor user activity and manage accounts- Responsive web design with Bootstrap

- **Real-time Monitoring** - Live view of all active rides and driver locations- RESTful API endpoints

- Unit testing with JUnit and Mockito

### 🛠️ Technical Highlights

- **Real-time Updates** - WebSocket-based live tracking and notifications## Technology Stack

- **Secure Authentication** - Spring Security with BCrypt password encryption

- **Email Notifications** - Automated booking confirmations and updates- **Backend**: Spring Boot, Spring Security, Spring Data JPA

- **Responsive Design** - Mobile-friendly UI built with Bootstrap 5- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript

- **RESTful APIs** - Well-documented REST endpoints for all operations- **Database**: H2 (for development), configurable for production

- **Database Migrations** - Flyway-based version-controlled schema management- **Build Tool**: Maven

- **Docker Support** - Containerized deployment with Docker Compose- **Testing**: JUnit 5, Mockito



## 🏗️ Technology Stack## Prerequisites



### Backend- Java 11 or higher

- **Framework**: Spring Boot 2.7.17- Maven 3.6+

- **Security**: Spring Security with JWT- Git

- **ORM**: Hibernate / Spring Data JPA

- **Build Tool**: Maven 3.8+## Getting Started

- **Template Engine**: Thymeleaf

1. **Clone the repository**

### Frontend   ```bash

- **UI Framework**: Bootstrap 5   git clone https://github.com/yourusername/cabease.git

- **JavaScript**: ES6+ with AJAX   cd cabease

- **Real-time**: WebSocket (STOMP)   ```

- **Maps Integration**: Google Maps API

2. **Build the project**

### Database   ```bash

- **Primary**: PostgreSQL 15   mvn clean install

- **Development**: H2 (In-memory)   ```



### DevOps3. **Run the application**

- **Containerization**: Docker & Docker Compose   ```bash

- **Version Control**: Git   mvn spring-boot:run

- **Testing**: JUnit 5, Mockito   ```



## 🚀 Quick Start4. **Access the application**

   - Open your browser and go to `http://localhost:8080`

### Prerequisites   - Register a new account or login with existing credentials

- Java 23 or higher

- Maven 3.8+## Configuration

- PostgreSQL 15+ (or Docker)

- GitThe application uses `application.properties` for configuration. Key settings include:



### Installation- Database configuration

- Email service settings

1. **Clone the repository**- Security settings

   ```bash- AI service API keys

   git clone https://github.com/Tejo0507/CabEase.git

   cd CabEase## API Endpoints

   ```

- `GET /home` - Home page

2. **Setup Database (Option 1: Docker)**- `GET /login` - Login page

   ```bash- `POST /register` - User registration

   docker-compose up -d- `GET /dashboard` - User/Admin dashboard

   ```- `GET /bookings` - List user bookings

- `POST /bookings` - Create new booking

3. **Setup Database (Option 2: Local PostgreSQL)**- `GET /admin/cabs` - Admin cab management

   ```sql- `POST /api/chat` - Chatbot API

   CREATE DATABASE cabease;

   CREATE USER cabease WITH PASSWORD 'cabease_pass';## Project Structure

   GRANT ALL PRIVILEGES ON DATABASE cabease TO cabease;

   ``````

src/

4. **Configure Application**├── main/

   │   ├── java/com/cabease/

   Update `src/main/resources/application.properties`:│   │   ├── config/          # Security and configuration

   ```properties│   │   ├── controllers/     # Web controllers

   spring.datasource.url=jdbc:postgresql://localhost:5432/cabease│   │   ├── models/          # JPA entities

   spring.datasource.username=cabease│   │   ├── repository/      # Data repositories

   spring.datasource.password=cabease_pass│   │   ├── services/        # Business logic

   │   │   └── CabEaseApplication.java

   # Email Configuration (Optional)│   ├── resources/

   spring.mail.username=your-email@gmail.com│   │   ├── static/          # CSS, JS files

   spring.mail.password=your-app-password│   │   ├── templates/       # Thymeleaf templates

   │   │   └── application.properties

   # Google Maps API Key (Optional)│   └── webapp/              # Web resources (if needed)

   google.maps.api.key=your-api-key└── test/

   ```    └── java/com/cabease/    # Unit tests

```

5. **Build and Run**

   ```bash## Testing

   mvn clean install

   mvn spring-boot:runRun the tests with:

   ``````bash

mvn test

6. **Access the Application**```

   

   Open your browser and navigate to: `http://localhost:8080`## Contributing



### Default Credentials1. Fork the repository

```2. Create a feature branch

User Account:3. Commit your changes

Email: user@example.com4. Push to the branch

Password: password1235. Create a Pull Request



Admin Account:## License

Email: admin@example.com

Password: admin123This project is licensed under the MIT License - see the LICENSE file for details.

```

## Support

## 📁 Project Structure

For support, email support@cabease.com or create an issue in the repository.

```
CabEase/
├── src/
│   ├── main/
│   │   ├── java/com/cabease/
│   │   │   ├── config/              # Configuration classes
│   │   │   ├── controllers/         # REST & Web controllers
│   │   │   ├── models/              # JPA entities
│   │   │   ├── repository/          # Data repositories
│   │   │   ├── services/            # Business logic
│   │   │   ├── security/            # Security configuration
│   │   │   └── CabEaseApplication.java
│   │   └── resources/
│   │       ├── static/              # CSS, JS, images
│   │       ├── templates/           # Thymeleaf templates
│   │       ├── db/migration/        # Flyway migrations
│   │       └── application.properties
│   └── test/                        # Unit & integration tests
├── scripts/                         # Utility scripts
├── database/                        # Database files
├── docker/                          # Docker configurations
├── pom.xml                          # Maven configuration
└── README.md
```

## 🔌 API Documentation

### Authentication
```http
POST /api/auth/register
POST /api/auth/login
```

### Bookings
```http
GET    /api/bookings              # List all bookings
POST   /api/bookings              # Create new booking
GET    /api/bookings/{id}         # Get booking details
PUT    /api/bookings/{id}/cancel  # Cancel booking
```

### Cabs
```http
GET    /api/cabs                  # List available cabs
GET    /api/cabs/nearby           # Find nearby cabs
GET    /api/cabs/{id}             # Get cab details
```

### Admin
```http
GET    /api/admin/dashboard       # Dashboard statistics
GET    /api/admin/cabs            # Manage cabs
POST   /api/admin/cabs            # Add new cab
PUT    /api/admin/cabs/{id}       # Update cab
DELETE /api/admin/cabs/{id}       # Delete cab
```

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

Run with coverage:
```bash
mvn test jacoco:report
```

## 🐳 Docker Deployment

Build and run with Docker:
```bash
docker-compose up -d
```

Stop services:
```bash
docker-compose down
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Tejo Sridhar M V S**
- GitHub: [@Tejo0507](https://github.com/Tejo0507)

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- Bootstrap team for the responsive UI framework
- Contributors and testers who helped improve this project

---

⭐ If you find this project useful, please consider giving it a star!
