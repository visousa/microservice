package com.example.microservice.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bruno on 05-11-2016.
 */

public class Purchase implements Serializable {
    private Long id;
    private String productType;
    private Date expires;

    transient private Details purchaseDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Details getPurchaseDetails() {
        return purchaseDetails;
    }

    public void setPurchaseDetails(Details purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }

}