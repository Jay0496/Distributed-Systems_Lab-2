package Task1.FileApp.Client;

import Task1.FileApp.FileInterface;

import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Downloads a file from a FileServer.
 *
 * Usage: java FileClient fileName host [port]
 *   fileName  file to download (path as seen by the server)
 *   host      machine running the server, e.g. localhost or 192.168.1.20
 *   port      registry port (default 1099)
 */
public class FileClient {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java FileClient fileName host [port]");
            return;
        }
        String fileName = args[0];
        String host = args[1];
        int port = (args.length > 2) ? Integer.parseInt(args[2]) : Registry.REGISTRY_PORT;

        try {
            // 1. Locate the registry on the server machine and look up the stub.
            Registry registry = LocateRegistry.getRegistry(host, port);
            FileInterface fi = (FileInterface) registry.lookup("FileServer");

            // Task 1c: Print dynamic proxy stub class name (demonstrates location transparency)
            System.out.println("Returned class: " + fi.getClass().getName());

            // Task 1c: Print all bound identifiers currently published in the registry
            String[] boundNames = registry.list();
            System.out.println("Names bound in registry:");
            for (String name : boundNames) {
                System.out.println(" - " + name);
            }

            // 2. Call the remote method as if it were local.
            byte[] data = fi.downloadFile(fileName);
            if (data == null) {
                System.err.println("The server could not provide '" + fileName + "'.");
                return;
            }

            // 3. Task 1b: Save byte payload to local client folder under original filename
            Path target = Path.of("Task1/FileApp/Client/" + Path.of(fileName).getFileName().toString());
            Files.write(target, data);
        } catch (Exception e) {
            // Task 1c: Catches java.rmi.ConnectException if executed while server is inactive
            System.err.println("FileClient error: " + e);
            e.printStackTrace();
        }
    }
}
