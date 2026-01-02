import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 65432);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);
        
        System.out.println("From server: " + in.readLine());
        System.out.println("From server: " + in.readLine());
        
        System.out.print("Your name: ");
        String name = sc.nextLine();
        out.println(name);
        
        System.out.println("From server: " + in.readLine());
        
        s.close();
    }
}
