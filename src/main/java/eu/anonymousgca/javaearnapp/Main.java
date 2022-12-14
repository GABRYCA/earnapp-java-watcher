package eu.anonymousgca.javaearnapp;

import com.mysql.cj.jdbc.Driver;
import eu.anonymousgca.javaearnapp.API.DashboardConnection;
import eu.anonymousgca.javaearnapp.Discord.DiscordWebhook;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

    public static String oauthRefreshToken;
    public static JsonConfigLoader jsonConfigLoader;
    public static DashboardConnection dashboardConnection;
    public static Connection mySQLConnection;
    public static double previousMoneyEarned = 0;
    public static double valueGB = 0.30;
    public static int counterChecks = 0;

    private static  String DB_URL = null;
    private static  String DB_USERNAME = null;
    private static  String DB_PASSWORD = null;

    public static void main(String[] args) throws IOException {

        // Start message.
        System.out.println("Program started!\n");

        // Load config
        jsonConfigLoader = new JsonConfigLoader();
        oauthRefreshToken = jsonConfigLoader.getOauthRefreshToken();

        // Init DashboardConnection class.
        dashboardConnection = new DashboardConnection(oauthRefreshToken);

        // Check if MySQL is enabled and correct, if not, disable it.
        if (jsonConfigLoader.isMysqlEnabled()) {
            System.out.println("MySQL enabled...");
            DB_URL = "jdbc:mysql://" + jsonConfigLoader.getMysqlHost() + ":" + jsonConfigLoader.getMysqlPort() + "/" + jsonConfigLoader.getMysqlDatabase();
            DB_USERNAME = jsonConfigLoader.getMysqlUsername();
            DB_PASSWORD = jsonConfigLoader.getMysqlPassword();
            getConnection();
            // If failed to login, try to connect to DB_URL without database.
            if (!jsonConfigLoader.isMysqlEnabled()) {
                System.out.println("Failed to connect to database, trying to connect to database without database...");
                DB_URL = "jdbc:mysql://" + jsonConfigLoader.getMysqlHost() + ":" + jsonConfigLoader.getMysqlPort();
                getConnection();
                // If failed to login, disable MySQL.
                if (!jsonConfigLoader.isMysqlEnabled() || mySQLConnection == null) {
                    System.out.println("Failed to connect to database, disabling MySQL...");
                    jsonConfigLoader.setMysqlEnabled(false);
                } else {
                    System.out.println("Connected to database without database!" +
                            "\nCreating one...");
                    createDatabase();
                }
            }
            if (jsonConfigLoader.isMysqlEnabled()) {
                // Create table if not exists.
                createTable();
                // Reset DB_URL to default.
                DB_URL = "jdbc:mysql://" + jsonConfigLoader.getMysqlHost() + ":" + jsonConfigLoader.getMysqlPort() + "/" + jsonConfigLoader.getMysqlDatabase();
            }
        }

        // Run each hour a task to check the dashboard, earnings etc...
        ScheduledExecutorService executorService;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Main::hourlyTask, 0, 1, java.util.concurrent.TimeUnit.HOURS);

        // Debug message.
        System.out.println("\nProgram is running!\n");
    }

    private static boolean createDatabase() {
        try {
            System.out.println("Creating database...");
            Statement statement = mySQLConnection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + jsonConfigLoader.getMysqlDatabase());
            statement.close();
            System.out.println("CREATE DATABASE IF NOT EXISTS " + jsonConfigLoader.getMysqlDatabase());
            System.out.println("Database created!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createTable() {
        try {
            System.out.println("Creating table if not existing...");
            Statement statement = mySQLConnection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS earnings (time datetime, traffic double, earnings double)");
            statement.close();
            System.out.println("CREATE TABLE IF NOT EXISTS earnings (time datetime, traffic double, earnings double)");
            System.out.println("Table created only if wasn't already!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get a new MySQL connection.
     */
    private static void getConnection() {
        // Estabilish connection with MySQL database.
        try {
            Class.forName(Driver.class.getName());

            mySQLConnection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            if (mySQLConnection != null) {
                System.out.println("Connected to MySQL database!");
                jsonConfigLoader.setMysqlEnabled(true);
            } else {
                System.out.println("Failed to connect to MySQL database!");
                jsonConfigLoader.setMysqlEnabled(false); // Disable MySQL.
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Check earnings each hour as a task.
     */
    private static void hourlyTask() {

        // Get earnings from dashboard.
        JSONObject money = null;
        try {
            money = dashboardConnection.getMoney();
        } catch (IOException ignored){}

        if (money == null){
            return;
        }

        // Get the money earned.
        double moneyEarned = Double.parseDouble(money.get("earnings_total").toString());

        // Debug message visible on the terminal.
        System.out.println("Check number: [" + (counterChecks + 1) + "]");
        System.out.println("Money: " + moneyEarned);

        // Check if the money earned is different from the previous check.
        double moneyEarnedDifference = moneyEarned - previousMoneyEarned;

        // Debug message visible on the terminal.
        if (counterChecks != 0) {
            System.out.println("Money earned since last check: " + round(moneyEarnedDifference,2));
        } else {
            System.out.println("Money earned since last check: 0");
            moneyEarnedDifference = 0;
        }

        // Update previous money earned and also calculate the about used bandiwdth.
        previousMoneyEarned = moneyEarned;
        double sketchyBandwidthUsage = round((1024 * moneyEarnedDifference) / valueGB, 2);
        moneyEarnedDifference = round(moneyEarnedDifference, 2); // 0.012334542 whatever -> 0.01
        counterChecks++;

        // If MySQL is enabled, insert the data into the database.
        if (jsonConfigLoader.isMysqlEnabled()) {
            String query = "INSERT INTO earnings (time, traffic, earnings) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement;
            try {
                getConnection();
                preparedStatement = mySQLConnection.prepareStatement(query);
                preparedStatement.setTimestamp(1, new Timestamp(new Date().getTime()));
                preparedStatement.setDouble(2, sketchyBandwidthUsage);
                preparedStatement.setDouble(3, moneyEarnedDifference);
                preparedStatement.executeUpdate();
                System.out.println(preparedStatement);
                preparedStatement.close();
                mySQLConnection.close();
                System.out.println("Data saved to MySQL database!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If Discord Webhook is enabled, send the data to the Discord Webhook.
        if (jsonConfigLoader.isDiscordWebhookEnabled()) {
            DiscordWebhook webhook = new DiscordWebhook(jsonConfigLoader.getDiscordWebhookUrl());
            DiscordWebhook.EmbedObject embedObject = new DiscordWebhook.EmbedObject()
                    .setTitle("Money earned since last check: " + moneyEarnedDifference + "$")
                    .setDescription("Money earned: " + moneyEarned + "$")
                    .addField("Money earned since last check: ", " + " + moneyEarnedDifference + "$", false)
                    .addField("Money earned: ", moneyEarned + "$", false)
                    .addField("Bandwidth usage: ", sketchyBandwidthUsage + "MB", false)
                    .setFooter("JavaEarnApp by AnonymousGCA/GABRYCA", "https://avatars.githubusercontent.com/u/39743848?s=40&v=4")
                    .setColor(new java.awt.Color(255, 70, 0));
            if (moneyEarnedDifference > 0.00){
                embedObject.setColor(new java.awt.Color(0, 255, 0));
            }
            webhook.addEmbed(embedObject);
            try {
                webhook.execute();
                System.out.println("Webhook sent!");
            } catch (IOException e) {
                e.printStackTrace();
                jsonConfigLoader.setDiscordWebhookEnabled(false); // Disable Discord Webhook.
            }
        }

        // Debug message visible on the terminal.
        System.out.println("\nWaiting for next check...\n");
    }
}