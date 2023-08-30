import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Session implements Runnable{
    private Socket socket;

    private ArrayList<String> subscribedTopics = new ArrayList<>();
    public Session(Socket socket){
        this.socket=socket;
    }

    @Override
    public void run() {
            while (true) {
                try {
                    byte[] bf = new byte[500];
                    socket.getInputStream().read(bf);
                    String rec = new String(bf, "UTF-8");
                    rec = rec.trim();

                    String[] parts = rec.split(" ");
                    String command = parts[0];
                    String topic;
                    String msg = "";
                    if (command.equalsIgnoreCase("subscribe")) {
                        try {
                            topic = parts[1];
                            if (Server.checkTopicExistence(topic)) {
                                if (isSubscribedTo(topic)) {
                                    socket.getOutputStream().write("Already subscribed to topic".getBytes());
                                } else {
                                    subscribe(topic);
                                }
                            } else {
                                Server.addTopic(topic);
                                subscribe(topic);
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                            socket.getOutputStream().write("You must subscribe to a topic following the next format: subscribe <topic>".getBytes());
                        }
                    } else if (command.equalsIgnoreCase("send")) {
                        try {
                            topic = parts[1];
                            for (int i = 2; i < parts.length; i++) {
                                msg += " " + parts[i];
                            }

                            if (!Server.sendToTopic(topic, msg)) {
                                socket.getOutputStream().write("Topic can't be found".getBytes());
                            }

                        } catch (ArrayIndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                            socket.getOutputStream().write("You must send a message in the next format: send <topic> <message>".getBytes());
                        }
                    }else{
                        socket.getOutputStream().write("Can not find the specified command".getBytes());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
    }

    public boolean isSubscribedTo(String channel){
        boolean found=false;
        for (int i = 0; i< subscribedTopics.size(); i++){
            if(channel.equalsIgnoreCase(subscribedTopics.get(i))){
                found=true;
            }
        }
        return found;
    }

    public void broadcastMessageFromTopic(String topic, String message){
        try {
            socket.getOutputStream().write((topic+":"+message).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribe(String topic){
        subscribedTopics.add(topic);
    }

}
