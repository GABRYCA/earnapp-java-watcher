package eu.anonymousgca.javaearnapp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class JsonConfigLoader {

    private String oauthRefreshToken;
    private boolean mysqlEnabled;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUsername;
    private String mysqlPassword;

    //TODO value per GB from dashboard.
    private double valuePerGB;

    private boolean discordWebhookEnabled;

    private String discordWebhookUrl;

    public JsonConfigLoader() throws FileNotFoundException {
        // Check if config.json exists
        File file = new File("config.json");
        if (!file.exists()) {
            // If not, create it
            System.out.println("Config file not found, creating it...");

            // Create a new JSONObject
            JSONObject config = new JSONObject();
            config.put("oauth-refresh-token", "YOUR_OAUTH_REFRESH_TOKEN");
            config.put("mysql-enabled", false);
            config.put("mysql-host", "localhost");
            config.put("mysql-port", 3306);
            config.put("mysql-database", "earnapp_anonymousgca");
            config.put("mysql-username", "root");
            config.put("mysql-password", "root");
            config.put("value-gb", 0.30);
            config.put("discord-webhook-enabled", false);
            config.put("discord-webhook-url", "YOUR_DISCORD_WEBHOOK_URL");

            // Write config to file
            try {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(config.toJSONString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Exit program.
            System.out.println("Config file created! Please fill in the oauth-refresh-token in your config.json and restart the program!");
            System.exit(0);
        } else {
            // If yes, load it
            System.out.println("Config file found, loading it...");

            // Load config
            FileReader fileReader = new FileReader(file.getName());

            // Parse config
            JSONParser parser = new JSONParser();
            JSONObject config = null;
            try {
                config = (JSONObject) parser.parse(fileReader);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // If config is null, exit program.
            if (config == null) {
                System.out.println("Config file is empty! Please fill in the oauth-refresh-token in your config.json and restart the program!");
                System.exit(0);
            }

            // Debug terminal message.
            System.out.println("Config file loaded!\n");
            System.out.println("Reading properties: ");

            // Read properties
            oauthRefreshToken = (String) config.get("oauth-refresh-token");
            mysqlEnabled = (boolean) config.get("mysql-enabled");
            mysqlHost = (String) config.get("mysql-host");
            mysqlPort = (int) ((long) config.get("mysql-port"));
            mysqlDatabase = (String) config.get("mysql-database");
            mysqlUsername = (String) config.get("mysql-username");
            mysqlPassword = (String) config.get("mysql-password");
            valuePerGB = (double) config.get("value-gb");
            discordWebhookEnabled = (boolean) config.get("discord-webhook-enabled");
            discordWebhookUrl = (String) config.get("discord-webhook-url");

            // Debug terminal message.
            System.out.println("Properties read with success!\n");
        }
    }

    public String getOauthRefreshToken() {
        return oauthRefreshToken;
    }

    public boolean isMysqlEnabled() {
        return mysqlEnabled;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public double getValuePerGB() {
        return valuePerGB;
    }

    public boolean isDiscordWebhookEnabled() {
        return discordWebhookEnabled;
    }

    public String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }

    public void setMysqlEnabled(boolean enabled) {
        mysqlEnabled = enabled;
    }

    public void setDiscordWebhookEnabled(boolean enabled) {
        discordWebhookEnabled = enabled;
    }
}
