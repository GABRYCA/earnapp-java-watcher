package eu.anonymousgca.javaearnapp.API;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;

public class DashboardConnection {

    private String baseURL = "https://earnapp.com/dashboard/api/";
    private String oauthRefreshToken;
    private String xsrfToken;

    public JSONArray latestTraffic;
    public DashboardConnection(String oauthRefreshToken) throws IOException {
        this.oauthRefreshToken = oauthRefreshToken;
        this.xsrfToken = getCSRF();
        if (xsrfToken == null) {
            throw new IOException("xsrfToken is null!");
        }
    }

    public JSONObject getDataURL(String path) throws IOException {
        URL url = new URL(baseURL + path + "?appid=earnapp_dashboard");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept-Encoding", "UTF8");
        conn.setRequestProperty("xsrf-token", xsrfToken);
        conn.setRequestProperty("Cookie", "auth-method=google; oauth-refresh-token=" + oauthRefreshToken + "; xsrf-token=" + xsrfToken);
        conn.connect();
        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new IOException("Something is wrong! Maybe the oauth-refresh-token is invalid or expired!" +
                    "\nResponse code: " + responseCode +
                    "\nResponse message: " + conn.getResponseMessage());
        }

        // Get response from server
        InputStream response = conn.getInputStream();

        // Parse response to JSONObject
        InputStreamReader responseReader = new InputStreamReader(response, "UTF-8");
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            Object obj = parser.parse(responseReader);
            try {
                jsonObject = (JSONObject) obj;
            } catch (ClassCastException ignored) {
                latestTraffic = new JSONArray();
                latestTraffic.add(obj);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        conn.disconnect();

        return jsonObject;
    }

    public JSONObject getDownloads() throws IOException {
        return getDataURL("downloads");
    }

    public JSONObject getPaymentMethods() throws IOException {
        return getDataURL("payment_methods");
    }

    public JSONObject getUserData() throws IOException {
        return getDataURL("user_data");
    }

    public JSONObject getDevices() throws IOException {
        return getDataURL("devices");
    }

    public JSONObject getTransactions() throws IOException {
        return getDataURL("transactions");
    }

    public JSONObject getMoney() throws IOException {
        return getDataURL("money");
    }

    public JSONObject getReferrals() throws IOException {
        return getDataURL("referees");
    }

    public JSONObject getCounters() throws IOException {
        return getDataURL("counters");
    }

    public JSONObject getBonuses() throws IOException {
        return getDataURL("bonuses");
    }

    public JSONObject getNotifies() throws IOException {
        return getDataURL("notifications");
    }

    public JSONArray getUsage() throws IOException {
        getDataURL("usage");
        return latestTraffic;
    }

    public JSONObject getBrightvpnReferrals() throws IOException {
        return getDataURL("referees_bvpn");
    }

    public JSONObject getSpeedtest () throws IOException {
        return getDataURL("speedtest");
    }

    public String getCSRF() throws IOException {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        URL url2 = new URL(baseURL + "sec/rotate_xsrf?appid=earnapp_dashboard");
        HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();

        if (responseCode != 200){
            System.out.println("Error Code: " + responseCode);
            System.out.println("Error Message: " + conn.getResponseMessage());
            return null;
        }

        conn.getContent();
        String csrf = null;

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        if(cookiesHeader != null){
            for(String cookie : cookiesHeader){
                String[] cookieParts = cookie.split(";");
                String[] cookieNameValue = cookieParts[0].split("=");
                if(cookieNameValue[0].equals("xsrf-token")){
                    csrf = cookieNameValue[1];
                }
            }
        }
        conn.disconnect();
        return csrf;
    }

}
