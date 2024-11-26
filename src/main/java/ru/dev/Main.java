package ru.dev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

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
                fileWriter.write(line);
            }
        }
    }
}