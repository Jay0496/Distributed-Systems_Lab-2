package Task3.FileApp.Server;

import Task3.FileApp.FileInterface;
import Task3.FileApp.FileServiceException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the remote interface.
 *
 * Note: this class does NOT extend UnicastRemoteObject. It is made
 * remote by FileServer, which calls UnicastRemoteObject.exportObject().
 */
public class FileImpl implements FileInterface {

    private final Path serverDir = Path.of("Task3/FileApp/Server/");
    @Override
    public byte[] downloadFile(String fileName) throws FileServiceException {
        String clientHost = "Unknown";
        try {
            clientHost = RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            System.err.println("Could not get client host: " + e.getMessage());
        }

        System.out.println("\nClient connected from IP/Host: " + clientHost);
        System.out.println("Requested file: '" + fileName + "'");

        Path filePath = serverDir.resolve(fileName);

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            System.err.println("FileImpl error: File '" + fileName + "' does not exist on server.");
            throw new FileServiceException("File '" + fileName + "' was not found on the server.");
        }

        try {
            byte[] data = Files.readAllBytes(Path.of("Task3/FileApp/Server/" + fileName));
            System.out.println("Successfully served file: '" + fileName + "' (" + data.length + " bytes)");
            return data;
        } catch (IOException e) {
            System.out.println("FileImpl: could not read '" + fileName + "': " + e);
            return null;
        }
    }

    @Override
    public List<String> listFiles() throws FileServiceException {
        String clientHost = "Unknown";
        try {
            clientHost = RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            System.err.println("Could not get client host: " + e.getMessage());
        }

        System.out.println("\nClient connected from IP/Host: " + clientHost + " requested file list.");

        File folder = serverDir.toFile();
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        } else {
            throw new FileServiceException("Unable to read server directory.");
        }

        System.out.println("Returned " + fileNames.size() + " files to client.");
        return fileNames;
    }
}
