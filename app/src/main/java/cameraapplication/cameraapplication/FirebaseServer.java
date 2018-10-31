package cameraapplication.cameraapplication;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.UUID;

public class FirebaseServer extends AsyncTask<Void,Void,Void> {
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String severKey = "AIzaSyAL8K_NtXxwmDkhvEi86h-6-cwUWjUn3-s";
    private static String imageName = null;

    public FirebaseServer(String image) {
//        storageImageToFB(bitmap);
        imageName = image;

    }

    private static void storageImageToFB(Bitmap image){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

//        imageName = "images/"+UUID.randomUUID().toString()+".jpg";
        final StorageReference mountainImagesRef = storageRef.child(imageName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("AnhNTT, Error upload image: ", e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageName = taskSnapshot.getMetadata().getName();
                Log.d("AnhNTT, success getMetadata().getName():", taskSnapshot.getMetadata().getName());
            }

        });

    }


    private static HttpURLConnection getConnection() throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Authorization", "key=" + severKey);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
    }


    public static void sendMessage(){
        HttpURLConnection connection = null;
        try {
            connection = getConnection();
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter wr = new OutputStreamWriter(outputStream);


            wr.write(buildNotificationMessage().toString());
            wr.flush();
            wr.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String response = inputstreamToString(connection.getInputStream());
                Log.d("AnhNTT","Message sent to Firebase for delivery, response: " + response);

            } else {
                String response = inputstreamToString(connection.getErrorStream());
                Log.d("AnhNTT","Unable to send message to Firebase:" + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static JSONObject buildNotificationMessage() {
        JSONObject json = new JSONObject();

        try {
            json.put("to", "/topics/SafeObser");
//            json.put("to","dE4GlN-TS-U:APA91bFkpLjcw1SY2KpaUdS-jVGqf1Imnnqhimm0ITrxUb_hchk03AuzL5mO4XQdPLOGZikhE6PVoZjppLPDhUTkYgkpfCZi5yca0phvuk37Y-4dmqE3_kDollYaafLkIgb5e8z7F-kj");

            JSONObject info = new JSONObject();
            info.put("title", "SafeObser");   // Notification title
            info.put("body", "Có vật nguy hiểm"); // Notification body
            info.put("click_action", "OPEN_ACTIVITY_1");

            json.put("notification", info);

            JSONObject data = new JSONObject();
            data.put("images",imageName);
            data.put("cameraID","Cầu thang lầu 1");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(Calendar.getInstance().getTime());
            data.put("dateTime",currentDateandTime);
            data.put("story_id", "story_12345");   // Notification title

            json.put("data",data);
//            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String inputstreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    @Override
    protected Void doInBackground(Void...voids ) {
        sendMessage();
        return null;
    }


}
