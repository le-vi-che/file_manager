package ru.otus.fileManager;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class FileManager {

    private File currentDirectory;

    public FileManager() {
        this.currentDirectory = new File(System.getProperty("user.dir"));
    }

    void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\n" + currentDirectory + ">");
            String userCommand = scanner.nextLine();
            if (userCommand.startsWith("ls")) {
                if (userCommand.startsWith("ls i")) {
                    commandLsMoreDetails(currentDirectory);
                    continue;
                }
                commandLs(currentDirectory);
            } else if (userCommand.startsWith("cd ")) {
                commandCd(userCommand);
            }
            else if (userCommand.equals("cd..")) {
                commandCdParent();
            } else if (userCommand.startsWith("mkdir ")) {
                commandMkDir(userCommand, currentDirectory);
            } else if (userCommand.startsWith("rm ")) {
                commandRm(userCommand, currentDirectory.getAbsolutePath());
            } else if (userCommand.startsWith("mv ")) {
                commandMv(userCommand);
            } else if (userCommand.startsWith("cp ")) {
                commandCp(userCommand);
            } else if (userCommand.startsWith("finfo ")) {
                commandFinfo(userCommand, currentDirectory);
            } else if (userCommand.startsWith("help")) {
                commandHelp();
            } else if (userCommand.startsWith("exit")) {
                break;
            }
        }
    }

    public void commandLs(File directory) {
        if (directory.isFile()) {
            System.out.println("Не удалось вывести список файлов.");
            return;
        }
        File[] fileList = directory.listFiles();
        if (fileList == null) {
            throw new NullPointerException();
        }
        if (fileList.length == 0) {
            System.out.println("В папке нет элементов");
        }
        System.out.println("\n>>>Список файлов директории: ");
        for (File file : fileList) {
            if (file.isHidden()) {
                continue;
            }
            System.out.println(file.getName());
        }
    }

    public void commandLsMoreDetails(File directory) {
        File[] fileList = directory.listFiles();
        if (fileList == null || fileList.length == 0) {
            System.out.println("В указанной директории нет файлов ");
            return;
        }
        System.out.println("\n>>>Список файлов директории: ");
        for (File file : fileList) {
            if (file.isHidden()) {
                continue;
            }
            System.out.printf("Имя:%s, Размер:%s  bytes, Последнее обновление: %s\n", file.getName(), file.length(), convertDate(file));
        }
    }

    private void commandCd(String userCommand) {
        String[] parts = userCommand.split(" ");
        if (parts.length < 2) {
            System.out.println("Ошибка: Не указан путь к директории.");
            return;
        }

        File selectedDir = new File(currentDirectory, parts[1]);
        if (selectedDir.isDirectory()) {
            currentDirectory = selectedDir;
        } else {
            System.out.println("Ошибка: Директория не найдена.");
        }
    }

    private void commandCdParent() {
        currentDirectory = currentDirectory.getParentFile();
    }

    private static void commandHelp() {
        System.out.println("ls - вывод списка файлов в текущей папке без указания размера файлов\n" +
                "ls i – вывод списка файлов в текущей папке с указанием размера файлов\n" +
                "cd [path] – переход в указанную поддиректорию. cd .. – переход в родительский каталог\n" +
                "mkdir [name] – создание новой директории с указанным именем\n" +
                "mv [source] [destination] – переименовать/перенести файл или директорию\n" +
                "cp [source] [destination] – скопировать файл \n" +
                "rm [filename] – удаление указанного файла или директории \n" +
                "finfo [filename] – получение подробной информацию о файле \n" +
                "help – вывод в консоль всех поддерживаемых команд\n" +
                "exit – завершение работы файлового менеджера");
    }

    private void commandFinfo(String userCommand, File currentDirectory) {
        String[] textElements = userCommand.split(" ", 2);
        String fileName = textElements[1];
        String path = currentDirectory + "\\" + fileName;
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Файла с таким именем не существует в этой директории");
            return;
        }
        System.out.println("Name:" + file.getName() + " ,Size:" +
                file.length() + " ,Last Modified:" + convertDate(file));
    }


    private static void commandMkDir(String userCommand, File currentDirectory) {
        String[] elements = userCommand.split(" ", 2);
        String nameOfTheFutureDirectory = elements[1];
        String path = currentDirectory + "\\" + nameOfTheFutureDirectory;
        File file = new File(path);
        if (file.mkdir()) {
            System.out.println(">>>Директория создана " + file.getPath());
            return;
        }
        System.out.println(">>>Невозможно создать директорию " + nameOfTheFutureDirectory);//создать причину почему нельзя
    }


    private void commandRm(String userCommand, String pathOfCurrentDirectory) {
        String[] messageElements = userCommand.split(" ", 2);
        String fileName = messageElements[1];
        File fileToBeDeleted;

        if (fileName.startsWith("C:\\")) {
            fileToBeDeleted = new File(fileName);
        } else {
            String path = pathOfCurrentDirectory + "\\" + fileName;
            fileToBeDeleted = new File(path);
        }

        if (fileToBeDeleted.delete()) {
            System.out.printf("Файл " + "%s" + " удалён", fileName);
            if (fileToBeDeleted.getAbsolutePath().equals(currentDirectory.getAbsolutePath())) {
                currentDirectory = new File(currentDirectory.getParent());
            }
        } else {
            System.out.printf("Файл " + "%s" + " не получилось удалить", fileName);
        }
    }

    private void commandMv(String userCommand) {
        String[] textElements = userCommand.split(" ", 3);
        String source = textElements[1];
        String destination = textElements[2];
        File fileToMove = new File(source);
        if (fileToMove.renameTo(new File(destination))) {
            System.out.println("Файл был перемещён");
        } else {
            System.out.println("Не удалось переместить файл");
        }
    }

    private void commandCp(String userCommand) {
        String[] messageElements = userCommand.split(" ", 3);
        String source = messageElements[1];
        String destination = messageElements[2];
        Path soursePath = Paths.get(source).toAbsolutePath();
        Path destPath = Paths.get(destination).toAbsolutePath();
        try {
            Files.copy(soursePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            if (destPath.toFile().exists()) {
                System.out.println("Файл скопирован");
            } else {
                System.out.println("Не удалось скопировать файл");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String convertDate(File file) {
        long fileLastModifiedDate = file.lastModified();
        Date date = new Date(fileLastModifiedDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return simpleDateFormat.format(date);
    }
}