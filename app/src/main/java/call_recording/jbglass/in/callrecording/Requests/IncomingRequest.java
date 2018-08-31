package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.IncomingBody;
import call_recording.jbglass.in.callrecording.JSONBody.UploadBody;
import call_recording.jbglass.in.callrecording.Models.IncomingPOJO;
import call_recording.jbglass.in.callrecording.Models.RemarkPOJO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by satyam on 16/6/18.
 */

public interface IncomingRequest {
    @POST("new_call")
    Call<IncomingPOJO> call(@Body IncomingBody incomingBody);
}
