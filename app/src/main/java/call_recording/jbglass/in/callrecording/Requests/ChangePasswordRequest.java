package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.ChangePasswordBody;
import call_recording.jbglass.in.callrecording.JSONBody.DispositionBody;
import call_recording.jbglass.in.callrecording.Models.DispositionPOJO;
import call_recording.jbglass.in.callrecording.Models.RemarkPOJO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by satyam on 30/7/18.
 */

public interface ChangePasswordRequest {
    @POST("update_password")
    Call<RemarkPOJO> call(@Body ChangePasswordBody changePasswordBody);
}
