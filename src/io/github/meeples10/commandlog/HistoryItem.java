package io.github.meeples10.commandlog;

import java.util.Date;

import org.bukkit.Location;

public class HistoryItem {
    String command;
    String sender;
    Date date;
    Location location;

    public HistoryItem(String Command, String Sender, Date date, Location Location) {
        this.command = Command;
        this.sender = Sender;
        this.date = date;
        this.location = Location;
    }

    public String getCommand() {
        return this.command;
    }

    public String getSender() {
        return this.sender;
    }

    public Date getDate() {
        return this.date;
    }

    public Location getLocation() {
        return this.location;
    }
}