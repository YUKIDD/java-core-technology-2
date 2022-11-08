package org.example.chap10.aes;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESTest {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        if (args[0].equals("-genkey")) {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecureRandom random = new SecureRandom();
            keygen.init(random);
            SecretKey key = keygen.generateKey();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(args[1]))) {
                out.writeObject(key);
            }
        } else {
            int mode;
            if (args[0].equals("-encrypt")) {
                mode = Cipher.ENCRYPT_MODE;
            } else {
                mode = Cipher.DECRYPT_MODE;
            }
            try {
                ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(args[3]));
                FileInputStream in = new FileInputStream(args[1]);
                FileOutputStream out = new FileOutputStream(args[2]);
                Key key = (Key) keyIn.readObject();
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(mode,key);
                Util.crypt(in,out,cipher);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (ShortBufferException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
