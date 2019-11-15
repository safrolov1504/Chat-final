package clientChat.messageServer;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import networkChatCommons.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private final String serverAddress;
    private final int port;
    private final IMessageService messageService;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    //обеспечивает сетевое взаимодействие
    public Network(String serverAddress, int port, IMessageService messageService)  {
        this.serverAddress = serverAddress;
        this.port = port;
        this.messageService = messageService;
    }

    private void initNetworkState(String serverAddress, int port) throws IOException {
        //открываем socket с указание порта и адреса
        this.socket=new Socket(serverAddress,port);
        //потоки создаются для обмена данными между сервером и клиентом
        this.inputStream=new DataInputStream(socket.getInputStream());
        this.outputStream=new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            while (true){
                try {
                    //ждет сообщения
                    String message = inputStream.readUTF();
                    Platform.runLater(()->messageService.processRetrievedMessage(message));
                    //messageService.processRetrievedMessage(message);
                } catch (IOException e) {
                    System.exit(0);;
                }
            }
        }).start();
    }

    public void send(String message) {
        try {
            if(outputStream == null){
                initNetworkState(serverAddress,port );
            }
            outputStream.writeUTF(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Connection is failed");
            alert.setContentText("Нет подключения в серверу");
            alert.showAndWait();
        }
    }

    public void onClose(){
        try {
            Message message = Message.creatEnd();
            outputStream.writeUTF(message.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}