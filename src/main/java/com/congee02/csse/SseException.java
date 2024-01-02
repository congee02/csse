package com.congee02.csse;

/**
 * SSE 异常
 * @author gn
 */
@SuppressWarnings("unused")
public class SseException extends RuntimeException {
    public SseException() {
    }

    public SseException(String message) {
        super(message);
    }

    public SseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SseException(Throwable cause) {
        super(cause);
    }

    public SseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
