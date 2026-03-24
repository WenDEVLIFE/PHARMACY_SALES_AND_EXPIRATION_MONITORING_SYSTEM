package com.mycompany.pharmacy_sales_and_expiration_monitoring_system.services;

import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.models.SaleReturn;
import com.mycompany.pharmacy_sales_and_expiration_monitoring_system.repositories.ReturnRepository;

import java.sql.SQLException;

public class ReturnService {
    private final ReturnRepository returnRepository = new ReturnRepository();

    public boolean processReturn(int saleId, int productId, int quantity, String reason) throws SQLException {
        SaleReturn saleReturn = new SaleReturn(saleId, productId, quantity, reason);
        return returnRepository.processReturn(saleReturn);
    }
}
