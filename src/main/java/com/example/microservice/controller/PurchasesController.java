package com.example.microservice.controller;

import com.example.microservice.dto.PurchaseDTO;
import com.example.microservice.profiler.ControllerActionMetrics;
import com.example.microservice.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by bruno on 03-11-2016.
 */
@RestController
@RequestMapping(path = "/api/v1/purchases")
public class PurchasesController {

    @Autowired
    private PurchaseService purchaseService;

    /**
     * List all valid purchases
     * @return lis of all valid purchases (not expired)
     */
    @ControllerActionMetrics
    @RequestMapping(path = "/", method=GET, produces = "application/json")
    public List<PurchaseDTO> index() {
        return purchaseService.getAllValidPurchases();
    }

    /**
     *
     * @param purchase purchaseDTO model and valid against rules defined in class
     * @return purchase id
     */
    @ControllerActionMetrics
    @RequestMapping(path = "/", method=POST, produces = "application/json")
    public Long insertOrChange(@Valid @RequestBody PurchaseDTO purchase) {
        return purchaseService.insertOrUpdate(purchase);
    }
}
