package serverChat.auth;

public class User {
    private String login;
    private String password;
    private String nick;


    public User(String login, String password, String nick) {
        this.login = login;
        this.password = password;
        this.nick = nick;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNick() {
        return nick;
    }

    @Override
    public String toString() {
        return "UserEntryOut{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }
}
