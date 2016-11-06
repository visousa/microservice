package com.example.microservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Used to validate has Id on not on Purchase Service
 * Created by bruno on 05-11-2016.
 */

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="You have a problem with your parameters")
public class InvalidParametersException extends RuntimeException {
}
