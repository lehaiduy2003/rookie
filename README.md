# Assignment: E-commerce Website

## Project Description
This project is an e-commerce web application built using **Java**, **Spring Boot**, and **Maven**. It provides functionality for both customers and administrators. The application demonstrates the use of various techniques and includes unit tests to showcase testing capabilities.
<br/>
**ERD**: https://dbdocs.io/lehaiduy2003/rookie-assignment
---

## Features

### For Customers:
- **Home Page**: Displays a category menu and featured products.
- **View Products by Category**: Browse products based on their categories.
- **Product Details**: View detailed information about a product, including:
    - Name
    - Images
    - Description
    - Price
    - Average Rating
- **Rate a Product**: Submit a rating for a product.
- **User Management**:
    - Register
    - Login/Logout
- **Optional Features**:
    - Shopping Cart
    - Ordering

### For Admin:
- **Authentication**:
    - Login/Logout (Admin role required)
- **Category Management**:
    - Manage product categories (Name, Description)
- **Product Management**:
    - Manage products with the following attributes:
        - Name
        - Category
        - Description
        - Price
        - Images
        - IsFeatured
        - CreatedOn
        - LastUpdatedOn
- **Customer Management**:
    - Manage customer details:
        - Email
        - First Name
        - Last Name
        - CreatedOn
        - LastUpdatedOn

---

## Technologies Used
- **Backend**: Java, Spring Boot
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Testing**: JUnit, Mockito
- **Other Tools**: Lombok, PMD, Checkstyle

---

## Project Requirements
- Apply as many trained techniques as possible.
- Include unit tests for common components and services (high coverage is not mandatory but should demonstrate testing skills).

---

## Architecture
The project follows a CQRS layered architecture:
1. **Controller Layer**: Handles HTTP requests and responses.
2. **Service Layer**: Contains business logic.
3. **Repository Layer**: Manages database interactions.
4. **Entity Layer**: Defines database models.
5. **DTOs**: Used for data transfer between layers.

---

## Scoring Criteria
1. **Commit Quality (10%)**:
    - Frequent commits with meaningful messages.
    - Clean and organized commit history.
2. **Functionality (60%)**:
    - Application is runnable and meets all requirements.
3. **Beyond Functionality (30%)**:
    - Good UI/UX layout.
    - Clean and maintainable code.
    - Rich features beyond the minimum requirements.

---

## How to Run the Project
1. Clone the repository.
2. Configure the database in `src/main/resources/application.properties`:
    - Update `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` with your PostgreSQL credentials.
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
5. Access the application at `http://localhost:8081`.

---

## Testing
Run unit tests using Maven:
```bash
mvn test
```

---

## License
This project is licensed under the MIT License.