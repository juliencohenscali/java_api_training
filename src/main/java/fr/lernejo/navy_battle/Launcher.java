package fr.lernejo.navy_battle;
import java.io.*;

public class Launcher{
    public static void main(String[] args) throws IOException, InterruptedException {
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










