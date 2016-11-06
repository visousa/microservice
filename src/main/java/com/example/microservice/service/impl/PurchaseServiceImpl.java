package com.example.microservice.service.impl;

import com.codahale.metrics.Timer;
import com.example.microservice.dao.PurchaseDAO;
import com.example.microservice.dto.PurchaseDTO;
import com.example.microservice.exceptions.InconsistencyDAOException;
import com.example.microservice.exceptions.InvalidParametersException;
import com.example.microservice.metric.MetricsService;
import com.example.microservice.model.Details;
import com.example.microservice.model.Purchase;
import com.example.microservice.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by bruno on 05-11-2016.
 */
@Service("purchaseService")
public class PurchaseServiceImpl implements PurchaseService{

    @Autowired
    private MetricsService metricsService;
    /**
     * Set the purchase DAO implementation
     */
    @Autowired
    PurchaseDAO dao;

    /**
     * get all purchases with details valid at the moment
     * @return list of all purchases with details
     */
    @Override
    public List<PurchaseDTO> getAllValidPurchases() {
        //get date for now
        Date now = new Date();


        Timer.Context allPurchasesTimer = metricsService.getTimer(
                dao.getClass().getCanonicalName() + ".allPurchases").time();
        List<Purchase> allPurchases = dao.allPurchases();
        allPurchasesTimer.close();

        Iterator<Purchase> iter = allPurchases.iterator();

        // store index of purchases to direct assign details and to direct get all valid ids
        Map<Long, Purchase> indexedPurchases = new HashMap<Long, Purchase>();

        while (iter.hasNext()){
            Purchase purchase = iter.next();
            //this code can be on DAO layer
            //test if purchase have id
            if(purchase.getId() == null){
                throw new InconsistencyDAOException();
            }
            // if purchase already expired remove it or if expires is null (means never expire)
            if(purchase.getExpires() != null && now.after(purchase.getExpires())) {
                iter.remove();
            }else{
                //this code can be on DAO layer
                //test of the purchase id is duplicated
                if(indexedPurchases.containsKey(purchase.getId())){
                    throw new InconsistencyDAOException();
                }else{
                    //index purchases
                    indexedPurchases.put(purchase.getId(), purchase);
                }
            }
        }

        //if doesn't have any valid purchase end the method
        if(indexedPurchases.size() == 0)
            return new ArrayList<PurchaseDTO>();

        //fetch details about the valid purchases
        final Timer.Context allDetailsTimer = metricsService.getTimer(
                dao.getClass().getCanonicalName() + ".getDetails").time();
        List<Details> fetchedDetails = dao.getDetails(new ArrayList<Long>(indexedPurchases.keySet()));
        allDetailsTimer.close();

        //assuming every Purchase have details
        //this code can be on DAO layer
        //assert if the size is the same has request
        if(fetchedDetails.size() != indexedPurchases.size()){
            throw new InconsistencyDAOException();
        }

        //this code can be on DAO layer
        // set of ids get on details (should be used to test if have duplicates)
        Set<Long> usedIds = new HashSet<Long>();

        //put details on the right purchases
        Iterator<Details> fetchedDetailsIter = fetchedDetails.iterator();
        while(fetchedDetailsIter.hasNext()){
            Details details = fetchedDetailsIter.next();

            //test if the details is to an requested purchase
            if(!indexedPurchases.containsKey(details.getId()))
                throw new InconsistencyDAOException();
            //detected if have duplicates
            if(usedIds.contains(details.getId())){
                throw new InconsistencyDAOException();
            }else{
                usedIds.add(details.getId());
            }
            indexedPurchases.get(details.getId()).setPurchaseDetails(details);
        }

        ArrayList<PurchaseDTO> availablePurchases = new ArrayList<PurchaseDTO>();
        Iterator<Purchase> purchaseIterator = indexedPurchases.values().iterator();
        while (purchaseIterator.hasNext()) {
            availablePurchases.add(
                    new PurchaseDTO(purchaseIterator.next())
            );
        }
        return availablePurchases;
    }

    /**
     * Insert purchase
     * @param purchaseDTO (purchase Data transfer object)
     * @return id of purchase
     */
    private Long insert(PurchaseDTO purchaseDTO) {
        Purchase purchase = purchaseDTO.convertToPurchase();
        Details details = purchase.getPurchaseDetails();
        //enforcement validation if doesn't have id
        if(purchase.getId() != null || details.getId() != null){
            throw new InvalidParametersException();
        }
        return dao.insert(purchase, details);
    }

    /**
     * Update purchase
     * @param purchaseDTO (purchase Data transfer object)
     * @return id of purchase
     */
    private Long update(PurchaseDTO purchaseDTO) {
        Purchase purchase = purchaseDTO.convertToPurchase();
        Details details = purchase.getPurchaseDetails();
        //enforcement validation if have id
        if(purchase.getId() == null || details.getId() == null){
            throw new InvalidParametersException();
        }
        return dao.update(purchase, details);
    }


    /**
     * Insert o update purchase
     * @param purchaseDTO (purchase Data transfer object)
     * @return id of purchase
     */
    @Override
    public Long insertOrUpdate(PurchaseDTO purchaseDTO) {
        Long result;
        if(purchaseDTO.getId() != null){
            final Timer.Context updateTimer = metricsService.getTimer(dao.getClass().getCanonicalName() + ".update").time();
            result = update(purchaseDTO);
            updateTimer.close();
        }else{
            final Timer.Context insertTimer = metricsService.getTimer(dao.getClass().getCanonicalName() + ".insert").time();
            result = insert(purchaseDTO);
            insertTimer.close();
        }
        return result;
    }
}