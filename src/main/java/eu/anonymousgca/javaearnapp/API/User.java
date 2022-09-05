package eu.anonymousgca.javaearnapp.API;

import org.json.simple.JSONObject;

import java.io.IOException;

public class User {

    private double multiplier;
    private String referralCode;
    private String name;
    private String first_name;
    private String last_name;
    private String onboarding;
    private String locale;

    /**
     * Constructor, needs DashboardConnection, this is currently unused.
     * @param dashboardConnection
     * @throws IOException
     */
    public User(DashboardConnection dashboardConnection) throws IOException {
        JSONObject userData = dashboardConnection.getUserData();
        this.multiplier = (double) userData.get("multiplier");
        this.referralCode = (String) userData.get("referral_code");
        this.name = (String) userData.get("name");
        this.first_name = (String) userData.get("first_name");
        this.last_name = (String) userData.get("last_name");
        this.onboarding = (String) userData.get("onboarding");
        this.locale = (String) userData.get("locale");
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public String getName() {
        return name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getOnboarding() {
        return onboarding;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return "User{" +
                "multiplier=" + multiplier +
                ", referralCode='" + referralCode + '\'' +
                ", name='" + name + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", onboarding=" + onboarding +
                ", locale='" + locale + '\'' +
                '}';
    }
}
