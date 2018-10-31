package app_interface;

import java.util.List;

import model.Base64Image;
import model.DetectionResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SendImageAPI {
    @POST("/upload_file/")
    @Headers( "Content-Type: application/json" )
    Call<List<DetectionResult>> uploadImage(@Body Base64Image image);
}
