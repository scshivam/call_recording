package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.LoginBody;
import call_recording.jbglass.in.callrecording.Models.LoginPOJO;
import call_recording.jbglass.in.callrecording.Models.MemberInfoPOJO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by satyam on 15/6/18.
 */

public interface MemberInfoRequest {
    @GET("memberinfo")
    Call<MemberInfoPOJO> call();
}
