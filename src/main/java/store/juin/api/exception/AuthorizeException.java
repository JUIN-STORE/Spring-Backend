package store.juin.api.exception;

public class AuthorizeException extends RuntimeException {
    public AuthorizeException(){
        super();
    }
    public AuthorizeException(String message){
        super(message);
    }
    public AuthorizeException(Throwable cause){
        super(cause);
    }
    public AuthorizeException(String message, Throwable cause){
        super(message,cause);
    }
}
