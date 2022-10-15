package me.mudkip989.example.nauto;

import net.dv8tion.jda.api.entities.*;

import java.io.*;
import java.util.*;

public class DBFile {
    File GuildFile;
    File UserFile;

    public DBFile() {
        try {
            File myObj = new File("filename.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void setGuildData(Guild guild, String key, String data) {

    }

    public String getGuildData(Guild guild, String key) {

        return null;
    }


}
