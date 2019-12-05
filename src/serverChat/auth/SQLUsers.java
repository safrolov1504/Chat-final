package serverChat.auth;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import serverChat.MyServer;

import java.sql.*;


public class SQLUsers {
    private static Connection con;
    private static final String URL_SQL = "jdbc:sqlite:/Users/safrolov/Documents/JavaProgramming/mailru2/part2/lesson2_dataBase/hw_chat/src/identifier.sqlite";
    private static PreparedStatement ps;

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection(URL_SQL);
        //Statement statement = con.createStatement();
        //System.out.println("There is connection with SQL");
        MyServer.admin.info("There is connection with SQL");
    }

    public void disconnect() throws SQLException {
        con.close();
    }

    public User getInformation(String login) throws SQLException {
        ps = con.prepareStatement("SELECT Login, Nick,Password FROM users where Login = ?");
        ps.setString(1,login);
        ResultSet rs = ps.executeQuery();
        ps.addBatch();
        if(rs.next()){
            return new User(rs.getString("Login"),
                     rs.getString("Password"),rs.getString("Nick"));
        }
        else {
            return new User(null,null,null);
        }
    }

    public static boolean isNickBusy(String nick) throws SQLException {
        ps = con.prepareStatement("SELECT Nick FROM users");
        ResultSet rs = ps.executeQuery();
        ps.addBatch();
        while (rs.next()){
            if(rs.getString("Nick").equals(nick)){
                return true;
            }
        }
        return false;
    }

    public static boolean isLoginBusy(String login) throws SQLException {
        ps = con.prepareStatement("SELECT Login FROM users");
        ResultSet rs = ps.executeQuery();
        ps.addBatch();
        while (rs.next()){
            if(rs.getString("Login").equals(login)){
                return true;
            }
        }
        return false;
    }

    public static void addNew(String login,String pass, String nick) throws SQLException {
        ps = con.prepareStatement("INSERT INTO users (Login, Password, Nick) VALUES ('"+login+"','"+pass+"','"+nick+"');");
        ps.executeUpdate();
        ps.addBatch();
    }

    public static void changeNick(String oldNick, String newNick) throws SQLException {
        String sqlRe="UPDATE users SET Nick = ? WHERE Nick = ?";
        ps = con.prepareStatement(sqlRe);
        ps.setString(1,newNick);
        ps.setString(2,oldNick);
        ps.executeUpdate();
        ps.addBatch();
    }

}
