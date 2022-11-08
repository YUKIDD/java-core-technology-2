package org.example.chap10.classLoader;

import java.io.*;

public class Caesar {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("USAGE: java classLoader.Caesar in out key");
            return;
        }

        try (InputStream in = new FileInputStream(args[0]);
             OutputStream out = new FileOutputStream(args[1])) {
            int key = Integer.parseInt(args[2]);
            int ch;
            while ((ch = in.read()) != -1) {
                byte c = (byte) (ch + key);
                out.write(c);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
