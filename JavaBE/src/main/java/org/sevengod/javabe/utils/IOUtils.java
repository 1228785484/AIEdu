package org.sevengod.javabe.utils;

import java.io.*;

public class IOUtils {
    
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    
    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
    
    public static BufferedInputStream toBufferedInputStream(InputStream input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("输入流不能为空");
        }
        return input instanceof BufferedInputStream ? 
               (BufferedInputStream) input : 
               new BufferedInputStream(input, DEFAULT_BUFFER_SIZE);
    }
}
