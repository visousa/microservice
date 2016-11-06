package com.example.microservice.service;

import com.example.microservice.dto.PurchaseDTO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 * Created by bruno on 05-11-2016.
 */
@Service
public class PurchaseDTOFactoryImpl implements PurchaseDTOFactory {
    static final private Random random = new Random();
    static final private String allowedCharacters = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ ";
    public static String generateString(int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = allowedCharacters.charAt(random.nextInt(allowedCharacters.length()));
        }
        return new String(text);
    }

    private PurchaseDTO createBasePurchaseDTO() {
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setDescription(generateString(40+random.nextInt(20)));
        purchaseDTO.setProductType(generateString(40+random.nextInt(20)));
        purchaseDTO.setQuantity(random.nextInt());
        purchaseDTO.setValue(random.nextDouble());
        return purchaseDTO;
    }

    @Override
    public PurchaseDTO createNotExpiredPurchaseDTO() {
        PurchaseDTO purchaseDTO = createBasePurchaseDTO();
        int hoursDiff = 60 + Math.abs(random.nextInt(100)); /* more 1 hour at least from now*/
        Date expireDate = new Date(new Date().getTime()+1000*60*60*hoursDiff);
        purchaseDTO.setExpires(expireDate);
        return purchaseDTO;
    }

    @Override
    public PurchaseDTO createExpiredPurchaseDTO() {
        PurchaseDTO purchaseDTO = createBasePurchaseDTO();
        int hoursDiff = 60 + Math.abs(random.nextInt(100)); /* minus 1 hour at least from now*/

        Date expireDate = new Date(new Date().getTime()-1000*60*60*hoursDiff) ;
        purchaseDTO.setExpires(expireDate);
        return purchaseDTO;
    }

}
