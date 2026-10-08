package Task3.FileApp.Client;

import Task3.FileApp.FileInterface;
import Task3.FileApp.FileServiceException;

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
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  List files:     java FileClient <host> [port]");
            System.out.println("  Download file:  java FileClient <fileName> <host> [port]");
            return;
        }

        // Determine mode based on argument count
        boolean isListOnly = (args.length == 1) || (args.length == 2 && isInteger(args[1]));

        String fileName = isListOnly ? null : args[0];
        String host = isListOnly ? args[0] : args[1];
        int port = Registry.REGISTRY_PORT;

        if (isListOnly && args.length > 1) {
            port = Integer.parseInt(args[1]);
        } else if (!isListOnly && args.length > 2) {
            port = Integer.parseInt(args[2]);
        }

        try {
            // 1. Locate the registry on the server machine and look up the stub.
            Registry registry = LocateRegistry.getRegistry(host, port);
            FileInterface fi = (FileInterface) registry.lookup("FileServer");

            if (isListOnly) {
                // Mode 1: No file requested -> Just list available files on the server
                System.out.println("--- Available Files on Server ---");
                List<String> serverFiles = fi.listFiles();
                for (String file : serverFiles) {
                    System.out.println(" - " + file);
                }
                System.out.println("----------------------------------");
            } else {
                System.out.println("Returned class: " + fi.getClass().getName());

                String[] boundNames = registry.list();
                System.out.println("Names bound in registry:");
                for (String name : boundNames) {
                    System.out.println(" - " + name);
                }
                System.out.println("----------------------------------\n");

                byte[] data = fi.downloadFile(fileName);
                if (data == null) {
                    System.err.println("The server could not provide '" + fileName + "'.");
                    return;
                }

                // 3. Save the file in the current directory under its base name.
                Path target = Path.of("Task3/FileApp/Client/" + Path.of(fileName).getFileName().toString());
                Files.write(target, data);

                System.out.println("\nFile downloaded successfully!");
                System.out.println("File Name: " + Path.of(fileName).getFileName().toString());
                System.out.println("Size: " + data.length + " bytes");
            }
        } catch (FileServiceException e) {
            System.err.println("File not found on server: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("FileClient error: " + e.getMessage());
            e.printStackTrace();
        }
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
