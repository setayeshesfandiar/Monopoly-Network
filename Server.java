import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("Server starting...");
        ServerSocket server = new ServerSocket(65432);
        System.out.println("Waiting for players...");
        
        while (true) {
            Socket client = server.accept();
            System.out.println("New client connected!");
            
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            out.println("Welcome to Monopoly!");
            out.println("Enter your name:");
            
            String name = in.readLine();
            System.out.println("Player: " + name);
            out.println("Hello " + name + "!");
            
            client.close();
        }
    }
}
