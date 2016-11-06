package com.example.microservice.dto;

import com.example.microservice.model.Details;
import com.example.microservice.model.Purchase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by bruno on 05-11-2016.
 */
public class PurchaseDTO implements Serializable {

    private Long id;

    @NotEmpty
    @Length(max=255)
    private String productType;

    @NotNull
    private Date expires;


    @NotEmpty
    @Length(max = 255)
    private String description;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double value;



    /**
     * Empty constructor - allow create a instance without nothind
     */
    public PurchaseDTO(){
    }

    /**
     * Constructor from a internal purchase
     */
    public PurchaseDTO(Purchase purchase){
        setId(purchase.getId());
        setExpires(purchase.getExpires());
        setProductType(purchase.getProductType());
        Details details = purchase.getPurchaseDetails();
        if(details != null){
            setQuantity(details.getQuantity());
            setValue(details.getValue());
            setDescription(details.getDescription());
        }
    }

    /**
     * this method revert the purchaseExposed in inter Purchase representation also with the details instance
     * @return internal purchase model wih details
     */
    @JsonIgnore //ignore field to serialize
    public Purchase convertToPurchase(){

        Purchase purchase = new Purchase();
        purchase.setId(getId());
        purchase.setExpires(getExpires());
        purchase.setProductType(getProductType());

        Details details = new Details();
        details.setId(getId());
        details.setQuantity(getQuantity());
        details.setValue(getValue());
        details.setDescription(getDescription());

        purchase.setPurchaseDetails(details);
        return purchase;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
