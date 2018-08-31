package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.CallingListBody;
import call_recording.jbglass.in.callrecording.JSONBody.DispositionBody;
import call_recording.jbglass.in.callrecording.Models.CallingListPOJO;
import call_recording.jbglass.in.callrecording.Models.MemberInfoPOJO;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by satyam on 15/6/18.
 */

public interface CallingListRequest {
    @POST("calling_list")
    Call<CallingListPOJO> call(@Body CallingListBody callingListBody);
}
