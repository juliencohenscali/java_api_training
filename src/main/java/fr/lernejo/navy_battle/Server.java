package fr.lernejo.navy_battle;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Server{

    List<String> shotCase = new ArrayList<>();
    public Map serverMap = new Map();
    public Map clientMap = new Map();
    public Server(String[] args) throws IOException, InterruptedException {
        ToolMethod toolMethod = new ToolMethod();
        String id = toolMethod.genId();

        InetSocketAddress address = new InetSocketAddress("localhost", Integer.parseInt(args[0])); HttpServer httpServer = HttpServer.create(address, 1); ExecutorService executor = Executors.newFixedThreadPool(1);httpServer.setExecutor(executor); System.out.println("Starting on port " + args[0] +"...");
        //Http Server Config

        HttpHandler httpHandlerPing = httpExchange -> { Headers head = httpExchange.getRequestHeaders(); System.out.println("[*] Ping From:" + head.get("Host"));String response = "ok\n";httpExchange.sendResponseHeaders(200, response.getBytes().length);httpExchange.getResponseBody().write(response.getBytes());httpExchange.close();};
        //Ping context declaration


        HttpHandler httpHandlerApiGameStart = httpExchange -> {
            if (Objects.equals(httpExchange.getRequestMethod(), "POST"))
            //Context in case of POST request
            {
                System.out.println(new String(httpExchange.getRequestBody().readAllBytes()));
                System.out.println("[*] ApiGameStart " + httpExchange.getRequestMethod() + " From:" + httpExchange.getRemoteAddress().getAddress() + ':' + httpExchange.getRemoteAddress().getPort());
                System.out.println();
                try {
                    serverMap = new Map();
                    clientMap = new Map();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String res = "{\n" +
                    "    \"id\": \"" + id + "\",\n" +
                    "    \"url\": \"http://localhost:" + args[0] + "\",\n" +
                    "    \"message\": \"May the best code win\"\n" +
                    "}";
                httpExchange.getResponseHeaders().add("From","server");
                httpExchange.sendResponseHeaders(202, res.getBytes().length);
                httpExchange.getResponseBody().write(res.getBytes());
                try (OutputStream os = httpExchange.getResponseBody()) { // (1)
                    os.write(res.getBytes());
                }
                httpExchange.close();

            }
            else
            //Context in case of other Method request
            {Headers head = httpExchange.getRequestHeaders();System.out.println("[!] GET request to ApiGameStart From:" + head.get("Host"));String res = "this context only accepts POST request\n";httpExchange.sendResponseHeaders(404, res.getBytes().length);httpExchange.getResponseBody().write(res.getBytes());httpExchange.close();}
        };// Start context declaration

        HttpHandler httpHandlerApiGameFire = httpExchange -> { //apiGameFire context declaration
            if (Objects.equals(httpExchange.getRequestMethod(), "GET"))
            {
                //ask if boat hit
                Headers head = httpExchange.getRequestHeaders();
                System.out.println("[*] ApiGameFire " + httpExchange.getRequestMethod() + " From:" + httpExchange.getRemoteAddress().getAddress() + ':' + httpExchange.getRemoteAddress().getPort());
                String targetedCell = httpExchange.getRequestURI().toString().split("[?]")[1].split("=")[1];
                System.out.println("[*] Shot in " + targetedCell);

                List<String> clientFireRes = serverMap.computeResult(targetedCell);
                String consequence = clientFireRes.get(0);
                boolean serverShipLeft = Boolean.parseBoolean(clientFireRes.get(1));
                boolean clientShipLeft = false;

                if (!serverShipLeft || serverMap.boatList.toArray().length == 0)
                {
                    System.out.println("[*] Server lost, END OF GAME !!");

                    //System.exit(0);
                }
                else {
                    System.out.println("[*] Client shot " + clientFireRes.get(0) + ", server have " + serverMap.boatList.toArray().length + " boat remaining");
                    System.out.println(serverMap.boatList);
                    System.out.println();
                    String serverTargetedCell = toolMethod.chooseCase();
                    while (shotCase.contains(serverTargetedCell))   {serverTargetedCell = toolMethod.chooseCase();}
                    shotCase.add(serverTargetedCell);
                    List<String> serverFireRes = clientMap.computeResult(serverTargetedCell);
                    clientShipLeft = Boolean.parseBoolean(serverFireRes.get(1));
                    if (!clientShipLeft || clientMap.boatList.toArray().length == 0){
                        System.out.println("[*] Client lost, END OF GAME !!");
                    }
                    else
                    {
                        System.out.println("[*] Server shot " + serverFireRes.get(0) + ", client have " + clientMap.boatList.toArray().length + " boat remaining");
                        System.out.println(clientMap.boatList);

                    }
                }

                boolean finalRes = clientShipLeft;
                String res = createFireResponse(consequence, String.valueOf(finalRes));
                httpExchange.sendResponseHeaders(202, res.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) { // (1)
                    os.write(res.getBytes());
                }

                System.out.println();
                httpExchange.close();
            }
            else
            {
                Headers head = httpExchange.getRequestHeaders();
                System.out.println("[!] POST request to ApiGameFire From:" + head.get("Host"));

                String res = "this context only accepts GET request\n";
                httpExchange.sendResponseHeaders(404, res.getBytes().length);
                httpExchange.getResponseBody().write(res.getBytes());

                httpExchange.close();
            }
            System.out.println();
        };


        httpServer.createContext("/ping", httpHandlerPing);
        httpServer.createContext("/api/game/start", httpHandlerApiGameStart);
        httpServer.createContext("/api/game/fire", httpHandlerApiGameFire);
        httpServer.start();
    }


    public String createFireResponse(String result, String boatLeft){
        return "{\n" +
            "    \"consequence\": \"" + result + "\",\n" +
            "    \"shipLeft\": " + boatLeft +"\n" +
            "}";
    }
}

