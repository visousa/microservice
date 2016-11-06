package com.example.microservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * To raise not found record
 * Created by bruno on 05-11-2016.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Record")
public class RecordNotFoundException extends RuntimeException {
}
