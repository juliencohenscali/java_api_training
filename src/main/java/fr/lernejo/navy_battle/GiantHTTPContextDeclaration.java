package fr.lernejo.navy_battle;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class GiantHTTPContextDeclaration {

    HttpHandler tooBigHttpContextDeclaration = null;


    public GiantHTTPContextDeclaration(Map serverMap, Map clientMap, ToolMethod toolMethod, Server serverInstance){
        tooBigHttpContextDeclaration = httpExchange -> { //apiGameFire context declaration
            if (Objects.equals(httpExchange.getRequestMethod(), "GET"))
            {
                System.out.println("[*] ApiGameFire " + httpExchange.getRequestMethod() + " From:" + httpExchange.getRemoteAddress().getAddress() + ':' + httpExchange.getRemoteAddress().getPort());
                String targetedCell = httpExchange.getRequestURI().toString().split("[?]")[1].split("=")[1];
                System.out.println("[*] Shot in " + targetedCell);
                List<String> clientFireRes = serverMap.computeResult(targetedCell);
                String consequence = clientFireRes.get(0);
                boolean serverShipLeft = Boolean.parseBoolean(clientFireRes.get(1));
                boolean clientShipLeft = false;
                if (!serverShipLeft || serverMap.boatList.toArray().length == 0)    {System.out.println("[*] Server lost, END OF GAME !!");}
                else {
                    System.out.println("[*] Client shot " + clientFireRes.get(0) + ", server have " + serverMap.boatList.toArray().length + " boat remaining");
                    System.out.println(serverMap.boatList);
                    System.out.println();
                    String serverTargetedCell = toolMethod.chooseCase();
                    while (serverInstance.shotCase.contains(serverTargetedCell))   {serverTargetedCell = toolMethod.chooseCase();}
                    serverInstance.shotCase.add(serverTargetedCell);
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
                String res = serverInstance.createFireResponse(consequence, String.valueOf(finalRes));
                httpExchange.sendResponseHeaders(202, res.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) { // (1)
                    os.write(res.getBytes());}
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
                httpExchange.close();}
            System.out.println();
        };
    }
}
