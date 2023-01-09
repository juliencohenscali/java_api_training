package fr.lernejo.navy_battle;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static java.net.http.HttpClient.newHttpClient;

class Client{


    public Client(String[] args) throws IOException, InterruptedException, ParseException {
        List<String> shotCase = new ArrayList<>();
        List<Object> fireRes = new ArrayList<>();
        ToolMethod toolMethod = new ToolMethod();
        String id = toolMethod.genId();
        sendStartRequest(id, args);
        boolean isBoatRemaining = true;
        int lapCount = 1;
        while (isBoatRemaining)     {
            fireRes = sendFireRequest(id, args, toolMethod, lapCount, shotCase);
            shotCase = (List<String>) fireRes.get(0);
            isBoatRemaining = (boolean) fireRes.get(1);
            lapCount++;
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
    private List<Object> sendFireRequest(String id, String[] args, ToolMethod toolMethod, int lapCount, List<String> shotCase) throws IOException, ParseException, InterruptedException {
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
        Thread.sleep(40);
        List<Object> res = new ArrayList<>();
        res.add(shotCase);
        res.add(Boolean.parseBoolean(shipLeft));
        return res;
    }

    public HttpRequest createFireRequest(String id, String url, String chosenCase) throws ParseException {
        if ((chosenCase.charAt(0)) >= 65 && chosenCase.charAt(0) <= 90){
            if (chosenCase.charAt(1) == 0){
                chosenCase = "Z2";}}
        else {
            chosenCase = "Z1";}
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
