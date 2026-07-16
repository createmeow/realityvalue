package dev.anye.mc.reality_value.lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class _File {
    /**
     * @param strings a ,b ,c
     * @return "/"+"a"+"/"+"b"
     */
    public static String getFilePath(String... strings) {
        StringBuilder path = new StringBuilder();
        for (String s : strings) {
            path.append(File.separator).append(s);
        }
        return path.toString();
    }

    public static String getFileFullPathWithRun(String... strings) {
        return System.getProperty("user.dir") + getFilePath(strings);
    }

    public static boolean getFileFullPathWithRunAndCheck(String... strings) {
        return checkAndCreateDir(System.getProperty("user.dir") + getFilePath(strings));
    }

    public static boolean checkAndCreateDir(String dir) {
        File folder = new File(dir);
        if (!folder.exists()) {
            return folder.mkdirs();
        }
        return true;
    }

    public static void checkAndCreateDir(String... dirs) {
        for (String dir : dirs) {
            File folder = new File(dir);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new RuntimeException("dir create failed");
            }
        }
    }

    public static List<File> findFiles(File folder) {
        List<File> allFiles = new ArrayList<>();
        if (!folder.isDirectory()) {
            return allFiles;
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".json")) {
                    allFiles.add(file);
                } else if (file.isDirectory()) {
                    allFiles.addAll(findFiles(file));
                }
            }
        }
        return allFiles;
    }

    public static List<Path> getFiles(String directoryPath, String suffix) {
        List<Path> jsonFiles = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(suffix)) {
                        jsonFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonFiles;
    }

    public static String getFileNameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(0, dotIndex);
        } else {
            return filename;
        }
    }
}