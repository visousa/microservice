package com.example.microservice.dao.impl;

import com.example.microservice.dao.PurchaseDAO;
import com.example.microservice.exceptions.InconsistencyDAOException;
import com.example.microservice.exceptions.RecordNotFoundException;
import com.example.microservice.model.Details;
import com.example.microservice.model.Purchase;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is dummy implementation of PurchaseDAO
 * Created by bruno on 05-11-2016.
 */
@Repository("purchaseDAO")
public class PurchaseDAOImpl implements PurchaseDAO{

    /**
     * Sequence generator and thread-safe, should have the state of the last id inserted
     */
    private static AtomicLong sequenceGenerator = new AtomicLong();

    /**
     * storage to all purchases
     */
    private List<Purchase> purchases = Collections.synchronizedList(new ArrayList<Purchase>());
    /**
     * storage to all details
     */
    private List<Details> details = Collections.synchronizedList(new ArrayList<Details>());

    /**
     * Get list of all purchases
     * @return list of all purchases
     */
    @Override
    public List<Purchase> allPurchases() {
        return new ArrayList<Purchase>(this.purchases);
    }

    /**
     * Get list of all details filtered by purchase ids
     * @return list filtered details
     */
    @Override
    public List<Details> getDetails(List<Long> purchasesIds) {
        ArrayList<Details> result = new ArrayList<Details>();
        Iterator<Details> iter = this.details.iterator();
        while (iter.hasNext()){
            Details details = iter.next();
            if(purchasesIds.contains(details.getId())){
                result.add(details);
                /**
                 * break if reach all purchasesIds
                 */
                if(result.size() == purchasesIds.size()){
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Insert purchase and details
     * @param purchase the purchase instance
     * @param details the details instance
     * @return id of the new purchase
     */
    @Override
    public Long insert(Purchase purchase, Details details) {
        synchronized (PurchaseDAO.class) {
            purchase.setId(sequenceGenerator.incrementAndGet());
            details.setId(purchase.getId());
            this.purchases.add(purchase);
            this.details.add(details);
        }
        return purchase.getId();
    }

    /**
     * Update purchase and details
     * @param purchase the purchase instance
     * @param details the details instance
     * @return id of purchase
     */
    @Override
    public Long update(Purchase purchase, Details details) {
        if(purchase.getId() > PurchaseDAOImpl.sequenceGenerator.get()){
            throw new RecordNotFoundException();
        }

        if(purchase.getId() != details.getId()){
            throw new InconsistencyDAOException();
        }
        Long id = purchase.getId();
        synchronized (this) {
            if(this.purchases.get(id.intValue()-1).getId() != id){
                throw new InconsistencyDAOException();
            }
            this.purchases.set(purchase.getId().intValue()-1, purchase);

            if(this.details.get(id.intValue()-1).getId() != id){
                throw new InconsistencyDAOException();
            }
            this.details.set(details.getId().intValue()-1, details);
        }
        return purchase.getId();
    }
}
