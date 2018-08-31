package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.DispositionBody;
import call_recording.jbglass.in.callrecording.Models.DispositionPOJO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by satyam on 15/6/18.
 */

public interface Disposition2Request {
    @POST("dispositions")
    Call<DispositionPOJO> call(@Body DispositionBody dispositionBody);
}
