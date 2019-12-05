package serverChat.auth;

import serverChat.MyServer;

import java.sql.SQLException;

public class BaseAuthServer implements AuthServer {
    private SQLUsers sqlUsers;

    @Override
    public void start() throws SQLException, ClassNotFoundException {
        sqlUsers = new SQLUsers();
        sqlUsers.connect();
        MyServer.admin.info("Auth server is running");
        //System.out.println("Auth server is running");
    }

    @Override
    public void stop() throws SQLException {
        sqlUsers.disconnect();
        MyServer.admin.info("Auth server has stopped");
        //System.out.println("Auth server has stopped");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) throws SQLException {
        if(login.equals("")||pass.equals("")){
            return null;
        }
        User entryOut = sqlUsers.getInformation(login);
        //System.out.println("информация при авторизации: " + entryOut.toString());
        String passIn = entryOut.getPassword();
        if(passIn==null)
            return null;
        if(passIn.equals(pass))
            return entryOut.getNick();
        else
            return null;
    }
}
