import java.net.*;
import java.io.*;

public class ServerSimple {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(8888);
        System.out.println("Server ready on port 8888");
        
        while (true) {
            Socket s = ss.accept();
            System.out.println("Client connected");
            
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            pw.println("Hello from server!");
            String input = br.readLine();
            System.out.println("Client said: " + input);
            
            s.close();
        }
    }
}
