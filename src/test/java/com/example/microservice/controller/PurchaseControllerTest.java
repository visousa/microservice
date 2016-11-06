package com.example.microservice.controller;

import com.example.microservice.Main;
import com.example.microservice.dao.PurchaseDAO;
import com.example.microservice.dto.PurchaseDTO;
import com.example.microservice.exceptions.RecordNotFoundException;
import com.example.microservice.model.Details;
import com.example.microservice.model.Purchase;
import com.example.microservice.service.PurchaseDTOFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by bruno on 05-11-2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes={Main.class})
public class PurchaseControllerTest {

    @MockBean
    private PurchaseDAO purchaseDAO;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseDTOFactory purchaseDTOFactory;

    /**
     * Test the empty purchase list
     * @throws Exception
     */
    @Test
    public void testEmptyPurchaseList() throws Exception {
        given(purchaseDAO.allPurchases()).willReturn(new ArrayList<Purchase>());

        this.mockMvc.perform(get("/api/v1/purchases/")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }


    /**
     * Test the purchase list with no purchase expired
     * @throws Exception
     */
    @Test
    public void testNotExpiredPurchaseList() throws Exception {

        final PurchaseDTO purchaseDTO = purchaseDTOFactory.createNotExpiredPurchaseDTO();
        purchaseDTO.setId(1L);
        final Purchase purchase = purchaseDTO.convertToPurchase();
        given(purchaseDAO.allPurchases()).willReturn(new ArrayList<Purchase>(){{
            add(purchase);
        }});

        given(purchaseDAO.getDetails(new ArrayList<Long>(){{
            add(purchase.getId());
        }})).willReturn(new ArrayList<Details>(){{
            add(purchase.getPurchaseDetails());
        }});

        // dummy test to detect if purchase is in list
        this.mockMvc.perform(get("/api/v1/purchases/")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":" + purchaseDTO.getId())));
    }

    /**
     * Test insert purchase
     * @throws Exception
     */
    @Test
    public void testInsertPurchase() throws Exception {

        final PurchaseDTO purchaseDTO = purchaseDTOFactory.createNotExpiredPurchaseDTO();

        final Purchase purchase = purchaseDTO.convertToPurchase();

        //set return id from inserted purchase
        when(purchaseDAO.insert(any(Purchase.class), any(Details.class))).thenReturn(1L);


        this.mockMvc.perform(
                post("/api/v1/purchases/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(purchaseDTO))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("1"));

        verify(purchaseDAO, times(1)).insert(any(Purchase.class), any(Details.class));
        verify(purchaseDAO, times(0)).update(any(Purchase.class), any(Details.class));
    }

    /**
     * Test update purchase
     * @throws Exception
     */
    @Test
    public void testUpdatePurchase() throws Exception {

        final PurchaseDTO purchaseDTO = purchaseDTOFactory.createNotExpiredPurchaseDTO();
        purchaseDTO.setId(1L);
        final Purchase purchase = purchaseDTO.convertToPurchase();

        //set return id from inserted purchase
        when(purchaseDAO.update(any(Purchase.class), any(Details.class))).thenReturn(purchaseDTO.getId());

        this.mockMvc.perform(
                post("/api/v1/purchases/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(purchaseDTO))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(purchaseDTO.getId().toString()));

        verify(purchaseDAO, times(0)).insert(any(Purchase.class), any(Details.class));
        verify(purchaseDAO, times(1)).update(any(Purchase.class), any(Details.class));
    }

    /**
     * Test update invalid purchase
     * @throws Exception
     */
    @Test
    public void testUpdateNotFoundPurchase() throws Exception {

        final PurchaseDTO purchaseDTO = purchaseDTOFactory.createNotExpiredPurchaseDTO();
        purchaseDTO.setId(1L);
        final Purchase purchase = purchaseDTO.convertToPurchase();

        //set return id from inserted purchase
        when(purchaseDAO.update(any(Purchase.class), any(Details.class))).thenThrow(new RecordNotFoundException());

        this.mockMvc.perform(
                post("/api/v1/purchases/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(purchaseDTO))
        )
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(purchaseDAO, times(0)).insert(any(Purchase.class), any(Details.class));
        verify(purchaseDAO, times(1)).update(any(Purchase.class), any(Details.class));
    }




}