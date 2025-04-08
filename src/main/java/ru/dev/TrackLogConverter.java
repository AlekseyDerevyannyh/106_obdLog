package ru.dev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrackLogConverter implements Runnable {
    private String fileName;
    private String inDir;
    private String outDir;
    private String header;
    private List<String> lines = new ArrayList<>();

    public TrackLogConverter(String fileName, String inDir, String outDir) {
        this.fileName = fileName;
        this.inDir = inDir;
        this.outDir = outDir;
    }

    @Override
    public void run() {
        try {
            readFile();
            removeHeaderFailLines();
            removeLogWhenStop();
            replaceInvalid();
            replaceMonth();
            writeFile();
        } catch (IOException e) {
            System.out.printf("IO error in file: %s\n", this.fileName);
        }
    }

    private String monthToNumber(String month) {
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

    private void readFile() throws IOException {
        String fileName = this.inDir + "/" + this.fileName;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            if (fileReader.ready()) {
                this.header = fileReader.readLine();
            }
            while (fileReader.ready()) {
                this.lines.add(fileReader.readLine());
            }
        }
    }

    private void removeHeaderFailLines() {
        this.lines.removeIf(s -> s.chars().filter(c -> c == '-').count() > 4);
    }

    private boolean isNormalVoltage(String line) {
        String[] values = line.split(",");
        return Double.parseDouble(values[values.length - 1]) > 12.0;
    }

    private void removeLogWhenStop() {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < this.lines.size(); i++) {
            if (((i < 2) || (i > this.lines.size() - 2))
                    && isNormalVoltage(this.lines.get(i))) {
                result.add(this.lines.get(i));
            }
            if ((i + 2 < this.lines.size()
                    && isNormalVoltage(this.lines.get(i))
                    && isNormalVoltage(this.lines.get(i + 1))
                    && isNormalVoltage(this.lines.get(i + 2)))
                    && (i - 2 >= 0
                    && isNormalVoltage(this.lines.get(i))
                    && isNormalVoltage(this.lines.get(i - 1))
                    && isNormalVoltage(this.lines.get(i - 2)))) {
                result.add(this.lines.get(i));
            }
        }
        this.lines = result;
    }

    private void replaceInvalid() {
        this.lines =  this.lines.stream()
                .map(s -> s.replaceAll(",-,", ",0,"))
                .collect(Collectors.toList());
    }

    private void replaceMonth() {
        List<String> result = new ArrayList<>();
        for (String line : this.lines) {
            String month = line.split("-")[1];
            String newMonth = monthToNumber(month);
            result.add(line.replaceFirst(month, newMonth));
        }
        this.lines = result;
    }

    private String getOutFileName() {
        String month = this.fileName.split("-")[2];
        String outFileName;
        if (!month.matches("\\d{2}")) {
            String monthNumber = monthToNumber(month);
            outFileName = fileName.replaceFirst(month, monthNumber);

        } else {
            outFileName = this.fileName;
        }
        return outDir + "/" + outFileName;
    }

    private void writeFile() throws IOException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(getOutFileName()))) {
            fileWriter.write(this.header + "\n");
            for (String line : this.lines) {
                fileWriter.write(line + "\n");
            }
        }
    }
}
