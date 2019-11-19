package clientChat.interfaceMy;

import javafx.scene.control.ListView;
import networkChatCommons.Message;
import networkChatCommons.variosOfMessage.PrivateMessage;
import networkChatCommons.variosOfMessage.PublicMessage;

public class MessageController {

    public static Message buildMessage(ListView<String> clientListIn,String fromNick, String message) {
        String selectedNickname = clientListIn.getSelectionModel().getSelectedItem();
        if (selectedNickname !=null){
            return buildPrivateMessage(fromNick, selectedNickname, message);
        }
        return buildPublicMessage(fromNick, message);
    }

    private static Message buildPrivateMessage(String fromNick, String selectedNickname, String message){
        PrivateMessage privateMsg = new PrivateMessage();
        privateMsg.from = fromNick;
        privateMsg.to = selectedNickname;
        privateMsg.message = message;
        return Message.creatPrivate(privateMsg);
    }

    private static Message buildPublicMessage(String fromNick, String message) {
        PublicMessage publicMsg  =new PublicMessage();
        publicMsg.from =fromNick;
        publicMsg.message = message;
        return Message.creatPublic(publicMsg);
    }
}
