package com.juin.store.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(){
        super();
    }
    public AccountNotFoundException(String message){
        super(message);
    }
    public AccountNotFoundException(String message, Throwable cause){
        super(message,cause);
    }
    public AccountNotFoundException(Throwable cause){
        super(cause);
    }
}
