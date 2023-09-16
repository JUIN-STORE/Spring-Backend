package store.juin.api.common.exception;

public class JUINIOException extends RuntimeException {
    public JUINIOException(){
        super();
    }
    public JUINIOException(String message){
        super(message);
    }
    public JUINIOException(String message, Throwable cause){
        super(message,cause);
    }
    public JUINIOException(Throwable cause){
        super(cause);
    }
}
