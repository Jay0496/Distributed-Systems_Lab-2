package Task2.FileApp.Server;

import Task2.FileApp.FileInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

/**
 * Implementation of the remote interface.
 *
 * Note: this class does NOT extend UnicastRemoteObject. It is made
 * remote by FileServer, which calls UnicastRemoteObject.exportObject().
 */
public class FileImpl implements FileInterface {

    @Override
    public byte[] downloadFile(String fileName) {
        String clientHost = "Unknown";
        try {
            clientHost = RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            System.err.println("Could not get client host: " + e.getMessage());
        }

        System.out.println("\nClient connected from IP/Host: " + clientHost);
        System.out.println("Requested file: '" + fileName + "'");

        try {
            byte[] data = Files.readAllBytes(Path.of("Task2/FileApp/Server/" + fileName));
            System.out.println("Successfully served file: '" + fileName + "' (" + data.length + " bytes)");
            return data;
        } catch (IOException e) {
            System.out.println("FileImpl: could not read '" + fileName + "': " + e);
            return null;
        }
    }
}
