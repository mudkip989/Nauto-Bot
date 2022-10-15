package me.mudkip989.example.nauto;


import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;

import javax.security.auth.login.*;
import java.lang.reflect.*;
import java.util.*;

public class Bot {


    public static void main(String[] args) throws LoginException {
        if (args.length == 0) {
            System.out.println("Missing Token");
            return;
        }
        JDA client = JDABuilder.createLight(args[0]).setActivity(Activity.watching("for your next move.")).build();
        client.addEventListener(new Commands(client));

        return;
    }


    public static void BuildCommands() {


    }


}
