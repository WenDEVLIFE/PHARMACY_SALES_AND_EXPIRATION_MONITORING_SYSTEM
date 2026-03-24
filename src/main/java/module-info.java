module com.mycompany.pharmacy_sales_and_expiration_monitoring_system {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.pharmacy_sales_and_expiration_monitoring_system to javafx.fxml;
    exports com.mycompany.pharmacy_sales_and_expiration_monitoring_system;
}
