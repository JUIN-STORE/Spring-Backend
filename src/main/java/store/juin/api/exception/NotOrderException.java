package store.juin.api.exception;

public class NotOrderException extends RuntimeException{
    public NotOrderException(){
        super();
    }
    public NotOrderException(String message){
        super(message);
    }
    public NotOrderException(Throwable cause){
        super(cause);
    }
    public NotOrderException(String message, Throwable cause){
        super(message,cause);
    }
}