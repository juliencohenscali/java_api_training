package fr.lernejo.navy_battle;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
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

    public Server(String[] args) throws IOException, InterruptedException {
        Map serverMap = new Map();
        Map clientMap = new Map();
        List<String> shotCase = new ArrayList<>();
        List<Object> clientGlobalMap = clientMap.flushMap();
        List<Object> serverGlobalMap = serverMap.flushMap();
        ToolMethod toolMethod = new ToolMethod();
        String id = toolMethod.genId();
        InetSocketAddress address = new InetSocketAddress("localhost", Integer.parseInt(args[0]));
        HttpServer httpServer = HttpServer.create(address, 1);ExecutorService executor = Executors.newFixedThreadPool(1);httpServer.setExecutor(executor);
        System.out.println("Starting on port " + args[0] +"...");
        HttpHandler httpHandlerPing = httpExchange -> { Headers head = httpExchange.getRequestHeaders(); System.out.println("[*] Ping From:" + head.get("Host"));String response = "OK";httpExchange.sendResponseHeaders(200, response.getBytes().length);httpExchange.getResponseBody().write(response.getBytes());httpExchange.close();};
        HttpHandler httpHandlerApiGameStart = httpExchange -> { if (Objects.equals(httpExchange.getRequestMethod(), "POST")) {            System.out.println(new String(httpExchange.getRequestBody().readAllBytes()));System.out.println("[*] ApiGameStart " + httpExchange.getRequestMethod() + " From:" + httpExchange.getRemoteAddress().getAddress() + ':' + httpExchange.getRemoteAddress().getPort());System.out.println();serverMap.flushMap();clientMap.flushMap();shotCase.clear();String res = "{\n    \"id\": \"" + id + "\",\n    \"url\": \"http://localhost:" + args[0] + "\",\n    \"message\": \"May the best code win\"\n}";httpExchange.getResponseHeaders().add("From","server");httpExchange.sendResponseHeaders(202, res.getBytes().length);httpExchange.getResponseBody().write(res.getBytes());try (OutputStream os = httpExchange.getResponseBody()) {     os.write(res.getBytes());}httpExchange.close();            }            else{ Headers head = httpExchange.getRequestHeaders();System.out.println("[!] GET request to ApiGameStart From:" + head.get("Host"));String res = "this context only accepts POST request\n";httpExchange.sendResponseHeaders(404, res.getBytes().length);httpExchange.getResponseBody().write(res.getBytes());httpExchange.close();            }        };
        GiantHTTPContextDeclaration stupidClass = new GiantHTTPContextDeclaration();
        HttpHandler httpHandlerApiGameFire = stupidClass.StupidGiantHTTPContextDeclaration(serverMap, clientMap, toolMethod, this, shotCase, (List<List<String>>) serverGlobalMap.get(1), (char[][]) serverGlobalMap.get(0), (List<List<String>>) clientGlobalMap.get(1), (char[][]) clientGlobalMap.get(0));;
        httpServer.createContext("/ping", httpHandlerPing); httpServer.createContext("/api/game/start", httpHandlerApiGameStart); httpServer.createContext("/api/game/fire", httpHandlerApiGameFire); httpServer.start();
    }
    public String createFireResponse(String result, String boatLeft){
        return "{\n    \"consequence\": \"" + result + "\",\n    \"shipLeft\": " + boatLeft +"\n}";
    }
}
