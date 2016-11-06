package com.example.microservice.service;

import com.example.microservice.dto.PurchaseDTO;

/**
 * Created by bruno on 05-11-2016.
 */
public interface PurchaseDTOFactory {

    PurchaseDTO createNotExpiredPurchaseDTO();
    PurchaseDTO createExpiredPurchaseDTO();

}
