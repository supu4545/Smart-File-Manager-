import java.util.Scanner;
import java.util.InputMismatchException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;  
import java.util.Map;
import java.util.List;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileManager{

    static Scanner sc = new Scanner(System.in);                                 // STATIC VARIABLE 
    static int choice;
    static Path mainFolder;
    static boolean scanned = false;
    static long[] fileCount = {0};
    static long[] folderCount = {0};
    static long[] totalSize = {0};

    static ArrayList<Path> folders = new ArrayList<>();
    static ArrayList<Path> files = new ArrayList<>();
    public static void main(String[] args) {                                    // MAIN FUNCTION

        System.out.println("===== SMART FILE MANAGER =====");
        
        boolean running = true;
        while(running){
        if(!scanned){
            System.out.println("""
                    1. Scan Folder
                    2. View all files
                    3. Search files
                    4. Organise files
                    5. Create backup
                    6. Duplicate files
                    7. Exit
        """);
        }
        else{
            System.err.println();
            System.err.println("""
                        2. View all files
                        3. Search files
                        4. Organise files
                        5. Create backup
                        6. Duplicate 
                        7. Exit
                """);
        }
        
        while(true){
            try{
            System.out.print("Choose your option : ");
            choice = sc.nextInt(); 
            sc.nextLine();

            if(choice <= 0 || choice >= 8){
                System.out.println("Wrong option");
                continue;
            } 
            break;
           }
           catch(InputMismatchException e){
            System.out.println("Invalid input");
            sc.nextLine();
           }
        }

        switch(choice){
            case 1 -> validateFolder();
            case 2 -> viewAllFiles();
            case 3 -> searching();
            case 4 -> organiseFiles();
            case 5 -> createBackup();
            case 6 -> checkDuplicate();
            case 7 -> {
                System.err.println("Program Ended");
                running = false;
            }
        }
      }
        sc.close();
    }


    static void validateFolder(){                                               // VALIDATING FOLDER NAME
        System.out.print("\nEnter path of folder : ");
        String pathh = sc.nextLine();

        Path selectedFolder = Paths.get(pathh).toAbsolutePath().normalize();

        if(!Files.exists(selectedFolder)){
            System.err.println("Folder does not exists");
            return;
        }

        if(!Files.isDirectory(selectedFolder)){
            System.err.println("Given path is not a folder");
            return;
        }

        mainFolder = selectedFolder;
        scanFolder(true);
    }

    static void resetScanData() {                                               // METHOD FOR RESETING DATA
        files.clear();
        folders.clear();

        fileCount[0] = 0;
        folderCount[0] = 0;
        totalSize[0] = 0;

        scanned = false;
    }

    static boolean scanFolder(boolean showResult){                              // SCANNING FOLDER

        resetScanData();
        try(Stream<Path> paths = Files.walk(mainFolder)){
            
            paths.forEach(path -> {
                try{
                    if(Files.isDirectory(path)){

                        if(!path.equals(mainFolder)){
                            folderCount[0]++;

                            folders.add(path);
                        }
                    }
                    else if(Files.isRegularFile(path)){
                        fileCount[0] ++;

                        long fileSize = Files.size(path);
                        totalSize[0] += fileSize;
                        files.add(path);    
                    }
                }
                catch(IOException e){
                    System.out.println("Could not read file size" + path + e.getMessage());
                }
            });
            
            if(showResult){                                                      // PRINTING RESULT ACCORDING TO NEED
                System.err.println();
                System.out.println("********** Scan successfully done **********");
                System.err.println("Viewing all files of " + mainFolder);
                System.out.println("\n********** SCAN RESULT **********\n");
                System.out.println("Total subfolder : " + folderCount[0]);
                System.out.println("Total files     : " + fileCount[0]);
                System.out.printf("Total size      : %.2f KB (%.2f MB)", (totalSize[0] / 1024.0) , (totalSize[0] / (1024.0 * 1024.0)));
                System.err.println();
            }

            scanned = true;
            return true;
        }
        catch(IOException e){
            System.out.println("Could not scan the folder " + e.getMessage());
            resetScanData();
            return false;
        }
    }


    static void viewAllFiles(){                                                 // VIEWING ALL FILES
        System.err.println();
        if(!scanned){
                System.err.println("First scan a folder using option 1");
        }
        else if(files.isEmpty()){
            System.out.println("No file found");
        }
        else{
            for(Path file : files){
               try{
                double sizeKB = Files.size(file) / 1024.0;
                System.err.printf("FILE : %s | %.2f KB\n" , mainFolder.relativize(file) , sizeKB);
               }
               catch(IOException e){
                    System.err.println("Unable to calculate the size of file");
               }
            }
        }
    }


    static void searching(){                                                    // SEARCHING FILES
        System.err.println();
        if(!scanned){
            System.err.println("First scan a folder using option 1");
        }
        else if(files.isEmpty()){
            System.out.println("No file found");
        }
        else{
            System.err.print("Enter the name of the file : ");
            String keyword = sc.nextLine().toLowerCase().trim();
            boolean found = false;
            int count = 0;

            if (keyword.isEmpty()) {
                System.err.println("Search keyword cannot be empty");
                return;
            }

            System.err.println("********** SEARCH RESULT **********\n");
            for(Path file : files){
                 String fileName = file.getFileName().toString().toLowerCase();

                 if(fileName.contains(keyword)){
                    try{
                        double fileSizeKB = Files.size(file) / 1024.0 ;
                        System.out.println("File found : " + file);
                        System.out.printf("File size : %.2f" , fileSizeKB);
                        System.err.println();
                        count ++;
                        found = true;
                    }
                    catch(IOException e){
                        System.err.println("Unable to calculate tha size of file");
                    }
                 }
            }

            if(!found){
                System.err.println("\nNo file found");
            } 
            if(count == 1){
                System.err.println("\nOnly 1 file found");
            }
            else if(count > 1){
                System.err.println("\nTotal " + count + " files found");
            }
        }
    }

    
    static Path createUniquePath(Path targetFolder , String fileName){          // CREATING PATH FOR ORGANISING
        Path targetPath = targetFolder.resolve(fileName);

        if(!Files.exists(targetPath)){
            return targetPath;
        }

        String name;
        String extension;
        int number = 1;

        int dotIndex = fileName.lastIndexOf(".");
        if(dotIndex > 0){
            name = fileName.substring(0 , dotIndex);
            extension = fileName.substring(dotIndex);
        }
        else{
            name = fileName;
            extension = "";
        }

        while(true){                                                            // CREATING NEW FILE IF TWO FILES HAVE SAME NAME
            String newFileName = name + "(" + number + ")" + extension;
            targetPath = targetFolder.resolve(newFileName);
            
            if(!Files.exists(targetPath)){
                return targetPath;
            }
            number ++;
        }
    }

    static void moveFileFolder(Path targetFolder , Path file) throws IOException{       // MOVING FILES TO CORRECT FOLDERS

        Files.createDirectories(targetFolder);

        if (file.getParent().equals(targetFolder)) {
            return;
        }

        String fileName = file.getFileName().toString();
        Path targetPath = createUniquePath(targetFolder, fileName);
        
        Files.move(file , targetPath);

        System.err.println("Moved " + fileName + " -> " + targetPath.getFileName());
    }

    static void organiseFiles(){                                                         // FINAL ORGANISATION
        if(!scanned){
            System.out.println("First scan any folder using option 1");
        }
        else if(files.isEmpty()){
            System.err.println("No file found");
        }
        else{
            for(Path file : files){
                String fileName = file.getFileName().toString().toLowerCase();
                int dotIndex = fileName.lastIndexOf(".");
                String extension = fileName.substring(dotIndex + 1);

                if(extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")){
                    
                    try{
                        Path imageFolder = mainFolder.resolve("Images");
                        moveFileFolder(imageFolder , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
                else if(extension.equals("txt") || extension.equals("doc") || extension.equals("pdf") || extension.equals("docx")){
                    try{
                        Path documentFolder = mainFolder.resolve("Documents");
                       moveFileFolder(documentFolder , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
                else if(extension.equals("mp3") || extension.equals("wav") || extension.equals("m4a") || extension.equals("aac")){
                    try{
                        Path audioFolder = mainFolder.resolve("Audios");
                        moveFileFolder(audioFolder , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
                else if(extension.equals("mp4") || extension.equals("mkv") || extension.equals("webm") || extension.equals("m4v")){
                    try{
                        Path videoFolder = mainFolder.resolve("Videos");
                       moveFileFolder(videoFolder , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
                else if(extension.equals("zip") || extension.equals("rar") || extension.equals("tar")){
                    try{
                        Path archiveFolder = mainFolder.resolve("Archives");
                        moveFileFolder(archiveFolder , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
                else if(extension.equals("apk") || extension.equals("exe") || extension.equals("jar")){
                    try{
                        Path appFolder = mainFolder.resolve("Applications");
                        moveFileFolder(appFolder , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
                else if(extension.equals("ttf") || extension.equals("otf") || extension.equals("woff") || extension.equals("woff2")){
                    try{
                        Path fontFolder = mainFolder.resolve("Fonts");
                        moveFileFolder(fontFolder , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
                else if(extension.equals("c") || extension.equals("cpp") || extension.equals("java") || extension.equals("py") || extension.equals("html") || extension.equals("css") || extension.equals("php") || extension.equals("js") ||extension.equals("sql") || extension.equals("json") ||extension.equals("xml")){
                    try{
                        Path codeFolder = mainFolder.resolve("Coding");
                        moveFileFolder(codeFolder , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
                else{
                    try{
                        Path others = mainFolder.resolve("Others");
                       moveFileFolder(others , file);
                    }
                    catch(IOException e){
                        System.err.println("Could not move " + file + ": " + e.getMessage());
                    }
                }
            }
            System.err.println("\nOrganisation completed");

            resetScanData();                                                              // RESETING OLD DATA
            boolean refreshed = scanFolder(false);

            if(refreshed){
                System.err.println("File information refreshed");
            }
            else{
                System.err.println("Files moved but refreshing failed");
            }  
        }
    }


    static void createBackup(){                                                          // CREATING BACKUP 
        if(!scanned){
            System.out.println("First scan a folder by using option 1");
        }
        else{
            int choice;
             
            System.out.println("Option 1 : Make your own backup folder\nOption 2 : Automatic generation of backup folder");
            while(true){
           try{
                System.err.print("Choose your option : ");
                choice = sc.nextInt();
                sc.nextLine();

                if(choice != 1 && choice != 2){
                    System.err.println("Wrong option");
                }
                else{
                    break;
                }
           }
           catch(InputMismatchException e){
            System.err.println("Wrong input");
            sc.nextLine();
           }
        }
            
           if(choice == 1){
                System.err.print("Enter path of backup folder : ");
                String path = sc.nextLine();
                Path backupFolderPath = Paths.get(path).toAbsolutePath().normalize();

                if (backupFolderPath.startsWith(mainFolder)) {
                    System.err.println("Backup folder cannot be inside the main folder");
                    return;
                }
                backup_path(backupFolderPath);
           }
           else{
            Path parentFolder = mainFolder.getParent();

            if (parentFolder == null) {                                                                     // VALIDATING ROOT DIRECTORY
                System.err.println("Automatic backup cannot be created for a root directory.");
                System.err.println("Please choose option 1 and enter a backup location manually.");
                return;
            }

            Path backupFolderPath = parentFolder.resolve(mainFolder.getFileName() + "Backup");
            backup_path(backupFolderPath);
           }
        }
    }

    static void backup_path(Path backupFolderPath){                                     // METHOD FOR BACKUP
        boolean backup = false;
         try{
            Files.createDirectories(backupFolderPath);

            try(Stream <Path> paths = Files.walk(mainFolder)){
                paths.forEach(sourcePath -> {
                    try{
                        Path relativePath = mainFolder.relativize(sourcePath);
                        Path targetPath = backupFolderPath.resolve(relativePath);

                        if(Files.isDirectory(sourcePath)){
                            Files.createDirectories(targetPath);
                        }
                        else{
                            Files.copy(
                                sourcePath,
                                targetPath,
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES
                            );
                        }
                    }
                    catch(IOException e){
                        System.err.println("Could not copy " + sourcePath);
                    }
                });
            }
            backup = true;
         }
         catch(IOException e){
            System.err.println("Unable to create the folder");
         }

         if(backup){
            System.err.println("\nBackup created successfully");
         }
         else{
            System.err.println("\nUnable to create the backup");
         }
    }


    static void checkDuplicate(){                                                       // CHECKING DUPLICATE FILES
        if(!scanned){
            System.err.println("First scan a folder using option 1");
        }
        else{
            int duplicateChoice;
            System.err.println();
            System.err.println("Option 1 : Check all duplicate files\nOption 2 : Check a specific file : ");
            while(true){
                try{
                    System.err.print("Choose your option : ");
                    duplicateChoice = sc.nextInt();
                    sc.nextLine();
                    if(duplicateChoice != 1 && duplicateChoice != 2){
                        System.err.println("Wrong option");
                    }
                    else{
                        break;
                    }
                }
                catch(InputMismatchException e){
                    System.err.println("Wrong input");
                    sc.nextLine();
                }
            }

            if(duplicateChoice == 1){                                                   // CHECKING ALL DUPLICATES
                Map <Long , List<Path>> filesBySize = new HashMap<>();

                for(Path file : files){
                    try{
                        long fileSize = Files.size(file);
                        filesBySize.computeIfAbsent(fileSize , key -> new ArrayList<>()).add(file);
                    }
                    catch(IOException e){
                        System.err.println("Unable to calculate the size of file");
                    }
                }

                ExecutorService pool = Executors.newFixedThreadPool(5);         // MULTITHREADING

                Map <String , List<Path>> fileByHash = new HashMap<>();

            try{
                for(List<Path> sameFileSize : filesBySize.values()){                     
                    if(sameFileSize.size() < 2){
                        continue;
                    }

                    Map<Path , Future<String>> hashTask = new HashMap<>();              // MAP FOR STORING FILE AND FUTURE HASH

                    for(Path file : sameFileSize){                                      // First loop: submit all files of this size 
                        Future<String> future = pool.submit(() -> {
                            return returningHash(file);
                        });

                        hashTask.put(file , future);  
                    }

                    for(Map.Entry<Path , Future<String>> entry : hashTask.entrySet()){      // Second loop: collect the calculated hashes
                        Path file = entry.getKey();
                        Future<String> future = entry.getValue();

                        try{
                            String hash = future.get();
                            fileByHash.computeIfAbsent(hash , key -> new ArrayList<>()).add(file);
                        }
                        catch(InterruptedException e){
                            Thread.currentThread().interrupt();
                            System.err.println("Hash checking was interrupted");
                            return;
                        }
                        catch (ExecutionException e) {
                            System.err.println("Could not calculate the hash of " + file);
                            System.err.println("Reason: " + e.getCause());
                        }
                    }
                 } 
              }
              finally{
                pool.shutdown();
              }

                boolean foundDuplicate = false;
                int groupNumber = 1;

                for(List<Path> duplicateFiles : fileByHash.values()){
                    if(duplicateFiles.size() > 1){
                        foundDuplicate = true;
                        System.err.println();
                        System.err.println(getOrdinalNumbers(groupNumber) + " Duplicate Group : ");

                        for(Path file : duplicateFiles){
                           System.err.println(mainFolder.relativize(file));
                        }
                        groupNumber ++;
                    }
                }

                if(!foundDuplicate){
                    System.err.println("No duplicate files found");
                } 
            }
            else{                                                                       // CHECKING A SPECIFIC FILE
                System.err.print("Enter name of the file : ");
                String fileName = sc.nextLine().trim();

                List<Path> sameByName = new ArrayList<>();
                List<Path> sameByData = new ArrayList<>();
                boolean foundDuplicate = false;
                int countName = 0;

                for(Path file : files){                                                     
                    if(file.getFileName().toString().equalsIgnoreCase(fileName)){
                       sameByName.add(file);
                       countName ++;
                    } 
                }
                if(sameByName.isEmpty()){
                    System.err.println("File not exists");
                    return;
                }

                if(sameByName.size() == 1){                                             // FINDING DUPLICATE BY DATA
                    try{
                        String hash1 = returningHash(sameByName.get(0));

                        for(Path hashfile : files){
                            String hash2 = returningHash(hashfile);

                            if(hash1.equals(hash2)){
                                sameByData.add(hashfile);
                            }
                        }  
                    }
                    catch(IOException e){
                        System.err.println("Unable to calculate the hash");
                    }
                }
                else{
                    foundDuplicate = true;                                              // FINDING DUPLICATE BY NAME
                    String calculateHash;
                    Map<String , List<Path>> sameNameFileHash = new HashMap<>();

                    System.err.println("Duplicates found by name : ");
                    
                    for(Path file : sameByName){
                        System.err.println(mainFolder.relativize(file));
                       
                        try{                                                            // ALSO FINDING SAME NAME FILE BY DATA
                            calculateHash = returningHash(file);
                            sameNameFileHash.computeIfAbsent(calculateHash , key -> new ArrayList<>()).add(file);
                        }
                        catch(IOException e){
                            System.err.println("Unable to calculate the hash");
                        }
                    }
                    System.err.println("Total " + countName + "files found");

                    for(List<Path> duplicateByNameAndData : sameNameFileHash.values()){
                        if(duplicateByNameAndData.size() < 2){
                            continue;
                        }
                        
                        System.err.println("But by data only these " + duplicateByNameAndData.size() + "files are same : ");
                        for(Path file : duplicateByNameAndData){
                            System.err.println(mainFolder.relativize(file));
                        }
                    }   
                }

                if(sameByData.size() > 1){
                    foundDuplicate = true;
                    System.err.println("Duplicates found by Data : ");

                    for(Path file : sameByData){
                        System.err.println(mainFolder.relativize(file));
                    }
                }

                if(!foundDuplicate){
                    System.err.println("No duplicate files found");
                } 
            }
        }
    }

    static String returningHash(Path file) throws IOException{                          // RETURNING FILES HASH DATA
        MessageDigest digest;

        try{
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("SHA-256 is not available" , e);
        }

        try(InputStream input = Files.newInputStream(file)){
            byte [] buffer = new byte[10000];
            int byteReads;

            while((byteReads = input.read(buffer)) != -1){
                digest.update(buffer , 0 , byteReads);
            }

            byte[] hashData = digest.digest();

            StringBuilder hash = new StringBuilder();

            for(byte hashh : hashData){
                hash.append(String.format("%02x" , hashh & 0xff));
            }

            return hash.toString();
        }
    }


    static String getOrdinalNumbers(int i){                                             // FUNCTION FOR GETTING ORDINAL NUMBERS
        if(i % 100 >= 11 && i % 100 <= 13){
            return i + "th";
        }
        switch(i % 10){
            case 1 : return i + "st";
            case 2 : return i + "nd";
            case 3 : return i + "rd";
            default : return i + "th";
        }
    }
}