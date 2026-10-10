package Task4.FileApp;

/**
 * Custom exception thrown when a file service operation fails (e.g., file not found).
 */
public class FileServiceException extends Exception {

    public FileServiceException(String message) {
        super(message);
    }

    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
