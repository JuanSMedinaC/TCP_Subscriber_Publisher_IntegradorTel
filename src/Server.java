import java.io.IOException;
import java.net.*;
import java.time.Instant;
import java.util.*;

public class Server {
    private static ArrayList<Session> sessions=new ArrayList<>();
    private static ArrayList<String> topics = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(6000);
        while(true){
            System.out.println("Esperando Cliente");
            Socket socketClient=server.accept();
            System.out.println("Cliente conectado");
            Session session=new Session(socketClient);
            new Thread(session).start();
            sessions.add(session);
        }
    }

    public static boolean checkTopicExistence(String topic){
        boolean exists=false;
        for (int i=0; i<topics.size();i++){
            if (topic.equalsIgnoreCase(topics.get(i))){
                exists=true;
            }
        }
        return exists;
    }

    public static boolean sendToTopic(String topic,String msg){
        boolean sent = false;
        if (checkTopicExistence(topic)){
            for (int i =0 ; i<sessions.size();i++){
                if (sessions.get(i).isSubscribedTo(topic)){
                    sessions.get(i).broadcastMessageFromTopic(topic,msg);
                }
            }
            sent=true;
        }
        return sent;
    }

    public static void addTopic(String topic){
        topics.add(topic);
    }

}