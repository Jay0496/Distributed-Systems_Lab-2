package Task4.FileApp;

/**
 * Task 3 Requirement: Custom exception class for the file service.
 * Extends Exception and implements Serializable (via Exception hierarchy)
 * so it can be serialized across the network from server to client.
 */
public class FileServiceException extends Exception {

    public FileServiceException(String message) {
        super(message);
    }

    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
