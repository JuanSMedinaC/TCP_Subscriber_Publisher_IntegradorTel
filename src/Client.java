import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 6000);
        Runnable matchRecThread=() ->
        {
            while(true) {
                try {
                    byte[] bf = new byte[500];
                    socket.getInputStream().read(bf);
                    String rec = new String(bf, "UTF-8");
                    System.out.println(rec.trim());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        Thread runReader= new Thread(matchRecThread);
        runReader.start();

        BufferedReader sc=new BufferedReader(new InputStreamReader(System.in));

        while (true){
            String msg=sc.readLine();
            socket.getOutputStream().write(msg.getBytes("UTF-8"));
        }

    }
}

