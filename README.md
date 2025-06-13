# IRCTC-style Train Ticket Booking System 🚆

An online full-stack application that simulates a train ticket booking platform like IRCTC. It allows users to register, log in, search trains, book tickets, cancel bookings, and check PNR status.

---

## 📌 Features

- 🔐 User Registration & Login (JWT-based Authentication)
- 🚆 Train Search by source, destination, and date
- 🎫 Book Ticket (class-wise seat availability)
- ❌ Cancel Ticket
- 📄 Check PNR Status
- 🧾 Admin panel to add/manage trains (Optional)

---

## 🛠️ Technologies Used

### Backend (Spring Boot)
- Java 17+
- Spring Boot
- Spring Data JPA + Hibernate
- Spring Security + JWT
- MySQL Database

### Frontend (React)
- ReactJS
- Bootstrap
- Axios

---

## 🗃️ Database Schema

- **User**: Stores user credentials and roles
- **Train**: Contains train details and seat availability
- **Booking**: Stores booking records with PNR
- **Passenger**: Linked to bookings
- **SeatClass**: Tracks seats by class type (Sleeper, AC3, AC2, AC1)

---

