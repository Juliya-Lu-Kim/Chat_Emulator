import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class FirstClient {
    public static String name ;

    public FirstClient(String name) {
        this.name = name;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        FirstClient.name = name;
    }

    public static void main(String[] args) throws IOException {

        //Созднание сокета подключения абонента к серверу (адрес задан)
        try (
                Socket socket = new Socket(Server.address, Server.port);
                PrintWriter outClient = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in))
        ){
            //Ввод логина абонента для начала чата
            System.out.println("Input your login :");
            String msg = scanner.readLine();
            if(msg.equalsIgnoreCase("")) {
                msg = "Guest";
            }
            setName(msg);
            System.out.println("Connect :" + msg);
            outClient.println("Connect :" + msg + " : " + socket.getInetAddress());
            outClient.flush();
            msg = inClient.readLine();
            System.out.println(msg);

            // Реализация отправки и получения сообщений
            while (true){
                if(inClient.ready()){
                    System.out.println("*****");
                    msg = inClient.readLine();
                    System.out.println(msg);
                    System.out.println("*****");
                }
                if(scanner.ready()) {
                    System.out.println("-----");
                    msg = scanner.readLine();
                    if (msg.equalsIgnoreCase("Exit")) {
                        outClient.println(msg);
                        outClient.flush();
                        break;
                    }
                    outClient.println(msg);
                    outClient.flush();
                    System.out.println("-----");
                }
            }
        }

    }
}
