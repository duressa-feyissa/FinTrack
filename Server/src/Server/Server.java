package Server;
import java.net.ServerSocket;
import java.net.Socket;
import ClientHandler.*;
import Common.Common;

public class Server {
    private ServerSocket serverSocket;
    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();

                // Handle client request in a separate thread
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            Common.path = args[0] + "/" + "secretkey.key";
            if (args.length == 2)
                port = Integer.parseInt(args[1]);
        }
        Server server = new Server(port);
        server.start();
    }
}
