package org.sevengod.javabe.web.exception;

/**
 * Custom exception for Dify API related errors
 */
public class DifyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DifyException(String message) {
        super(message);
    }

    public DifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DifyException timeout() {
        return new DifyException("DifyAPI响应超时");
    }
}
