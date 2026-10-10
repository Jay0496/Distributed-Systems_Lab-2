package Task4.FileApp.Client;

import Task4.FileApp.FileInterface;
import Task4.FileApp.FileServiceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Downloads a file from a FileServer.
 *
 * Usage: java FileClient fileName host [port] //download
 *   fileName  file to download (path as seen by the server)
 *   host      machine running the server, e.g. localhost or 192.168.1.20
 *   port      registry port (default 1099)
 *
 * Usage: java FileClient <host> [port] //list
 *   host      machine running the server, e.g. localhost or 192.168.1.20
 *   port      registry port (default 1099)
 */
public class FileClient {

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            return;
        }

        String action = args[0].toLowerCase();

        if (action.equals("list")) {
            String host = args[1];
            int port = (args.length > 2) ? Integer.parseInt(args[2]) : Registry.REGISTRY_PORT;
            handleList(host, port);
        } else if (action.equals("download")) {
            if (args.length < 3) {
                System.out.println("Error: Missing filename for download.");
                printUsage();
                return;
            }
            String fileName = args[1];
            String host = args[2];
            int port = (args.length > 3) ? Integer.parseInt(args[3]) : Registry.REGISTRY_PORT;
            handleDownload(fileName, host, port);
        } else if (action.equals("upload")) {
            if (args.length < 3) {
                System.out.println("Error: Missing filename for upload.");
                printUsage();
                return;
            }
            String fileName = args[1];
            String host = args[2];
            int port = (args.length > 3) ? Integer.parseInt(args[3]) : Registry.REGISTRY_PORT;
            handleUpload(fileName, host, port);
        } else {
            System.err.println("Unknown command: " + action);
            printUsage();
        }
    }
    private static void handleList(String host, int port) {
        try {
            FileInterface fi = getStub(host, port);
            System.out.println("--- Available Files on Server ---");
            List<String> files = fi.listFiles();
            for (String file : files) {
                System.out.println(" - " + file);
            }
            System.out.println("----------------------------------");
        } catch (Exception e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }

    private static void handleDownload(String fileName, String host, int port) {
        try {
            // 1. Locate the registry on the server machine and look up the stub.
            FileInterface fi = getStub(host, port);
            byte[] data = fi.downloadFile(fileName);

            Path clientDir = Path.of("Task4/FileApp/Client/");
            Files.createDirectories(clientDir);
            Path target = clientDir.resolve(Path.of(fileName).getFileName().toString());

            Files.write(target, data);
            System.out.println("\nFile downloaded successfully!");
            System.out.println("File Name: " + Path.of(fileName).getFileName().toString());
            System.out.println("Size: " + data.length + " bytes");
        } catch (FileServiceException e) {
            System.err.println("File not found on server: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("FileClient error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleUpload(String fileName, String host, int port) {
        try {
            Path clientDir = Path.of("Task4/FileApp/Client/");
            Path filePath = clientDir.resolve(fileName);

            if (!Files.exists(filePath)) {
                System.err.println("Client Error: Local file '" + filePath + "' not found to upload.");
                return;
            }

            byte[] content = Files.readAllBytes(filePath);
            FileInterface fi = getStub(host, port);

            fi.uploadFile(fileName, content);
            System.out.println("Upload successful for '" + fileName + "'!");
            System.out.println("File Name: " + Path.of(fileName).getFileName().toString());
            System.out.println("Size: " + content.length + " bytes");

        } catch (FileServiceException e) {
            System.err.println("[Server Error]: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[Client IO Error]: Could not read local file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[Client/RMI Error]: " + e.getMessage());
        }
    }

    private static FileInterface getStub(String host, int port) throws Exception {
        Registry registry = LocateRegistry.getRegistry(host, port);
        return (FileInterface) registry.lookup("FileServer");
    }

    private static void printUsage() {
        System.out.println("\nUsage:");
        System.out.println("  java Task4.FileApp.Client.FileClient list <host> [port]");
        System.out.println("  java Task4.FileApp.Client.FileClient download <fileName> <host> [port]");
        System.out.println("  java Task4.FileApp.Client.FileClient upload <fileName> <host> [port]");
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
