package org.example.chap01;

/**
 * 计算文件中单词的个数
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CountLongWords {
    public static void main(String[] args) throws IOException {
        String contents = new String(Files.readAllBytes(Paths.get("src/main/java/org/example/chap01/test.txt")), StandardCharsets.UTF_8);
        List<String> words = List.of(contents.split("\\PL+"));

        long count = 0;
        for (String w : words) {
            if (w.length() > 3) {
                count++;
            }
        }
        System.out.println(count);

        count = words.stream().filter(w -> w.length() > 3).count();
        System.out.println(count);

        count = words.parallelStream().filter(w -> w.length() > 3).count();
        System.out.println(count);
    }
}
