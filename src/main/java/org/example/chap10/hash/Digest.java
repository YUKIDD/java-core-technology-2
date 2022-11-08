package org.example.chap10.hash;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Scanner;

public class Digest {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        Scanner in = new Scanner(System.in);
        String filename;
        if (args.length >= 1){
            filename = args[0];
        } else {
            System.out.print("File name: ");
            filename = in.nextLine();
        }
        String algname;
        if (args.length >= 2) {
            algname = args[1];
        } else {
            System.out.println("Select one of the following algorithms: ");
            for (Provider p : Security.getProviders()) {
                for (Provider.Service s : p.getServices()) {
                    if (s.getType().equals("MessageDigest")) {
                        System.out.println(s.getAlgorithm());
                    }
                }
            }
            System.out.print("Algorithm:");
            algname = in.nextLine();
        }
        MessageDigest alg = MessageDigest.getInstance(algname);
        byte[] input = Files.readAllBytes(Paths.get(filename));
        byte[] hash = alg.digest(input);
        for (int i = 0; i < hash.length; i++) {
            System.out.printf("%02X ",hash[i] & 0xFF);
        }
        System.out.println();
    }
}
