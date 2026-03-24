module com.mycompany.pharmacy_sales_and_expiration_monitoring_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;

    opens com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui to javafx.fxml;
    opens com.mycompany.pharmacy_sales_and_expiration_monitoring_system to javafx.fxml;

    exports com.mycompany.pharmacy_sales_and_expiration_monitoring_system;
    exports com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models;
    exports com.mycompany.pharmacy_sales_and_expiration_monitoring_system.ui;
    exports com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services;
    exports com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories;
    exports com.mycompany.pharmacy_sales_and_expiration_monitoring_system.utils;
}
