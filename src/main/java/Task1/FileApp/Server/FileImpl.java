package Task1.FileApp.Server;

import Task1.FileApp.FileInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of the remote interface.
 *
 * Note: this class does NOT extend UnicastRemoteObject. It is made
 * remote by FileServer, which calls UnicastRemoteObject.exportObject().
 */
public class FileImpl implements FileInterface {

    @Override
    public byte[] downloadFile(String fileName) {
        try {
            return Files.readAllBytes(Path.of("Task1/FileApp/Server/" + fileName));
        } catch (IOException e) {
            System.out.println("FileImpl: could not read '" + fileName + "': " + e);
            return null;
        }
    }
}
