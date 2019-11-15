package clientChat.interfaceMy;

import clientChat.messageServer.IMessageService;
import clientChat.messageServer.ServiceMassage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import networkChatCommons.*;
import networkChatCommons.variosOfMessage.*;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public final long TIMEOUT = 120_000;

    public @FXML TextField enterText;
    public @FXML Button button;
    public @FXML TextArea textArea;
    public @FXML TextArea yourNickField;
    public @FXML TextField loginField;
    public @FXML ListView<String> clientsList;

    public @FXML PasswordField passField;
    public @FXML FlowPane authPanel;
    public @FXML HBox authPanelIn;
    public @FXML HBox chatPanel;
    public @FXML VBox timeOutBox;
    public @FXML Button NewUser;


    public @FXML TextField nickFieldNew;
    public @FXML TextField loginFieldNew;
    public @FXML PasswordField passFieldNew;
    public @FXML Button back;
    public @FXML VBox newUserBox;

    public Button changeNickInChat;
    public VBox chanNickBox;
    public TextField nickFieldChangeOld;
    public TextField nickFieldChangNew;

    private IMessageService messageService;
    public String nickName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        try {
            this.messageService = new ServiceMassage(this);

            Thread timeOutThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(TIMEOUT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!messageService.isAuth()){
                        timeOutBox.setVisible(true);
                        authPanel.setVisible(false);
                    }
                }
            });
            timeOutThread.setDaemon(true);
            timeOutThread.start();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Server connection error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    public void buttonAction(ActionEvent actionEvent) {
        sendMessage();
    }

    @FXML
    public void addText(ActionEvent actionEvent) {
        sendMessage();
    }

    @FXML
    public void buttonSendAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passField.getText();
        AuthMessage msg = new AuthMessage();
        msg.login = login;
        msg.password =password;
        Message authMsg = Message.creatAuth(msg);
        messageService.sendMessage(authMsg.toJson());
    }

    @FXML
    public void TimeOutExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    private void sendMessage (){
        String message = enterText.getText();
        textArea.appendText("Я: " + message+System.lineSeparator());

        Message msg = buildMessage(message);

        messageService.sendMessage(msg.toJson());
        enterText.clear();
    }

    private Message buildMessage(String message) {
        String selectedNickname = clientsList.getSelectionModel().getSelectedItem();
        if (selectedNickname !=null){
            return buildPrivateMessage(selectedNickname, message);
        }
        return buildPublicMessage(message);
    }

    private Message buildPrivateMessage(String selectedNickname, String message){
        PrivateMessage privateMsg = new PrivateMessage();
        privateMsg.from = nickName;
        privateMsg.to = selectedNickname;
        privateMsg.message = message;
        return Message.creatPrivate(privateMsg);
    }
    private Message buildPublicMessage(String message) {
        PublicMessage publicMsg  =new PublicMessage();
        publicMsg.from =nickName;
        publicMsg.message = message;
        return Message.creatPublic(publicMsg);
    }

    public void shutdown()  {
        try {
            messageService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void creatNewUser(ActionEvent actionEvent) {
        authPanel.setVisible(false);
        System.out.println("Здесь создается новый юзер");
        newUserBox.setVisible(true);
    }

    public void addNewUser(ActionEvent actionEvent) {
        String nickNew = nickFieldNew.getText();
        String loginNew  = loginFieldNew.getText();
        String passNew = passFieldNew.getText();
        if(!nickNew.equals("") && !loginNew.equals("") && !passNew.equals("")){
            NewUser newUser = new NewUser();
            newUser.nick = nickNew;
            newUser.login = loginNew;
            newUser.password = passNew;

            Message newUserMessage = Message.creatNewUser(Command.ADD_NEW,newUser);
            System.out.println(newUserMessage.toJson());
            messageService.sendMessage(newUserMessage.toJson());
        }
        else {
            String additionErr="";
            if(nickNew.equals("")) additionErr=additionErr+ "Ник ";
            if(loginNew.equals("")) additionErr = additionErr+ "Логин ";
            if (passNew.equals("")) additionErr = additionErr + "Пароль ";
            additionErr=additionErr+ "не может быть пустым!";
            String errMessage = "Ошибка добавление новго пользователя";
            showError(errMessage, additionErr);
        }
    }

    public void showError(String errMessage, String additionErr){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(errMessage);
        alert.setContentText(additionErr);
        alert.showAndWait();
    }

    public void ButtonBack(ActionEvent actionEvent) {
        newUserBox.setVisible(false);
        authPanel.setVisible(true);
    }

    //просто смена окон
    public void changeNickInChan(ActionEvent actionEvent) {
        chatPanel.setVisible(false);
        System.out.println("здесь меняется ник");
        chanNickBox.setVisible(true);
    }

    public void ButtonBackChangeNick(ActionEvent actionEvent) {
        chanNickBox.setVisible(false);
        chatPanel.setVisible(true);
    }

    public void changeNick(ActionEvent actionEvent) {
        String nickFieldChangeOldIn = nickFieldChangeOld.getText();
        String nickFieldChangNewIn = nickFieldChangNew.getText();
        if((nickFieldChangeOldIn.equals(""))|| (nickFieldChangNewIn.equals(""))) {
            showError("Ошибка смены ника!", "Заполните все поля");
        }   else if(!nickFieldChangeOldIn.equals(nickName)){
            showError("Ошибка смены ника!", "Это не Ваш ник, сменить его невозможно!");
        }
        else {
            ChangeNickMessage changeNickMessage = new ChangeNickMessage();
            changeNickMessage.newNick = nickFieldChangNewIn;
            changeNickMessage.oldNick = nickFieldChangeOldIn;

            Message message = Message.creatNewUserOk(Command.CHANGE_NICK,changeNickMessage);
            System.out.println(message.toJson());
            messageService.sendMessage(message.toJson());
            System.out.println("по этой кнопке меняется ник");
        }

    }

    public void newUserAction(Message message) {
        //System.out.println(message.toJson());
        if(message.command.equals(Command.ADD_NEW_OK)){
            showError(message.newUser.message,"nick: "+message.newUser.nick + " login: "+message.newUser.login);
            newUserBox.setVisible(false);
            authPanel.setVisible(true);
        }
        else
            showError("Ошибка в добавление ника!", message.newUser.message);
    }

    public void changeNickOk(String newNick) {
        nickName = newNick;
        chanNickBox.setVisible(false);
        yourNickField.setText(newNick);
        chatPanel.setVisible(true);
    }
}
