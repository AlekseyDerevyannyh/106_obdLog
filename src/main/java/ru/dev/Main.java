package ru.dev;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        String inDir;
        String outDir;
        String separator = FileSystems.getDefault().getSeparator();
        if (args.length == 0) {
            inDir = ".";
            outDir = "./result";
        } else if (args.length == 1) {
            inDir = args[0];
            outDir = "./result";
        } else {
            inDir = args[0];
            outDir = args[1];
        }
        Set<String> files = getFileNames(inDir);
        if (files == null || files.isEmpty())       return;
        String trackLogRegex = "^trackLog-\\d{4}-.{0,7}-\\d{2}_\\d{2}-\\d{2}-\\d{2}\\.csv$";
        ConcurrentLinkedQueue<String> trackLogFiles = new ConcurrentLinkedQueue<>();
        for (String file : files) {
            if (file.matches(trackLogRegex))
                trackLogFiles.add(file);
        }
        File outDirectory = new File(outDir);
        if (!outDirectory.exists()) {
            boolean success = outDirectory.mkdirs();
            if (!success) {
                System.out.println("Failed to create output directory");
                return;
            }
        }

//        ExecutorService executor = Executors.newWorkStealingPool();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String fileName;
        while ((fileName = trackLogFiles.poll()) != null) {
            executor.execute(new TrackLogConverter(fileName, inDir, outDir, separator));
        }
        executor.shutdown();
    }

    public static Set<String> getFileNames(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }
}
