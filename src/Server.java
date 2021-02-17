import fileutils.FileReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {



    public static void main(String[] args) {

        //ExecutorService är till för att inte behöva skapa massa trådar utan man återanvänder samma tråd varje gång
        ExecutorService exs = Executors.newCachedThreadPool();

        try {
            ServerSocket serverSocket = new ServerSocket(5050);
            System.out.println(Thread.currentThread());

            while (true) {

                Socket soc = serverSocket.accept();
                exs.execute(() -> handleConnection(soc));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleConnection(Socket soc) {

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(soc.getInputStream()));

            String url = readHeaders(input);

            var output = new PrintWriter(soc.getOutputStream());
//            String page = """
//                    <html>
//                    <head>
//                        <title>Hello World</title>
//                    </head>
//                    <body>
//                    <h1>Hello there!</h1>
//                    <div>First page</div>
//                    </body>
//                    </html>""";

            File file = new File("rec " + File.separator + url); //"src//rec//index.html"
            byte[] page = FileReader.readFromFile(file);

            String contentType = Files.probeContentType(file.toPath());

            output.println("HTTP/1.1 200 OK");
            //getBytes() är till för att om man har smileys eller liknande i kodstycket innan räknas de som 2 tecken om man bara skriver .lenght()
            output.println("Content-Length:" + page.length);
            output.println("Content-Type: " + contentType); //text/html
            output.println("");
            //output.print(Arrays.toString(page));

            output.flush();
            var dataOut = new BufferedOutputStream(soc.getOutputStream());
            dataOut.write(page);
            dataOut.flush();
            soc.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readHeaders(BufferedReader input) throws IOException {
        String requestedUrl = "";
        while(true) {
            String headerLine = input.readLine();
            if (headerLine.startsWith("GET")) {
                requestedUrl = headerLine.split(" ")[1];
            }
            System.out.println(headerLine);
            if (headerLine.isEmpty()) {
                break;
            }
        }
        return requestedUrl;
    }
}

class Todo {
    int id;
    String title;
    Boolean completed;

    public Todo(int id, String title, Boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }
}

class Todos {
    List<Todo> todos;
}
