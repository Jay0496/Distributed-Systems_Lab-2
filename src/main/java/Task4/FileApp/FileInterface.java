package Task4.FileApp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for the file service.
 * Every method a client can call remotely must be declared here
 * and must throw RemoteException.
 */
public interface FileInterface extends Remote {
    /**
     * Returns the contents of the named file.
     * @throws FileServiceException if the file cannot be found or read.
     * @throws RemoteException if an RMI communication failure occurs.
     */
    byte[] downloadFile(String fileName) throws FileServiceException, RemoteException;

    /**
     * Returns a list of file names available on the server.
     * @throws FileServiceException if listing files fails.
     * @throws RemoteException if an RMI communication failure occurs.
     */
    List<String> listFiles() throws FileServiceException, RemoteException;

    /**
     * Uploads a file to the server.
     * @param fileName Name of the file to save on the server.
     * @param content Byte contents of the file.
     * @throws FileServiceException if the upload or disk write fails.
     * @throws RemoteException if an RMI error occurs.
     */
    void uploadFile(String fileName, byte[] content) throws FileServiceException, RemoteException;
}
