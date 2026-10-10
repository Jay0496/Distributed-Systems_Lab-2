package Task4.FileApp.Server;

import Task4.FileApp.FileInterface;
import Task4.FileApp.FileServiceException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of the remote interface.
 *
 * Note: this class does NOT extend UnicastRemoteObject. It is made
 * remote by FileServer, which calls UnicastRemoteObject.exportObject().
 */
public class FileImpl implements FileInterface {

    private final Path serverDir = Path.of("Task4/FileApp/Server/");

    // Shared State 1: Thread-safe global counter for total uploads
    private final AtomicInteger uploadCounter = new AtomicInteger(0);

    // Shared State 2: Per-filename lock pool to prevent file corruption during concurrent same-file uploads
    private final ConcurrentHashMap<String, ReentrantLock> fileLocks = new ConcurrentHashMap<>();


    public FileImpl() {
        try {
            Files.createDirectories(serverDir);
        } catch (IOException e) {
            System.err.println("Could not create server directory: " + e.getMessage());
        }
    }

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
            byte[] data = Files.readAllBytes(Path.of("Task4/FileApp/Server/" + fileName));
            System.out.println("Successfully served file: '" + fileName + "' (" + data.length + " bytes)");
            return data;
        } catch (IOException e) {
            System.out.println("FileImpl: could not read '" + fileName + "': " + e);
            throw new FileServiceException("Failed to read file '" + fileName + "' on the server.", e);
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

    @Override
    public void uploadFile(String fileName, byte[] content) throws FileServiceException, RemoteException {
        String clientHost = getClientHost();
        String threadName = Thread.currentThread().getName();

        System.out.println("\n>>> [" + threadName + "] Starting upload from client (" + clientHost + "): '" + fileName + "' (" + content.length + " bytes)");

        // Obtain or compute a lock specific to this filename
        ReentrantLock lock = fileLocks.computeIfAbsent(fileName, k -> new ReentrantLock());
        lock.lock();

        try {
            // Simulate slight IO processing time to easily demonstrate interleaved concurrent calls
            Thread.sleep(800);

            Path targetPath = serverDir.resolve(fileName);
            Files.write(targetPath, content);

            int currentTotal = uploadCounter.incrementAndGet();

            System.out.println("<<< [" + threadName + "] Upload finished for: '" + fileName + "'. Server Total Uploads Count: " + currentTotal);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FileServiceException("Upload interrupted on server.");
        } catch (IOException e) {
            System.err.println("Upload failed for '" + fileName + "': " + e.getMessage());
            throw new FileServiceException("Failed to write uploaded file on server.", e);
        } finally {
            lock.unlock();
        }
    }

    private String getClientHost() {
        try {
            return RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            return "Unknown";
        }
    }
}
