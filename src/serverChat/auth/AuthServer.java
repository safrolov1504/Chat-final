package serverChat.auth;

import com.sun.istack.internal.Nullable;

import java.sql.SQLException;

public interface AuthServer {
    void start() throws SQLException, ClassNotFoundException;
    void stop() throws SQLException;

    @Nullable
    String getNickByLoginPass(String login, String password) throws SQLException;
}
