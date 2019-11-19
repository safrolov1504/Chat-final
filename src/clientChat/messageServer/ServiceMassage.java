package clientChat.messageServer;

import clientChat.interfaceMy.Controller;
import clientChat.interfaceMy.WorkWithHistory;
import javafx.collections.FXCollections;
import networkChatCommons.variosOfMessage.ClientListMessage;
import networkChatCommons.Command;
import networkChatCommons.Message;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class ServiceMassage implements IMessageService{
    private static final String HOST_ADDRESS_PROP = "server.address";
    private static final String HOST_PORT_PROP = "server.port";
    private boolean isAuth = false;
    private String hostAddress;
    private int hostPort;
    private WorkWithHistory history;

    private TextArea textArea;
//    private IMessageService messageService;
    private Network network;
    private Controller primaryController;
//    private boolean needStopServerOnClosed;
//    private boolean needStopServiceClient;

    private String nickname;
    public void setNetwork(Network network) {
        this.network = network;
    }

    public ServiceMassage(Controller primaryController) {
        this.textArea = primaryController.textArea;
        this.primaryController = primaryController;
        initialise();
    }

    public boolean isAuth() {
        return isAuth;
    }

    private void initialise() {
        readProperties();
        startConnectionToServer();
    }

    private void readProperties() {
        //hostAddress="localhost";
        //hostPort=8189;
        Properties serverProperties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/clientChat/resources/application.properties")) {
            serverProperties.load(inputStream);
            hostAddress = serverProperties.getProperty(HOST_ADDRESS_PROP);
            hostPort = Integer.parseInt(serverProperties.getProperty(HOST_PORT_PROP));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read application.properties file", e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port value", e);
        }
    }

    private void startConnectionToServer() {
            this.network = new Network(hostAddress, hostPort, this);
    }

    @Override
    public void sendMessage(String message) {
        network.send(message);
    }



    @Override
    public void processRetrievedMessage(String messageIn) {
        Message message = Message.fromJson(messageIn);
        if(message.command.equals(Command.AUTH_MESSAGE)){
            printAuth(message);
            }
            else if(message.command.equals(Command.ADD_NEW) || (message.command.equals(Command.ADD_NEW_OK))){
                primaryController.newUserAction(message);
            }
                else if(message.command.equals(Command.CLIENT_LIST)){
                    ClientListMessage clientListMessage = message.clientListMessage;
                    primaryController.clientsList.setItems(FXCollections.observableArrayList(clientListMessage.online));
                    }
                        else {
                            printMessage(message);
                        }
    }


    private void printAuth(Message message){
        if(message.authMessage.message.equals(Command.AUTH_OK.toString())){
            isAuth = true;
            nickname = message.authMessage.nick;
            primaryController.nickName = nickname;
            primaryController.yourNickField.setText(nickname);
            primaryController.authPanel.setVisible(false);
            primaryController.chatPanel.setVisible(true);
            System.out.println("В этот момент появлется все");

            try {
                history = new WorkWithHistory(message.authMessage.login);
                String lastHistory = history.takeLastHistory();
                textArea.appendText(lastHistory);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textArea.appendText("Welcome to the chat!"+System.lineSeparator()+
                    (new Date(System.currentTimeMillis())).toString()+System.lineSeparator());

        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Authentication is failed");
            alert.setContentText(message.authMessage.message);
            alert.showAndWait();
        }
    }

    private void printMessage(Message message){
        String messagePrint;
        if(message.command.equals(Command.PUBLIC_MESSAGE)){
            messagePrint = message.publicMessage.from+": "+ message.publicMessage.message;
            textArea.appendText(messagePrint+System.lineSeparator());
            history.addHistory(messagePrint);
        }
        else if (message.command.equals(Command.PRIVATE_MESSAGE)){
            messagePrint ="Private message from " +message.privateMessage.from + ": " + message.privateMessage.message;
            textArea.appendText(messagePrint+System.lineSeparator());
            history.addHistory(messagePrint);
        }
        else if((message.changeNickMessage.oldNick.equals(nickname))&&
                (message.command.equals(Command.CHANGE_NICK)||message.command.equals(Command.CHANGE_NICK_OK))){
            System.out.println(message.toJson());
            if(message.command.equals(Command.CHANGE_NICK)){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Ошибка смены ника");
                alert.setContentText(message.changeNickMessage.message);
                alert.showAndWait();
            }
            else{
                primaryController.changeNickOk(message.changeNickMessage.newNick);
            }

        }
    }

    @Override
    public void close() {
//        if(needStopServerOnClosed){
            Message message = Message.creatEnd();
            sendMessage(message.toJson());
 //       }
        network.onClose();
    }

    public void addNewHistory(String message){
        history.addHistory(message);
    }
}
