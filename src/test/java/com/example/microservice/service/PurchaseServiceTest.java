package com.example.microservice.service;

import com.example.microservice.Main;
import com.example.microservice.dao.PurchaseDAO;
import com.example.microservice.dto.PurchaseDTO;
import com.example.microservice.model.Details;
import com.example.microservice.model.Purchase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Created by bruno on 05-11-2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={Main.class})
public class PurchaseServiceTest {

    @MockBean
    private PurchaseDAO purchaseDAO;

    @Autowired
    public PurchaseService purchaseService;

    private PurchaseDTOFactory purchaseDTOFactory = new PurchaseDTOFactoryImpl();

    /**
     * Test list all available purchases with empty list
     * @throws Exception
     */
    @Test
    public void testPurchaseServiceClean() throws Exception {
        given(purchaseDAO.allPurchases()).willReturn(new ArrayList<Purchase>());
        assertThat(purchaseService.getAllValidPurchases().size()).isEqualTo(0);
    }

    /**
     * Test list all available purchases with an not expired purchase
     * @throws Exception
     */
    @Test
    public void testPurchaseServiceNotExpired() throws Exception {
        final PurchaseDTO purchaseDTO = purchaseDTOFactory.createNotExpiredPurchaseDTO();
        //set id manually on purchase
        purchaseDTO.setId(1L);
        given(purchaseDAO.allPurchases()).willReturn(new ArrayList<Purchase>(){{
            add(purchaseDTO.convertToPurchase());
        }});
        given(purchaseDAO.getDetails(new ArrayList<Long>(){{add(purchaseDTO.getId());}}))
        .willReturn(new ArrayList<Details>(){{
            add(purchaseDTO.convertToPurchase().getPurchaseDetails());
        }});
        assertThat(purchaseService.getAllValidPurchases().size()).isEqualTo(1);
    }


    /**
     * Test list all available purchases with an expired purchase
     * @throws Exception
     */
    @Test
    public void testPurchaseServiceExpired() throws Exception {
        final PurchaseDTO purchaseDTO = purchaseDTOFactory.createExpiredPurchaseDTO();
        //set id manually on purchase
        purchaseDTO.setId(1L);
        given(purchaseDAO.allPurchases()).willReturn(new ArrayList<Purchase>(){{
            add(purchaseDTO.convertToPurchase());
        }});
        given(purchaseDAO.getDetails(new ArrayList<Long>(){{add(purchaseDTO.getId());}}))
        .willReturn(new ArrayList<Details>(){{
            add(purchaseDTO.convertToPurchase().getPurchaseDetails());
        }});
        assertThat(purchaseService.getAllValidPurchases().size()).isEqualTo(0);
    }

    /**
     * Test list all available purchases with an not expired and expired purchase
     * @throws Exception
     */
    @Test
    public void testPurchaseServiceExpiredAndNotExpired() throws Exception {
        final PurchaseDTO purchaseDTONoExpired = purchaseDTOFactory.createNotExpiredPurchaseDTO();
        final PurchaseDTO purchaseDTOExpired = purchaseDTOFactory.createExpiredPurchaseDTO();
        //set id manually on purchase
        purchaseDTOExpired.setId(1L);
        purchaseDTONoExpired.setId(2L);

        given(purchaseDAO.allPurchases()).willReturn(new ArrayList<Purchase>(){{
            add(purchaseDTOExpired.convertToPurchase());
            add(purchaseDTONoExpired.convertToPurchase());
        }});

        // should only ask about the not expired purchase
        given(purchaseDAO.getDetails(new ArrayList<Long>(){{
            add(purchaseDTONoExpired.getId());
        }})).willReturn(new ArrayList<Details>(){{
            add(purchaseDTONoExpired.convertToPurchase().getPurchaseDetails());
        }});

        assertThat(purchaseService.getAllValidPurchases().size()).isEqualTo(1);
    }

    /**
     * Test insert purchase (detect if service call the right action)
     * @throws Exception
     */
    @Test
    public void testPurchaseInsert() throws Exception {

        final PurchaseDTO purchaseDTONoExpired = purchaseDTOFactory.createNotExpiredPurchaseDTO();

        //mock the object to get the same purchase and details and continue testing
        final PurchaseDTO purchaseDTOmock = mock(PurchaseDTO.class);

        given(purchaseDTOmock.convertToPurchase()).willReturn(purchaseDTONoExpired.convertToPurchase());
        given(purchaseDTOmock.getId()).willReturn(null);


        final Purchase purchase = purchaseDTOmock.convertToPurchase();
        final Details details = purchase.getPurchaseDetails();


        given(purchaseDAO.insert(purchase, details)).willReturn(1L);

        Long id = purchaseService.insertOrUpdate(purchaseDTOmock);

        //verify the service chose the right method
        verify(purchaseDAO, times(1)).insert(purchase, details);
        verify(purchaseDAO, times(0)).update(purchase, details);

        //set ids (emulate DAO)
        purchase.setId(id);
        details.setId(id);

        given(purchaseDAO.allPurchases()).willReturn(new ArrayList<Purchase>(){{
            add(purchase);
        }});

        given(purchaseDAO.getDetails(new ArrayList<Long>(){{
            add(purchase.getId());
        }})).willReturn(new ArrayList<Details>(){{
            add(details);
        }});

        assertThat(purchaseService.getAllValidPurchases().size()).isEqualTo(1);
    }

    /**
     * Test update purchase (detect if service call the right action)
     * @throws Exception
     */
    @Test
    public void testPurchaseUpdate() throws Exception {

        final PurchaseDTO purchaseDTONoExpired = purchaseDTOFactory.createNotExpiredPurchaseDTO();
        purchaseDTONoExpired.setId(1L);
        //mock the object to get the same purchase and details and continue testing
        final PurchaseDTO purchaseDTOmock = mock(PurchaseDTO.class);

        given(purchaseDTOmock.convertToPurchase()).willReturn(purchaseDTONoExpired.convertToPurchase());
        given(purchaseDTOmock.getId()).willReturn(1L);


        final Purchase purchase = purchaseDTOmock.convertToPurchase();
        final Details details = purchase.getPurchaseDetails();


        given(purchaseDAO.update(purchase, details)).willReturn(1L);

        Long id = purchaseService.insertOrUpdate(purchaseDTOmock);

        //verify the service chose the right method
        verify(purchaseDAO, times(0)).insert(purchase, details);
        verify(purchaseDAO, times(1)).update(purchase, details);

        //set ids (emulate DAO)
        purchase.setId(id);
        details.setId(id);

        given(purchaseDAO.allPurchases()).willReturn(new ArrayList<Purchase>(){{
            add(purchase);
        }});

        given(purchaseDAO.getDetails(new ArrayList<Long>(){{
            add(purchase.getId());
        }})).willReturn(new ArrayList<Details>(){{
            add(details);
        }});

        assertThat(purchaseService.getAllValidPurchases().size()).isEqualTo(1);
    }

}