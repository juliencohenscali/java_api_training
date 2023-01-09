package fr.lernejo.navy_battle;
import com.google.gson.reflect.TypeToken;
import org.json.simple.*;
import com.sun.net.httpserver.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.*;

import javax.tools.Tool;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.HttpURLConnection.*;



public class Launcher{
    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        if (args.length == 0){
            Test test = new Test();
        }
        else if (args.length == 1){ //server
            Server server = new Server(args);
        }
        else if (args.length == 2) { //Client
            Client client = new Client(args);
        }
    }
}










