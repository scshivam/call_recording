package call_recording.jbglass.in.callrecording.Requests;

import java.util.Map;

import call_recording.jbglass.in.callrecording.JSONBody.LoginBody;
import call_recording.jbglass.in.callrecording.Models.LoginPOJO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by satyam on 21/2/18.
 */

public interface LoginRequest {
    @POST("authenticate")
    Call<LoginPOJO> call(@Body LoginBody loginBody);

}
