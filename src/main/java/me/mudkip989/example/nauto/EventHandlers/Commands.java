package me.mudkip989.example.nauto.EventHandlers;


import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.events.interaction.component.*;
import net.dv8tion.jda.api.hooks.*;
import net.dv8tion.jda.api.interactions.*;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.components.buttons.*;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.*;
import org.jetbrains.annotations.*;
import org.json.*;

import javax.annotation.*;
import java.awt.*;
import java.lang.reflect.*;
import java.lang.reflect.Member;
import java.util.*;
import java.util.List;
import java.util.function.*;

public class Commands extends ListenerAdapter {
    Map<String, Function<SlashCommandInteractionEvent, MessageCreateData>> commands;
    Map<String, Function<SlashCommandInteractionEvent, Event>> speccommands;

    String clienttag;
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println(clienttag);
        Thread thread = new Thread() {
            public void run() {
                System.out.println("Thread Running");
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        thread.start();


    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String comm = event.getCommandPath().split("\\/")[0];
        if (commands.containsKey(comm)) {

            MessageCreateData rep = commands.get(comm).apply(event);
            event.reply(rep).queue();
        } else if (speccommands.containsKey(comm)) {
            Event newevent = speccommands.get(comm).apply(event);
        } else {
            MessageCreateData rep = new MessageCreateBuilder().setEmbeds(new EmbedBuilder().setTitle("Incorrect Command.").setDescription("I dont know how, but you managed input a command that doesnt exist. Maybe I forgot to remove the command. Oh well...").build()).build();
            event.reply(rep).queue();
            System.out.println(event.getCommandPath());
        }


    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("hello")) {
            event.reply("Hello There " + event.getMember().getAsMention()).queue();
        } else if (event.getComponentId().startsWith("8ball.")) {
            String question = event.getComponentId().split("\\.", 2)[1];
            event.editMessage(new MessageEditBuilder().applyCreateData(ball8Button(question)).build()).queue();
        } else if (event.getComponentId().equals("dummy")) {
            event.deferEdit().queue();
        } else if (event.getComponentId().startsWith("chess")) {
            chessButton(event);
        } else if (event.getComponentId().equals("vote:1")) {
            voteButton(event);
        }
    }

    public Commands(JDA jda) {
        commands = new HashMap<>();
        speccommands = new HashMap<>();
        jda.updateCommands().addCommands(
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("greet", "Greets Someone").addOption(OptionType.STRING, "name", "Person to greet", false),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("8ball", "Ask the 8ball").addOption(OptionType.STRING, "question", "Ask it something!", true),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("test", "mudkip989 is testing something here").setGuildOnly(true).addSubcommands(new SubcommandData("subcommand", "mudkip989's testing ground").addOption(OptionType.STRING,"text", "hmm")),
                net.dv8tion.jda.api.interactions.commands.build.Commands.slash("poll", "Classic Poll Command").addOption(OptionType.STRING, "question", "Poll question").addOption(OptionType.STRING, "answers", "<something here>")

        ).queue();

        commands.put("greet", this::greet);
        commands.put("8ball", this::ball8);
        commands.put("poll", this::poll);
        speccommands.put("test", this::test);
        //jda.upsertCommand("chess", "Play some chess.").queue();
        //commands.put("chess", this::chess);
        //jda.upsertCommand("vote", "Vote on stuff.").addOption(OptionType.STRING, "content", "Ask your people something!", true).addOption(OptionType.STRING, "choice1", "Things to vote on!", true).queue();
        //speccommands.put("vote", this::vote);
        clienttag = jda.getSelfUser().getAsTag();
    }

    private Event test(SlashCommandInteractionEvent event){
        if(event.getCommandPath().split("\\/")[1].equals("subcommand") && event.getMember().getId().equals("305445170776768515")){
            try{
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(event.getOptions().get(0).getAsLong()));
            }catch (Exception e){
                System.out.println(e);
            }
            event.reply(new MessageCreateBuilder().setContent("Operation Complete.").build()).setEphemeral(true).queue();
        }else{
            event.reply(new MessageCreateBuilder().setContent("Operation Failed.").build()).setEphemeral(true).queue();
        }



