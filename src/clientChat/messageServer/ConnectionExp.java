package clientChat.messageServer;

import java.io.IOException;

public class ConnectionExp extends RuntimeException {

    public ConnectionExp(String message, Throwable cause) {
        super(message, cause);
    }
}