package cameraapplication.cameraapplication;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class StorageImageFB {

    public void storageImageToFB(Bitmap image){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        final String[] imageName = {"images/" + UUID.randomUUID().toString() + ".jpg"};
        final StorageReference mountainImagesRef = storageRef.child(imageName[0]);

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
                Log.d("AnhNTT, success getMetadata().getName():", taskSnapshot.getMetadata().getName());
                imageName[0] = taskSnapshot.getMetadata().getName();
                new FirebaseServer(imageName[0]).execute();
            }

        });

    }
}
