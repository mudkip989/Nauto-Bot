package me.mudkip989.example.nauto;


import me.mudkip989.example.nauto.EventHandlers.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;

import javax.security.auth.login.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Bot {


    public static void main(String[] args) throws LoginException, URISyntaxException {
        if (args.length == 0) {
            System.out.println("Missing Token");
            return;
        }
        JDA client = JDABuilder.createLight(args[0]).setActivity(Activity.watching("for your next move.")).build();
        client.addEventListener(new Commands(client));

        String s = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
        Path p = Paths.get(s+"/data");
        if(!Files.isDirectory(p)){



        }
        System.out.println(s);
        File data = new File(s + "\\data\\data.json");
        try{
            try{
                Files.createDirectories(data.toPath().getParent());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            if(data.createNewFile()){
                System.out.println("Data file created.");
            }else {
                System.out.println("Data file already exists... Skipping creation.");
            }
        }catch (Exception e){
            System.out.println("Error Occured. Please see below for message.");
            System.out.println(e);
            System.out.println("-----------------------------------------------------------------------------------");
        }

        return;
    }


    public static void BuildCommands() {


    }


}
