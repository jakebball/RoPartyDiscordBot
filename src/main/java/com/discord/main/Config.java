package com.discord.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {
    public static String token;
    public static String prefix = "!";

    public Config() {
        this.token = System.getenv().get("token");
    }
}
