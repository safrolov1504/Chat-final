package serverChat;

import networkChatCommons.Message;
import networkChatCommons.variosOfMessage.PrivateMessage;
import serverChat.auth.AuthServer;
import serverChat.auth.BaseAuthServer;
import serverChat.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.*;

public class MyServer {
    private static final int PORT = 8189;
    private final AuthServer authServer=new BaseAuthServer();
    private List<ClientHandler> clients = new ArrayList<>();
    public static Logger admin = Logger.getLogger("admin");
    public static Logger file = Logger.getLogger("file");

    public AuthServer getAuthServer() {
        return authServer;
    }

    //запуск сервера
    public MyServer() {
        //System.out.println("server is running");
        admin.info("server is running");
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            authServer.start();
            while (true){
                admin.info("Waiting for client");
                Socket socket = serverSocket.accept();
                admin.info("Client has connected");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            admin.error("Error server. Reason: "+ e.getMessage());
            //System.err.println("Error server. Reason: "+ e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            admin.error("SQL error: "+e.getMessage());
            //System.out.println("SQL error: "+e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                authServer.stop();
            } catch (SQLException e) {
                admin.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastClientsList();
    }


    private void broadcastClientsList() {
        List<String> nicknames = new ArrayList<>();
        for (ClientHandler client:clients) {
            nicknames.add(client.getClientName());
        }
        Message message = Message.creatClientList(nicknames);
        broadcastMessage(message.toJson());
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler client : clients) {
            if(client.getClientName().equals(nick)){
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String message,ClientHandler... unfilteredClients) {
        List<ClientHandler> unfiltered = Arrays.asList(unfilteredClients);
        //System.out.println("send: "+message);
        for (ClientHandler client : clients) {
            if(!unfiltered.contains(client)){
                client.SendMessage(message);
            }
        }
    }

    public synchronized void sendPrivateMessage(String sendLogin, String recievedLogin, String message, ClientHandler... unfilteredClients) {
        List<ClientHandler> unfiltered = Arrays.asList(unfilteredClients);
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.from = sendLogin;
        privateMessage.to = recievedLogin;
        privateMessage.message = message;

        Message msg = Message.creatPrivate(privateMessage);
        for (ClientHandler client:clients) {
            if(client.getClientName().equals(recievedLogin)&& (!unfiltered.contains(client))){
                client.SendMessage(msg.toJson());
                break;
            }
        }
    }
}
