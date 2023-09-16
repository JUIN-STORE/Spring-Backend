package store.juin.api.item.enumeration;

public class NotEnoughStockException extends RuntimeException{
    public NotEnoughStockException(){
        super();
    }
    public NotEnoughStockException(String message){
        super(message);
    }
    public NotEnoughStockException(Throwable cause){
        super(cause);
    }
    public NotEnoughStockException(String message, Throwable cause){
        super(message,cause);
    }
}
