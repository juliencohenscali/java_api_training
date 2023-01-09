package fr.lernejo.navy_battle;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Flow;

import static java.net.http.HttpClient.newHttpClient;

class Client{

    List<String> shotCase = new ArrayList<>();
    int lapCount = 0;

    public Client(String[] args) throws IOException, InterruptedException, ParseException {
        ToolMethod toolMethod = new ToolMethod();
        String id = toolMethod.genId();
        sendStartRequest(id, args);
        boolean isBoatRemaining = true;
        while (isBoatRemaining)     {
            isBoatRemaining = sendFireRequest(id, args, toolMethod);
        }
    }

    private void sendStartRequest(String id, String[] args) throws IOException, InterruptedException, ParseException {
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        HttpRequest request = createStartRequest(id, args[1]);
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(resp.headers().map().get("date"));
        System.out.println(resp.body());
        System.out.println("Sent Start\n");
    }
    private boolean sendFireRequest(String id, String[] args, ToolMethod toolMethod) throws IOException, ParseException, InterruptedException {
        String chosenCase = toolMethod.chooseCase();
        while (shotCase.contains(chosenCase))   {chosenCase = toolMethod.chooseCase();}
        shotCase.add(chosenCase);
        HttpClient client = newHttpClient();
        HttpRequest request = createFireRequest(id, args[1], chosenCase);
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

        String consequence = (resp.body().replace("\n", "").replace(" ", "").replace("{","").replace("}","").split(",")[0].split(":")[1].replace("\"",""));
        String shipLeft = (resp.body().replace("\n", "").replace(" ", "").replace("{","").replace("}","").split(",")[1].split(":")[1]);
        System.out.println(resp.headers().map().get("date"));
        System.out.println("The shot " + consequence + " and radar detection return " + shipLeft);
        System.out.println("Sent Fire, Shot n°: " + lapCount + " \n");
        lapCount++;
        Thread.sleep(40);
        return Boolean.parseBoolean(shipLeft);
    }

    public HttpRequest createFireRequest(String id, String url, String chosenCase) throws ParseException {
        if ((chosenCase.charAt(0)) >= 65 && chosenCase.charAt(0) <= 90){
            if (chosenCase.charAt(1) == 0){
                chosenCase = "Z2";
            }
        }
        else {
            chosenCase = "Z1";
        }
        return HttpRequest.newBuilder()
            .uri(URI.create(url + "/api/game/fire?cell=" + chosenCase))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }

    public HttpRequest createStartRequest(String id, String url) throws ParseException {
        return HttpRequest.newBuilder()
            .uri(URI.create(url + "/api/game/start"))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                "    \"id\": \"" + id + "\",\n" +
                "    \"url\": \"" + url + "\",\n" +
                "    \"message\": \"I will crush you!\"\n" +
                "}"))
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }
}
