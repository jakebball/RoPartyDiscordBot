package com.discord.main;

public class Config {
    public static String token;
    public static String prefix = "!";

    public Config() {
        this.token = System.getenv().get("token");
    }
}
