package com.example.microservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception should be used to throw inconsistencies on DAO
 * Created by bruno on 05-11-2016.
 */
@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="System have problem")
public class InconsistencyDAOException extends RuntimeException {
}
