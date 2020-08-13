package me.kodysimpson.cortexbot.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VersionUtil {

    private String jdaVersion;

    private final String GET_URL = "https://api.bintray.com/packages/dv8fromtheworld/maven/JDA/versions/_latest";

    public VersionUtil(){
        updateJDAVersion();
    }


    public String getJDAVersion(){
        return jdaVersion;
    }

    public void updateJDAVersion() {
        try {
            URL url = new URL(GET_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                JSONObject jsonObject = new JSONObject(response.toString());
                jdaVersion = jsonObject.getString("name");
            }


        } catch (MalformedURLException maexc){
            System.out.println("URL is outdated");
        } catch (IOException ioexc){
            System.out.println("Something has gone wrong");
        }
    }
}
