package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.Models.DispositionDatumPOJO;
import call_recording.jbglass.in.callrecording.Models.DispositionPOJO;
import call_recording.jbglass.in.callrecording.Models.MemberInfoPOJO;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by satyam on 15/6/18.
 */

public interface DispositionsRequest {
    @GET("dispositions")
    Call<DispositionPOJO> call();
}
