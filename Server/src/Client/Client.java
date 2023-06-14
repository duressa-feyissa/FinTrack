package Client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import Budget.*;
import Common.Common;
import Expense.Expense;
import Users.*;
import Income.*;
import org.json.JSONObject;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Specify the server's IP address
        int serverPort = 8080; // Specify the server's port

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine();

            System.out.println("Response from server: " + response);

            writer.close();
            reader.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

