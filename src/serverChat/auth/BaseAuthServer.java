package serverChat.auth;

import java.sql.SQLException;

public class BaseAuthServer implements AuthServer {
    private SQLUsers sqlUsers;

    @Override
    public void start() throws SQLException, ClassNotFoundException {
        sqlUsers = new SQLUsers();
        sqlUsers.connect();
        System.out.println("Auth server is running");
    }

    @Override
    public void stop() throws SQLException {
        sqlUsers.disconnect();
        System.out.println("Auth server has stopped");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) throws SQLException {
        if(login.equals("")||pass.equals("")){
            return null;
        }
        User entryOut = sqlUsers.getInformation(login);
        //System.out.println("информация при авторизации: " + entryOut.toString());
        String passIn = entryOut.getPassword();
        System.out.println(passIn);
        if(passIn==null)
            return null;
        if(passIn.equals(pass))
            return entryOut.getNick();
        else
            return null;
    }
}
