package serverChat.client;

import networkChatCommons.Command;
import networkChatCommons.Message;
import networkChatCommons.variosOfMessage.*;
import serverChat.MyServer;
import serverChat.auth.SQLUsers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private Socket socket;
    private MyServer myServer;
    private DataInputStream in;
    private DataOutputStream out;

    private String clientName;

    public String getClientName() {
        return clientName;
    }

    public ClientHandler(Socket socket, MyServer myServer) {
        try {
            clientName="";
            this.socket = socket;
            this.myServer = myServer;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (clientName.equals("")){
                        String clientMessage = in.readUTF();
//                        System.out.println(clientMessage);
                        Message message = Message.fromJson(clientMessage);
                        if(message.command==Command.AUTH_MESSAGE){
                            authentication(message);
                        }
                        else if(message.command==Command.ADD_NEW){
                            addNewUser(message);
                            authentication(message);
                        }
                    }
                    readMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    CloseConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to creat client handler", e);
        }
    }

    private void addNewUser(Message message){
        NewUser newUser = new NewUser();
        Message messageOut = new Message();
        try {
            if(SQLUsers.isNickBusy(message.newUser.nick)){
                newUser.message = "Ник занят";
                messageOut = Message.creatNewUser(Command.ADD_NEW, newUser);
            }
            else if(SQLUsers.isLoginBusy(message.newUser.login)){
                newUser.message = "Логин занят";
                messageOut = Message.creatNewUser(Command.ADD_NEW,newUser);
            }
            else
            {
                newUser.message= ("Добавлен новый пользователь");
                SQLUsers.addNew(message.newUser.login,message.newUser.password,message.newUser.nick);
                newUser.login=message.newUser.login;
                newUser.nick=message.newUser.nick;
                messageOut = Message.creatNewUser(Command.ADD_NEW_OK,newUser);
                //messageOut = Message.creatNewUserOk(newUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.SendMessage(messageOut.toJson());
        //System.out.println(messageOut.toJson());
    }

    // /auth login password
    private void authentication(Message message) throws IOException, SQLException {
//        String clientMessage = in.readUTF();
//        System.out.println(clientMessage);
//        Message message = Message.fromJson(clientMessage);
        if (message.command == Command.AUTH_MESSAGE) {
            AuthMessage authMessage = message.authMessage;
            String login = authMessage.login;
            String password = authMessage.password;

            String nick = myServer.getAuthServer().getNickByLoginPass(login, password);
            authMessage.nick = nick;

            if (nick == null) {
                creatAnswerForAuth(authMessage, "Не верный логин и пароль");
                return;
            }

            //проверяет есть ли такой ник
            if (myServer.isNickBusy(nick)) {
                creatAnswerForAuth(authMessage,"Учетная запись уже используется");
                return;
            }
            creatAnswerForAuth(authMessage, Command.AUTH_OK.toString());
            clientName = nick;

            myServer.broadcastMessage(broadcastGeneralInformation(" is online"));
            myServer.subscribe(this);
        }
    }

    private String broadcastGeneralInformation(String message) {
        PublicMessage publicMessage = new PublicMessage();
        publicMessage.from=clientName;
        publicMessage.message = message;

        Message msgOut = Message.creatPublic(publicMessage);
        return msgOut.toJson();

    }


    private void creatAnswerForAuth(AuthMessage authMessage, String message) {
        authMessage.message = message;
        Message msg= Message.creatAuth(authMessage);
        SendMessage(msg.toJson());
    }

    private void readMessage() throws IOException, SQLException {
        while (true){
            String clientMessage = in.readUTF();
            Message m = Message.fromJson(clientMessage);
            switch (m.command){
                case CHANGE_NICK:
                    if(SQLUsers.isNickBusy(m.changeNickMessage.newNick)){
                        m.changeNickMessage.message = "Ник уже занят";
                        System.out.println(m.toJson());
                        this.SendMessage(m.toJson());
                    }
                    else{
                        SQLUsers.changeNick(m.changeNickMessage.oldNick,m.changeNickMessage.newNick);
                        m.command = Command.CHANGE_NICK_OK;
                        this.SendMessage(m.toJson());
                        myServer.unsubscribe(this);
                        clientName = m.changeNickMessage.newNick;

                        myServer.broadcastMessage(
                                broadcastGeneralInformation("Пользователь "+ m.changeNickMessage.oldNick +
                                        " сменил ник на "+ m.changeNickMessage.newNick),this);
                        myServer.subscribe(this);
                        System.out.println("Меняем ник");
                    }

                case PUBLIC_MESSAGE:
                    myServer.broadcastMessage(clientMessage,this);
                    break;
                case PRIVATE_MESSAGE:
                    PrivateMessage privateMessage = m.privateMessage;
                    myServer.sendPrivateMessage(privateMessage.from, privateMessage.to,privateMessage.message,this);
                    break;
                case END:
                    return;
            }
        }
    }

    public void SendMessage(String message)  {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Failed to send message to user " + clientName + ": "+ message);
            e.printStackTrace();
        }
    }

    private void CloseConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMessage(broadcastGeneralInformation(" is offline"));
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket!");
            e.printStackTrace();
        }
    }


}
