package com.dji.drone.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class ImageSender {
    private final static String protocol = "http";
    private final static String site = "192.168.0.154";
    private final static String port = "6767";

    @Nullable
    public static Detection scanImage(@NonNull Bitmap image){
        try {
            byte[] imageArray = convertBitmap(image);

            String baseUrl = protocol + "://" + site + ":" + port + "/";
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();


            int responseCode = sendImage(imageArray, connection);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String crackResult = receiveScanResult(connection);

                Detection.Segment[][] segments = convertCrackResult(crackResult);

                return new Detection(image, segments);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] convertBitmap(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private static int sendImage(byte[] image, HttpURLConnection connection) throws IOException {
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(image);
        outputStream.flush();
        outputStream.close();

        return connection.getResponseCode();
    }

    private static String receiveScanResult(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        StringBuilder result = new StringBuilder();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            result.append(new String(buffer, 0, length));
        }
        inputStream.close();

        return result.toString();
    }

    private static Detection.Segment[][] convertCrackResult(String crackResult) throws JSONException {
        JSONObject jsonObject = new JSONObject(crackResult);

        int totalSize = jsonObject.getInt("size");
        int squareMatrizSize = (int) Math.sqrt((double) totalSize);
        //int[][] crack = new int[squareMatrizSize][squareMatrizSize];
        Detection.Segment[][] segments = new Detection.Segment[squareMatrizSize][squareMatrizSize];

        JSONArray jsonArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < totalSize; i++) {
            JSONObject resultObject = jsonArray.getJSONObject(i);
            int classValue = resultObject.getInt("class");
            int score = resultObject.getInt("score");
            //crack[i/squareMatrizSize][i%squareMatrizSize] = classValue;

            Detection.Segment segment = new Detection.Segment(classValue, score);
            segments[i/squareMatrizSize][i%squareMatrizSize] = segment;
        }

        return segments;
    }
}
