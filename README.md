# SOFE 4790 Distributed Systems - Lab #2: Developing Distributed Applications with Java RMI

---

## Project Structure
This submission contains the source code for all tasks structured into separate task subfolders as required:
- `Task1/FileApp/` - RMI File Service setup, exploration, and verification.
- `Task2/FileApp/` - Server connection metadata logging (`RemoteServer.getClientHost()`) and client download confirmations.
- `Task3/FileApp/` - Remote directory file listing (`listFiles`) and custom serializable exception handling (`FileServiceException`).
- `Task4/FileApp/` - Concurrent multi-threaded file uploads with thread-safe counters (`AtomicInteger`) and per-filename locking (`ConcurrentHashMap` / `ReentrantLock`).

---

## How to Compile and Run

Each task folder is self-contained. To compile and run any task, navigate to the source directory (`src/main/java`) and follow the commands below:

### Task 1 or Task 2:
1. **Start Server:**
   ```bash
   javac Task1/FileApp/Server/*.java
   java Task1.FileApp.Server.FileServer
   
2. **Start Client:**
   ```bash
   javac Task1/FileApp/Client/*.java
   java Task1.FileApp.Client.FileClient DSLab2SS.png localhost
You can use the above commands for task two as well. Just replace Task1 with Task2 in the commands.

### Task 3:
1. **Start Server:**
   ```bash
   javac Task3/FileApp/Server/*.java
   java Task3.FileApp.Server.FileServer

2. **Start Client (List Mode):**
   ```bash
   javac Task3/FileApp/Client/*.java
   java Task3.FileApp.Client.FileClient localhost

3. **Start Client (Download / Test Exception):**
   ```bash
   javac Task3/FileApp/Client/*.java
   java Task3.FileApp.Client.FileClient notRealFile.txt localhost

### Task 4:
1. **Start Server:**
   ```bash
   javac Task4/FileApp/Server/*.java
   java Task4.FileApp.Server.FileServer

2. **Start Client (Upload / List / Download):**
   ```bash
   javac Task4/FileApp/Client/*.java
   java Task4.FileApp.Client.FileClient upload upload1.txt localhost
   java Task4.FileApp.Client.FileClient list localhost
   java Task4.FileApp.Client.FileClient DSLab2.txt localhost
