# Project Agents and Architecture

This document describes the structural "agents" (modules) and the system "agents" (user roles) within the Pharmacy Sales and Expiration Monitoring System.

## Technical Agents (Architecture Layers)

Based on the required project structure, the following layers act as technical agents responsible for specific domains:

### 1. Repositories (Data Agent)
- **Responsibility**: Interface with the MySQL database.
- **Key Components**: `UserRepository`, `ProductRepository`, `TransactionRepository`, `SupplierRepository`.
- **Function**: Performs CRUD operations and handles SQL queries.

### 2. Services (Business Logic Agent)
- **Responsibility**: Orchestrates business rules and coordinates between Repositories and UI.
- **Key Components**: `AuthenticationService`, `SalesService`, `InventoryService`.
- **Function**: Validates transactions, checks for expiration alerts, and generates reports.

### 3. UI (View/Interaction Agent)
- **Responsibility**: Handles user interactions and displays data via JavaFX.
- **Key Components**: FXML files and their respective Controllers.
- **Function**: Transitions between screens, captures user input, and provides visual feedback.

### 4. Utils (Utility Agent)
- **Responsibility**: Provides cross-cutting concerns and helper functions.
- **Key Components**: `DatabaseConnection`, `AlertHelper`, `DateFormatter`.
- **Function**: Manages the JDBC connection pool and formats data for display.

---

## System Agents (User Roles)

The system supports two primary user agents with distinct permissions:

### 1. Administrator
- **Full Control**: Can manage products, suppliers, and users.
- **Reporting**: Access to comprehensive sales summaries and inventory audits.
- **System Settings**: Configures system-wide parameters.

### 2. Cashier
- **Sales Focus**: Primarily responsible for processing transactions and generating receipts.
- **Inventory View**: Can check product availability and expiration dates but has limited editing permissions.
- **End-of-Day Logs**: Submits daily sales logs for admin review.
