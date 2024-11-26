package ru.dev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        writeFile("file1", readFile("file"));
    }

    public static List<String> readFile(String fileName) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            List<String> result = new ArrayList<>();
            while (fileReader.ready()) {
                result.add(fileReader.readLine());
            }
            return result;
        }
    }

    public static void writeFile(String fileName, List<String> lines) throws IOException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                fileWriter.write(line + "\n");
            }
        }
    }

    public static String monthToNumber(String month) {
        return switch (month) {
            case "янв." -> "01";
            case "февр." -> "02";
            case "мар." -> "03";
            case "апр." -> "04";
            case "мая" -> "05";
            case "июн." -> "06";
            case "июл." -> "07";
            case "авг." -> "08";
            case "сент." -> "09";
            case "окт." -> "10";
            case "нояб." -> "11";
            case "дек." -> "12";
            default -> throw new IllegalStateException("Unexpected value: " + month);
        };
    }
}