package ru.otus.fileManager;

public class Main {
    public static void main(String[] args) {
        ru.otus.fileManager.FileManager fileManager = new ru.otus.fileManager.FileManager();
        System.out.println("Добро пожаловать в консольный файловый менеджер");
        System.out.println("Введите \"help\" для вывода списка команд");
        fileManager.start();
    }
}