        return event;
    }
    public MessageCreateData MessageFromString(String text) {


        MessageCreateData message = new MessageCreateBuilder().setContent(text).build();

        return message;
    }

    public Event vote(SlashCommandInteractionEvent event) {
        if (event.getOptions().size() < 1) {
            EmbedBuilder e = new EmbedBuilder().setTitle("**Failed**").setDescription("Please enter a thing to vote on").setColor(new Color(255, 0, 0));
            MessageCreateData m = new MessageCreateBuilder().setEmbeds(e.build()).build();
            event.reply(m).setEphemeral(true);

            return event;
        }

        EmbedBuilder e = new EmbedBuilder().setTitle("**Vote!**").setDescription(event.getOptions().get(0).getAsString() + "\n\nVote Now with the reactions below!").setColor(new Color(0, 255, 0));
        MessageCreateData m = new MessageCreateBuilder().setEmbeds(e.build()).addActionRow(Button.primary("vote:1", event.getOptions().get(1).getAsString())).build();

        event.reply(m).setEphemeral(true).queue();
        return event;
    }

    public Event voteButton(ButtonInteractionEvent event) {
        EmbedBuilder e = new EmbedBuilder().setTitle("Clicked").setDescription("You Clicked me!").setColor(new Color(0, 0, 255));
        MessageEditData m = new MessageEditBuilder().setEmbeds(e.build()).build();
        event.editMessage(m).queue();
        return event;
    }

    private MessageCreateData greet(SlashCommandInteractionEvent event) {
        List<OptionMapping> options = event.getOptions();
        if (options.size() < 1) {
            return new MessageCreateBuilder().setContent("Hello").addActionRow(Button.primary("hello", "Greet Me")).build();
        }
        String text = "Hello " + options.get(0).getAsString();


        return new MessageCreateBuilder().setContent(text).build();
    }

    private MessageCreateData ball8(SlashCommandInteractionEvent event) {
        List<OptionMapping> options = event.getOptions();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(0xFF0000FF, true));
        String question = options.get(0).getAsString();
        List<String> responses = new ArrayList<>();
        responses.add("Yes");
        responses.add("Maybe");
        responses.add("100% YES!!!");
        responses.add("I dont want to answer that");
        responses.add("DEFINITELY NOT");
        responses.add("No");
        responses.add("No, Just- Just no.");
        responses.add("YES YES YES!!!");
        Random rand = new Random();
        rand.setSeed(new Date().getTime());
        String get = responses.get(rand.nextInt(responses.size()));
        try {
            eb.setTitle(question);
            eb.setDescription(get);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            System.out.println("Question: " + question);
            eb.setTitle("Question Failed:");
            eb.setDescription(e.toString());
            return new MessageCreateBuilder().addEmbeds(eb.build()).build();
        }

        MessageEmbed meb = eb.build();
        MessageCreateData data;
        try {
            data = new MessageCreateBuilder().addActionRow(Button.success("8ball." + question, "Try again!")).setEmbeds(meb).build();
        } catch (IllegalArgumentException e) {
            data = new MessageCreateBuilder().addActionRow(Button.danger("dummy", "Question too long for retry.")).setEmbeds(meb).build();
        }

        return data;
    }

    private MessageCreateData ball8Button(String question) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(0xFF0000FF, true));
        List<String> responses = new ArrayList<>();
        responses.add("Yes.");
        responses.add("Maybe.");
        responses.add("100% YES!!!");
        responses.add("I don't want to answer that.");
        responses.add("DEFINITELY NOT!");
        responses.add("No.");
        responses.add("No, Just- Just no.");
        responses.add("YES YES YES!!!");
        responses.add("Perhaps...");
        responses.add("Most Likely.");
        responses.add("Not Likely.");
        responses.add("Forget About It.");
        responses.add("Don't Think About It.");
        Random rand = new Random();
        rand.setSeed(new Date().getTime());
        String get = responses.get(rand.nextInt(responses.size()));

        try {
            eb.setTitle(question);
            eb.setDescription(get);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            System.out.println("Question: " + question);
            eb.setTitle("Question Failed:");
            eb.setDescription(e.toString());
            return new MessageCreateBuilder().addEmbeds(eb.build()).build();
        }

        MessageEmbed meb = eb.build();
        MessageCreateData data;
        try {
            data = new MessageCreateBuilder().addActionRow(Button.success("8ball." + question, "Try again!")).setEmbeds(meb).build();
        } catch (IllegalArgumentException e) {
            data = new MessageCreateBuilder().addActionRow(Button.danger("dummy", "Question too long for retry.")).setEmbeds(meb).build();
        }

        return data;
    }

    public MessageCreateData poll(SlashCommandInteractionEvent event){


        return null;
    }

    private MessageCreateData chess(SlashCommandInteractionEvent event) {
        MessageCreateBuilder message = new MessageCreateBuilder().addEmbeds(new EmbedBuilder().setColor(new Color(0x5555ff)).setTitle("Chess").setDescription("Play chess with friends").build()).addActionRow(Button.secondary("chess", "button"));
        List<OptionMapping> options = event.getOptions();


        return message.build();
    }

    private void chessButton(ButtonInteractionEvent event) {
        //chess.pos.piece.selectOption.player1.player2.turn
        List<List<String>> data = GetButtonIDArray(event.getMessage());
        event.editMessage(new MessageEditBuilder().applyData(chessBoard(GetButtonIDArray(event.getMessage()))).setEmbeds(new EmbedBuilder().setTitle("This game is not yet functional.").setDescription("Come back later. We have yet to work out how to show the game.").setColor(new Color(0xff0000)).build()).build()).queue();
    }

    private MessageEditData chessBoard(List<List<String>> data) {
        MessageEditBuilder newButtons = new MessageEditBuilder();
        int i = 0;
        for (List<String> row : data) {
            i++;
            int j = 0;
            List<Button> rowb = new ArrayList<>();
            for (String id : row) {
                j++;
                if ((i + j % 2) == 1) {
                    rowb.add(Button.primary(id, " "));
                } else {
                    rowb.add(Button.secondary(id, " "));
                }
            }
            newButtons.setActionRow(rowb);

        }

        return newButtons.build();
    }

    private List<List<String>> GetButtonIDArray(Message message) {

        List<ActionRow> rows = message.getActionRows();
        List<List<String>> data = new ArrayList<>();
        for (ActionRow row : rows) {
            List<String> drow = new ArrayList<>();
            List<Button> buttons = row.getButtons();
            for (Button button : buttons) {
                drow.add(button.getId());
            }
            data.add(drow);
        }
        return data;
    }

    private MessageCreateData devcommand(SlashCommandInteractionEvent event) {

        return null;
    }


}
