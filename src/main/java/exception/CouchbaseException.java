package exception;

public class CouchbaseException extends RuntimeException{
    public CouchbaseException(String message) {
        super(message);
    }
    public CouchbaseException(String message, Throwable throwable) {
        super(message,throwable);
    }
}
