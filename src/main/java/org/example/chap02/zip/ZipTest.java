package org.example.chap02.zip;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipTest {
    public static void main(String[] args) throws IOException {
        String zipname;
        if (args.length > 1) {
            zipname = args[0];
        } else {
            zipname = "src/main/java/org/example/chap02/zip/pom.zip";
        }
        showContents(zipname);
        System.out.println("---");
        showContents2(zipname);
    }

    public static void showContents(String zipname) throws IOException {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipname))) {
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                System.out.println(entry.getName());
                Scanner in = new Scanner(zin, StandardCharsets.UTF_8);
                while (in.hasNextLine()) {
                    System.out.println("  " + in.nextLine());
                }
                zin.closeEntry();
            }
        }
    }

    public static void showContents2(String zipname) throws IOException {
        FileSystem fs = FileSystems.newFileSystem(Paths.get(zipname),null);
        Files.walkFileTree(fs.getPath("/"),new SimpleFileVisitor<Path>() {
            public FileVisitResult visitResult(Path path, BasicFileAttributes attrs) throws IOException {
                System.out.println(path);
                for (String line : Files.readAllLines(path, Charset.forName("UTF-8"))) {
                    System.out.println("   " + line);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
