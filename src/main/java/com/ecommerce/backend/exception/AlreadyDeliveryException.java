package com.ecommerce.backend.exception;

public class AlreadyDeliveryException extends RuntimeException{
    public AlreadyDeliveryException(){
        super();
    }
    public AlreadyDeliveryException(String message){
        super(message);
    }
    public AlreadyDeliveryException(Throwable cause){
        super(cause);
    }
    public AlreadyDeliveryException(String message, Throwable cause){
        super(message,cause);
    }
}
