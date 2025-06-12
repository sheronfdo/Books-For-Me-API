# Books-For-Me-API
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/sheronfdo/Books-For-Me-API.git)

Books For Me API is a backend service built with Spring Boot, designed to power an e-commerce platform for books. It provides functionalities for customers, sellers, and potentially administrators. The API interacts with Firebase for data storage (Firestore), seller authentication (Firebase Authentication), and real-time notifications (Firebase Cloud Messaging).

## Features

*   **User Management:**
    *   **Customers:** Registration (using UID from client-side Firebase Auth), profile updates.
    *   **Sellers:** Registration with Firebase Authentication, profile updates, address management (including geolocation), business registration details, and profile image uploads. Sellers can be individuals or companies.
*   **Book Catalog:**
    *   Adding new books with comprehensive details (title, author, ISBN, publisher, category, description, cover image, publication year, language, tags).
*   **Book Stock Management:**
    *   Sellers can add and manage their book stock, including quantity, price, and condition for each book they sell.
*   **Order Processing:**
    *   Customers can place orders.
    *   Order lifecycle management including payment status updates (pending, completed, cancelled, failed) and order item status updates (e.g., confirmed, approved, processing, shipped, delivered).
    *   Automatic stock deduction upon order approval.
*   **Notifications:**
    *   Real-time push notifications to customers and sellers regarding order status changes using Firebase Cloud Messaging.
*   **API Design:**
    *   RESTful API endpoints.
    *   Standardized JSON request and response formats.
    *   Global exception handling for robust error reporting.
    *   Request validation for DTOs.

## Technologies Used

*   **Backend:** Java 17, Spring Boot 3.3.8
*   **Database:** Google Firebase Firestore
*   **Authentication:** Google Firebase Authentication (for sellers)
*   **Notifications:** Google Firebase Cloud Messaging (FCM)
*   **Build Tool:** Apache Maven
*   **Utilities:**
    *   Lombok (for reducing boilerplate code)
    *   ModelMapper (for object mapping between DTOs and Entities)
    *   Jakarta Bean Validation (for request data validation)

## Prerequisites

*   JDK 17 or later installed.
*   Apache Maven 3.x installed (or use the included Maven Wrapper).
*   A Google Firebase project with the following services enabled:
    *   Firestore
    *   Firebase Authentication (with Email/Password sign-in provider enabled for sellers)
    *   Firebase Cloud Messaging
*   Firebase Admin SDK JSON key file.

## Setup and Configuration

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sheronfdo/Books-For-Me-API.git
    cd Books-For-Me-API
    ```

2.  **Firebase Configuration:**
    *   Go to your Firebase project settings in the Firebase console.
    *   Navigate to "Service accounts".
    *   Click on "Generate new private key" and download the JSON file.
    *   Rename this downloaded JSON file to `books-for-me-firebase-adminsdk.json`.
    *   Place this `books-for-me-firebase-adminsdk.json` file in the `src/main/resources/` directory of the project.

3.  **Build the project:**
    You can use the Maven Wrapper included in the project or your globally installed Maven.

    *   Using Maven Wrapper (recommended):
        *   On Linux/macOS: `./mvnw clean install`
        *   On Windows: `mvnw.cmd clean install`
    *   Using global Maven: `mvn clean install`

## Running the Application

*   **Using Maven Wrapper:**
    *   On Linux/macOS: `./mvnw spring-boot:run`
    *   On Windows: `mvnw.cmd spring-boot:run`
*   **Using an IDE:**
    *   Import the project as a Maven project into your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code with Java extensions).
    *   Locate and run the `com.jamith.booksformeapi.BooksForMeApiApplication` class.

The application will start, and by default, the API will be accessible at `http://localhost:8080`.

## API Endpoints

The API provides several endpoints grouped by resource:

### Customer API (`/api/customer`)

*   `POST /register`: Registers a new customer. Expects details like UID (from client-side Firebase Auth), display name, email, etc.
*   `POST /updateProfile`: Updates an existing customer's profile.

### Seller API (`/api/seller`)

*   `POST /register`: Registers a new seller (individual or company). Creates a Firebase Auth user for the seller.
*   `POST /address`: Sets or updates the seller's address, including latitude and longitude.
*   `POST /brdetails`: Sets or updates the seller's business registration details (for company-type sellers).
*   `POST /image`: Sets or updates the seller's profile image.
*   `POST /updateProfile`: Updates general profile information for an existing seller.

### Book API (`/api/book`)

*   `POST /addNewBook`: Adds a new book to the platform's catalog.
*   `POST /addNewBookStock`: Allows a seller to add stock for a specific book, including price, quantity, and condition.

### Order API (`/api/order`)

*   `POST /makeorder`: Allows a customer to place a new order with items from their cart.
*   `POST /paymentStatus`: Updates the payment status of an existing order (e.g., completed, failed, cancelled).
*   `POST /orderStatus`: Allows updating the status of individual items within an order (e.g., approved, shipped, delivered) by sellers.

## Project Structure

```
.
├── .mvn/                   # Maven wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/jamith/booksformeapi/
│   │   │       ├── BooksForMeApiApplication.java  # Spring Boot main application class
│   │   │       ├── advice/              # Global exception handlers (GlobalExceptionHandler)
│   │   │       ├── config/              # Application configuration (FirebaseConfig)
│   │   │       ├── controller/          # REST API controllers (BookController, CustomerController, etc.)
│   │   │       ├── dto/                 # Data Transfer Objects (for API requests and responses)
│   │   │       ├── entity/              # Data model classes representing Firestore documents
│   │   │       ├── enums/               # Enumerations (OrderStatus, PaymentStatus, SellerType, UserRole)
│   │   │       ├── service/             # Business logic interfaces and implementations
│   │   │       └── utils/               # Utility classes (DateUtil, ResponseUtil, RequestValidationUtils)
│   │   └── resources/
│   │       ├── application.properties   # Spring Boot application configuration properties
│   │       └── books-for-me-firebase-adminsdk.json # Firebase service account key (YOU NEED TO ADD THIS)
│   └── test/                   # Test classes
├── mvnw                    # Maven wrapper script (Linux/macOS)
├── mvnw.cmd                # Maven wrapper script (Windows)
├── pom.xml                 # Maven project object model: dependencies and build configuration
└── README.md               # This file
```

## Configuration Details

*   **`application.properties`**: Contains basic Spring Boot application settings, like the application name.
*   **`FirebaseConfig.java`**: Initializes the Firebase Admin SDK using the `books-for-me-firebase-adminsdk.json` service account key.
*   **Firebase Service Account Key**: Crucial for the application to communicate with Firebase services. Ensure it's correctly placed in `src/main/resources/` and named `books-for-me-firebase-adminsdk.json`.

This API serves as the core backend for the "Books For Me" platform, handling critical operations for users and book management.
