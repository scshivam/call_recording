package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.RemarkBody;
import call_recording.jbglass.in.callrecording.JSONBody.ReplicateCallBody;
import call_recording.jbglass.in.callrecording.Models.RemarkPOJO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by satyam on 22/7/18.
 */

public interface ReplicateCall {
    @POST("recall")
    Call<RemarkPOJO> call(@Body ReplicateCallBody replicateCallBody);

}
