package com.example.microservice.dao;

import com.example.microservice.model.Details;
import com.example.microservice.model.Purchase;

import java.util.List;

/**
 * Created by bruno on 05-11-2016.
 */
public interface PurchaseDAO {
    /**
     * List of all purchases
     * @return list of all purchases
     */
    List<Purchase> allPurchases();

    /**
     * Get details to a list of purchases Ids
     * @param purchasesIds list of purchases ids
     * @return list of details to all purchases ids
     */
    List<Details> getDetails(List<Long> purchasesIds);

    /**
     * Insert a purchase - should be an transaction
     * @param purchase the purchase instance
     * @param details the details instance
     * @return new id
     */
    Long insert(Purchase purchase, Details details);

    /**
     * Update a purchase - should be an transaction
     * @param purchase the purchase instance
     * @param details the details instance
     * @return the id of purchase updated
     */
    Long update(Purchase purchase, Details details);
}