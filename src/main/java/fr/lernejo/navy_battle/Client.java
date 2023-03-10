package fr.lernejo.navy_battle;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.net.http.HttpClient.newHttpClient;

class Client{


    public Client(String[] args) throws IOException, InterruptedException {

    }

    public boolean fireUsage(String[] args,String port) throws IOException, InterruptedException {
        List<String> shotCase = new ArrayList<>();
        ToolMethod toolMethod = new ToolMethod();
        String id = toolMethod.genId();
        int lapCount = 1;
        List<Object> m = sendFireRequest(id, args, toolMethod, lapCount, shotCase, port);
        return (true);
    }

    public void sendStartRequest(String[] args) throws IOException, InterruptedException {
        ToolMethod toolMethod = new ToolMethod();
        String id = toolMethod.genId();
        sendStartRequest(id, args);
    }

    private void sendStartRequest(String id, String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        HttpRequest request = createStartRequest(id, args);
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(resp.headers().map().get("date"));
        System.out.println(resp.body());
        System.out.println("Sent Start\n");
    }
    public List<Object> sendFireRequest(String id, String[] args, ToolMethod toolMethod, int lapCount, List<String> shotCase, String port) throws IOException, InterruptedException {
        String chosenCase = toolMethod.chooseCase();
        while (shotCase.contains(chosenCase))   {chosenCase = toolMethod.chooseCase();}
        shotCase.add(chosenCase);
        HttpClient client = newHttpClient();
        HttpRequest request = null;
        if (args.length == 2){request = createFireRequest(id, args[1], chosenCase, args);}
        else {request = createFireRequest(id, "http://localhost:" + port, chosenCase, args);}
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        String consequence = (resp.body().replace("\n", "").replace(" ", "").replace("{","").replace("}","").split(",")[0].split(":")[1].replace("\"",""));
        String shipLeft = (resp.body().replace("\n", "").replace(" ", "").replace("{","").replace("}","").split(",")[1].split(":")[1]);
        System.out.println(resp.headers().map().get("date"));System.out.println("[*] The shot in " + chosenCase + " is a " + consequence + " and radar detection return " + shipLeft);Thread.sleep(50);
        List<Object> res = new ArrayList<>();
        res.add(shotCase);res.add(Boolean.parseBoolean(shipLeft));
        return res;
    }

    public HttpRequest createFireRequest(String id, String url, String chosenCase, String[] args) {
        if ((chosenCase.charAt(0)) >= 65 && chosenCase.charAt(0) <= 90){
            if (chosenCase.charAt(1) == 0){
                chosenCase = "Z2";}}
        else {
            chosenCase = "Z1";}
        return HttpRequest.newBuilder()
            .uri(URI.create(url + "/api/game/fire?cell=" + chosenCase))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .setHeader("Hook",args[0])
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }
    public HttpRequest createStartRequest(String id, String[] args) {
        return HttpRequest.newBuilder()
            .uri(URI.create(args[1] + "/api/game/start"))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                "    \"id\": \"" + id + "\",\n" +
                "    \"url\": \"http://localhost:" + args[0] + "\",\n" +
                "    \"message\": \"I will crush you!\"\n" +
                "}"))
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }
}
