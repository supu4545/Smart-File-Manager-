# Smart File Manager

A **Java-based console File Manager** that helps users scan, search, organize, back up, and detect duplicate files inside a selected folder.

## Features

* 📂 **Scan Folder** — Scans the selected folder and its subfolders and displays the total number of files, folders, and storage size.
* 📄 **View All Files** — Displays all files found during the scan along with their sizes.
* 🔎 **Search Files** — Searches files by name using a keyword.
* 🗂️ **Organise Files** — Automatically categorizes files into folders such as:

  * Images
  * Documents
  * Audios
  * Videos
  * Archives
  * Applications
  * Fonts
  * Coding
  * Others
* 💾 **Create Backup** — Creates a complete backup of the selected folder while maintaining its folder structure.
* ♻️ **Duplicate File Detection** — Finds duplicate files by comparing their file sizes and **SHA-256 hashes**.
* ⚡ **Multithreading** — Uses a fixed thread pool to calculate file hashes efficiently when checking multiple duplicate files.
* 🛡️ **Input Validation** — Handles invalid menu input and invalid folder paths.

## Technologies Used

* **Java**
* Java NIO (`Path`, `Files`, `Files.walk`)
* Java Collections (`ArrayList`, `HashMap`, `List`, `Map`)
* **SHA-256** hashing
* Java Concurrency (`ExecutorService`, `Future`)
* Exception Handling
* File I/O

## How to Run

### 1. Clone the repository

```bash
git clone <your-repository-url>
```

### 2. Open the project

Open the project in any Java-supported IDE such as **IntelliJ IDEA, Eclipse, or VS Code**.

### 3. Compile the program

```bash
javac FileManager.java
```

### 4. Run the program

```bash
java FileManager
```

## How to Use

After starting the program, you will see a menu:

```text
1. Scan Folder
2. View all files
3. Search files
4. Organise files
5. Create backup
6. Duplicate files
7. Exit
```

**First, use `Scan Folder` and enter the path of the folder you want to manage.**
After scanning, the remaining file-management features become available.

## Duplicate Detection

The duplicate detection system first groups files based on **file size**. Files having the same size are then compared using **SHA-256 hashing** to determine whether their contents are actually identical.

For checking all duplicates, the project uses `ExecutorService` with a fixed thread pool to calculate hashes concurrently.

## Backup

The backup feature supports:

1. Creating a backup at a location chosen by the user.
2. Automatically creating a backup folder next to the selected folder.

The backup maintains the original folder structure and copies file attributes where possible.

## Requirements

* **Java 17 or later** recommended
* A computer with access to the folders you want to manage

## Project Structure

```text
FileManager/
│
├── FileManager.java
└── README.md
```

## Note

This is a **console-based project** created for learning and practicing Java concepts such as:

* Object-oriented programming fundamentals
* Java NIO file handling
* Collections
* Exception handling
* Hashing
* Multithreading and concurrency

Feel free to use, modify, and improve the project.
