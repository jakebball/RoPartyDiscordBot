package com.discord.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Config {
    public static String token;
    public static String prefix = "!";

    public Config() throws IOException {
        this.token = new String(Files.readAllBytes(Paths.get("C:/Users/Jake/Desktop/Bot Development/Roparty/.gitignore.txt")));
    }
}
