package fr.lernejo.navy_battle;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class GiantHTTPContextDeclaration {


    public GiantHTTPContextDeclaration(){

    }
    public HttpHandler StupidGiantHTTPContextDeclaration(Map serverMap, Map clientMap, ToolMethod toolMethod, Server serverInstance, List<String> shotCase, List<List<String>> serverBoatList, char[][] serverGlobalMap, List<List<String>> clientBoatList, char[][] clientGlobalMap){
        return httpExchange -> {
            if (Objects.equals(httpExchange.getRequestMethod(), "GET"))
            {
                System.out.println("[*] ApiGameFire " + httpExchange.getRequestMethod() + " From:" + httpExchange.getRemoteAddress().getAddress() + ':' + httpExchange.getRemoteAddress().getPort());                String targetedCell = httpExchange.getRequestURI().toString().split("[?]")[1].split("=")[1];                System.out.println("[*] Shot in " + targetedCell);                List<String> clientFireRes = serverMap.computeResult(targetedCell, serverBoatList, serverGlobalMap);                String consequence = clientFireRes.get(0);                boolean endGame = false;                boolean serverShipLeft = Boolean.parseBoolean(clientFireRes.get(1));                boolean clientShipLeft = false;                if (!serverShipLeft || serverBoatList.toArray().length == 0)    {System.out.println("[*] Server lost, END OF GAME !!"); endGame = true;}                else {                    System.out.println("[*] Client shot " + clientFireRes.get(0) + ", server have " + serverBoatList.toArray().length + " boat remaining");                    System.out.println(serverBoatList);                    System.out.println();                    String serverTargetedCell = toolMethod.chooseCase();                    while (shotCase.contains(serverTargetedCell))   {serverTargetedCell = toolMethod.chooseCase();}                    shotCase.add(serverTargetedCell);                    List<String> serverFireRes = clientMap.computeResult(serverTargetedCell, clientBoatList, clientGlobalMap);                    clientShipLeft = Boolean.parseBoolean(serverFireRes.get(1));                    if (!clientShipLeft || clientBoatList.toArray().length == 0){                        System.out.println("[*] Client lost, END OF GAME !!");                        endGame = true;                    }                    else                    {                        System.out.println("[*] Server shot " + serverFireRes.get(0) + ", client have " + clientBoatList.toArray().length + " boat remaining");                        System.out.println(clientBoatList);                    }                }                boolean finalRes = clientShipLeft;                sendRq(httpExchange, serverInstance, consequence, finalRes, endGame);
            }
            else
            {badRqMethod(httpExchange);}
            System.out.println();
        };
    }

    public void sendRq(HttpExchange httpExchange, Server serverInstance, String consequence, boolean finalRes, boolean endGame) throws IOException {
        String res = serverInstance.createFireResponse(consequence, String.valueOf(finalRes));
        httpExchange.getResponseHeaders().add("Content-Type","application/json");
        httpExchange.sendResponseHeaders(202, res.getBytes().length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(res.getBytes());}
        System.out.println();
        if (endGame){
            System.exit(0);
        }
        httpExchange.close();
    }

    public void badRqMethod(HttpExchange httpExchange) throws IOException {
        Headers head = httpExchange.getRequestHeaders();
        System.out.println("[!] POST request to ApiGameFire From:" + head.get("Host"));
        String res = "this context only accepts GET request\n";
        httpExchange.sendResponseHeaders(404, res.getBytes().length);
        httpExchange.getResponseBody().write(res.getBytes());
        httpExchange.close();
    }
}
