package com.example.microservice.service;

import com.example.microservice.dto.PurchaseDTO;

import java.util.List;

/**
 * Created by bruno on 05-11-2016.
 */
public interface PurchaseService {

    /**
     * get all purchases with details valid at the moment
     * @return list of all purchases with details
     */
    List<PurchaseDTO> getAllValidPurchases();

    /**
     * Insert o update purchase
     * @param purchaseDTO (purchase Data transfer object)
     * @return id of purchase
     */
    Long insertOrUpdate(PurchaseDTO purchaseDTO);

}
