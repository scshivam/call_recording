package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.DispositionBody;
import call_recording.jbglass.in.callrecording.JSONBody.RemarkBody;
import call_recording.jbglass.in.callrecording.Models.DispositionPOJO;
import call_recording.jbglass.in.callrecording.Models.RemarkPOJO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by satyam on 15/6/18.
 */

public interface RemarkRequest {
    @POST("write_remark")
    Call<RemarkPOJO> call(@Body RemarkBody remarkBody);
}
