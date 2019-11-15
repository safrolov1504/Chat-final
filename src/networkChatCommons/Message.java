package networkChatCommons;

import com.google.gson.Gson;
import networkChatCommons.variosOfMessage.*;

import java.util.List;

public class Message {
    public Command command;

    public PrivateMessage privateMessage;
    public AuthMessage authMessage;
    public PublicMessage publicMessage;
    public ClientListMessage clientListMessage;
    public NewUser newUser;
    public ChangeNickMessage changeNickMessage;

    public String toJson() {
        return new Gson().toJson(this);
    }
    public static Message fromJson(String json){
        return new Gson().fromJson(json, Message.class);
    }

    private static Message create(Command cmd) {
        Message m = new Message();
        m.command=cmd;
        return m;
    }

    public static Message creatClientList(List<String> nicknames) {
        Message message = create(Command.CLIENT_LIST);
        ClientListMessage msg = new ClientListMessage();
        msg.online = nicknames;
        message.clientListMessage = msg;
        return message;
    }

    public static Message creatPrivate(PrivateMessage msg){
        Message m = create(Command.PRIVATE_MESSAGE);
        m.privateMessage = msg;
        return m;
    }

    public static Message creatPublic(PublicMessage msg){
        Message m = create(Command.PUBLIC_MESSAGE);
        m.publicMessage = msg;
        return m;
    }

    public static Message creatAuth(AuthMessage msg){
        Message m = create(Command.AUTH_MESSAGE);
        m.authMessage = msg;
        return m;
    }

    public static Message creatEnd(){
        Message m = create(Command.END);
        return m;
    }

    public static Message creatNewUser(Command command, NewUser nu){
        Message m = create(command);
        m.newUser = nu;
        return m;
    }

    public static Message creatNewUserOk(Command command, ChangeNickMessage cnn){
        Message m = create(command);
        m.changeNickMessage = cnn;
        return m;
    }
}
