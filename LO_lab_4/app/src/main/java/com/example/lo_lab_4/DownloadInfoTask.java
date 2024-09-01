package com.example.lo_lab_4;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadInfoTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        String fileInformation = "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int fileSize = connection.getContentLength();
            String fileType = connection.getContentType();
            fileInformation = "Size: " + fileSize + ", Type: " + fileType;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return fileInformation;
    }
}