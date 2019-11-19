package clientChat.messageServer;

import java.io.IOException;

public interface IMessageService {
    void sendMessage(String message);

    void processRetrievedMessage(String message);

    default void close() throws IOException {

    }

    boolean isAuth();

    void addNewHistory(String ms);
}
