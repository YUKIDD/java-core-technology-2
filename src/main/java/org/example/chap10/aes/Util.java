package org.example.chap10.aes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {
    public static void crypt(InputStream in, OutputStream out, Cipher cipher) throws IOException, ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] intBytes = new byte[blockSize];
        byte[] outBytes = new byte[outputSize];

        int inLength = 0;
        boolean done = false;
        while (!done) {
            inLength = in.read(intBytes);
            if (inLength == blockSize) {
                int outLength = cipher.update(intBytes,0,blockSize,outBytes);
                out.write(outBytes,0,outLength);
            } else {
                done = true;
            }
            if (inLength > 0) {
                outBytes = cipher.doFinal(intBytes,0,inLength);
            } else {
                outBytes = cipher.doFinal();
            }
            out.write(outBytes);
        }
    }
}
