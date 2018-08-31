package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.UploadBody;
import call_recording.jbglass.in.callrecording.Models.RemarkPOJO;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by satyam on 15/6/18.
 */

public interface UploadRequest {
    @POST("update_file")
    Call<RemarkPOJO> call(@Body UploadBody uploadBody);
}
