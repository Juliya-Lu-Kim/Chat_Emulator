import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    final static String address = "localhost";
    final static int port = 1000;
    final static List<Socket> socketList = new ArrayList<>();
    static PrintWriter outServer;
    static BufferedReader inServer;
    static Socket socket;

    // Подключение входного потока в зависимости от сокета
    public static BufferedReader socketIn(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Подключение выходного потока в зависимости от сокета
    public static PrintWriter socketOut(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream());
    }

    public static void main(String[] args) throws IOException {

        // Создние сокета сервера
        try (
                ServerSocket server = new ServerSocket(Server.port);
                Scanner scanner = new Scanner(System.in);
        ) {

            while (true) {
                String message = "Type Exit to exit";
                // Ожидание подключения обоих абонентов
                while (socketList.size() < 2) {
                    socket = server.accept();
                    socketList.add(socket);
                    inServer = socketIn(socket);
                    System.out.println("Client " + inServer.readLine() + " connected");
                    outServer = socketOut(socket);
                    outServer.println("Client: " + message);
                    outServer.flush();
                }
                int i = 1;

                // Реализация чата двух абонентов
                while (socketList.size()==2) {
                    if (socketIn(socketList.get(0)).ready()) {
                        inServer = socketIn(socketList.get(0));
                        message = inServer.readLine();
                        System.out.println("Client: " + message + " : " + socketList.get(0).getInetAddress() + " / " +
                                LocalDateTime.now());
                        outServer = socketOut(socketList.get(i));
                        if (message.equalsIgnoreCase("exit")) {
                            System.out.println(socketList.get(0).getInetAddress() + " was disconnect " + LocalDateTime.now());
                            outServer.println("Your subscriber was disconnect");
                            outServer.flush();
                            socketList.remove(0);
                            i = 0;
                        }
                        outServer.println(message);
                        outServer.flush();
                    }
                    if (socketIn(socketList.get(i)).ready()) {
                        inServer = socketIn(socketList.get(i));
                        message = inServer.readLine();
                        System.out.println("Client: " + message + " : " + socketList.get(i).getInetAddress() + " / " +
                                LocalDateTime.now());
                        outServer = socketOut(socketList.get(0));
                        if (message.equalsIgnoreCase("exit")) {
                            System.out.println(socketList.get(0).getInetAddress() + " was disconnect " + LocalDateTime.now());
                            outServer.println("Your subscriber was disconnect");
                            outServer.flush();
                            socketList.remove(i);
                            i = 0;
                        }
                        outServer.println(message);
                        outServer.flush();
                    }
                }

                // реализация обмена сообщениями между сервером и абонентом,
                // в случае выхода из чата второго абонента
                while (true){
                    i=0;
                    outServer = socketOut(socketList.get(i));
                    inServer = socketIn(socketList.get(i));
                    if(inServer.ready()){
                        if(inServer.readLine().equalsIgnoreCase("exit")){
                            System.out.println(socketList.get(i).getInetAddress() + " was disconnect " + LocalDateTime.now());
                            socketList.remove(i);
                            if(socketList.isEmpty()) {
                                System.out.println("Is server disconnect ? Y/N" );
                                if(scanner.nextLine().equalsIgnoreCase("Y")) {
                                    message = "Y";
                                    break;
                                } else break;
                            }
                        }
                        outServer.println("Only server  connected. Type Exit to exit");
                        outServer.flush();
                    }
                }
                if(message.equalsIgnoreCase("Y")) break;
            }

            //Закрытие сервера. Закрытия потоков.
            inServer.close();
            outServer.close();
        }
    }
}

