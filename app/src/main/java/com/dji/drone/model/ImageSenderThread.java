package com.dji.drone.model;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ImageSenderThread implements Callable<DetectionResult> {
    private final String TAG = getClass().getSimpleName();

    private Socket socket;
    private String host;
    private int port;

    private DataOutputStream sender;
    private DataInputStream receiver;
    private ConcurrentLinkedDeque<DetectionResult> detectionResults;

    private int index;
    private Bitmap image;

    public ImageSenderThread(String serverIpAddress, int serverPort, int index, Bitmap image) {
        this.host = serverIpAddress;
        this.port = serverPort;
        socket = new Socket();
        //detectionResults = new ConcurrentLinkedDeque<>();
        this.index = index;
        this.image = image;
    }

    private void sendImage(int id, Bitmap image){
        try {
            byte[] imageData = convertImageToBytes(image);
            sender.writeInt(id);
            sender.write("<<StartImage>>".getBytes());
            sender.write(imageData);
            sender.write("<<FinishImage>>".getBytes());
        }catch (IOException e){
            Log.d(TAG, e.getMessage());
        }
    }

    private DetectionResult receiveClassification(){
        try {
            //int id = receiver.readInt();
            //int sizeArray = receiver.readInt();
            //int[][] detection = new int[sizeArray][sizeArray];
            //for (int i = 0; i < sizeArray; i++) {
            //    for (int j = 0; j < sizeArray; j++) {
            //        detection[i][j] = receiver.readInt();
            //    }
            //}
            //detectionResults.add(new DetectionResult(id, detection));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String jsonData = bufferedReader.readLine();
            JSONObject jsonObject = new JSONObject(jsonData);
            int index = jsonObject.getInt("index");
            JSONArray jsonArray = jsonObject.getJSONArray("classifications");

            Log.d(TAG, "index: " + index + " array : " + jsonArray.length());
            Log.d(TAG, jsonArray.toString());

            return new DetectionResult(index, jsonArrayToIntArray(jsonArray));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int[][] jsonArrayToIntArray(JSONArray jsonArray) {
        int[][] result = new int[jsonArray.length()][jsonArray.length()];
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray rowArray = jsonArray.getJSONArray(i);
                for (int j = 0; j < jsonArray.length(); j++) {
                    result[i][j] = rowArray.getInt(j);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
    public DetectionResult getResult(){
        if(detectionResults.isEmpty()){
            return null;
        }
        return detectionResults.pop();
    }
    */
    public boolean openConection(){
        try {
            socket.connect(new InetSocketAddress(host, port), 5000);
            sender = new DataOutputStream(socket.getOutputStream());
            receiver = new DataInputStream(socket.getInputStream());

            //receiveClassification();
        }catch (IOException e){
            Log.d(TAG, "Failed in connect");
            return false;
        }
        return true;
    }

    public void closeConnection(){
        try {
            sender.close();
            receiver.close();
            socket.close();
        }catch (IOException e){
            Log.d(TAG, "Error in close: " + e.getMessage());
        }
    }

    private byte[] convertImageToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /*
    @Override
    public void run() {
        super.run();

        openConection();
        sendImage(index, image);
        receiveClassification();
        closeConnection();
    }
    */
    @Override
    public DetectionResult call() throws Exception {
        openConection();
        sendImage(index, image);
        DetectionResult result = receiveClassification();
        closeConnection();
        return result;
    }
}
