package ru.dev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static List<String> logs;
    public static String header;

    public static void main(String[] args) throws IOException {
        String fileName = "trackLog-2024-нояб.-23_07-22-08.csv";
        logs = readFile(fileName);
        removeHeaderFailLines(logs);
        logs = removeLogWhenStop(logs);
        logs = replaceInvalid(logs);
        logs = replaceMonth(logs);

        String month = fileName.split("-")[2];
        String monthNumber = monthToNumber(month);
        String outputFileName = fileName.replaceFirst(month, monthNumber);
        writeFile(outputFileName, logs);
    }

    public static List<String> readFile(String fileName) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            List<String> result = new ArrayList<>();
            if (fileReader.ready()) {
                header = fileReader.readLine();
            }
            while (fileReader.ready()) {
                result.add(fileReader.readLine());
            }
            return result;
        }
    }

    public static void writeFile(String fileName, List<String> lines) throws IOException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName))) {
            fileWriter.write(header + "\n");
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

    public static void removeHeaderFailLines(List<String> lines) {
        lines.removeIf(s -> s.chars().filter(c -> c == '-').count() > 4);
    }

    public static List<String> removeLogWhenStop(List<String> lines) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            if (((i < 2) || (i > lines.size() - 2))
                    && isNormalVoltage(lines.get(i))) {
                result.add(lines.get(i));
            }
            if ((i + 2 < lines.size()
                    && isNormalVoltage(lines.get(i))
                    && isNormalVoltage(lines.get(i + 1))
                    && isNormalVoltage(lines.get(i + 2)))
                    && (i - 2 >= 0
                    && isNormalVoltage(lines.get(i))
                    && isNormalVoltage(lines.get(i - 1))
                    && isNormalVoltage(lines.get(i - 2)))) {
                result.add(lines.get(i));
            }
        }
        return result;
    }

    public static boolean isNormalVoltage(String line) {
        String[] values = line.split(",");
        return Double.parseDouble(values[values.length - 1]) > 12.0;
    }

    public static List<String> replaceInvalid(List<String> lines) {
        return lines.stream()
                .map(s -> s.replaceAll(",-,", ",0,"))
                .collect(Collectors.toList());
    }

    public static List<String> replaceMonth(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            String month = line.split("-")[1];
            String newMonth = monthToNumber(month);
            result.add(line.replaceFirst(month, newMonth));
        }
        return result;
    }
}