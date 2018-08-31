package call_recording.jbglass.in.callrecording.Requests;

import call_recording.jbglass.in.callrecording.JSONBody.HistoryBody;
import call_recording.jbglass.in.callrecording.JSONBody.IncomingBody;
import call_recording.jbglass.in.callrecording.Models.HistoryDataPOJO;
import call_recording.jbglass.in.callrecording.Models.IncomingPOJO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by satyam on 29/7/18.
 */

public interface HistoryRequest {
    @POST("history")
    Call<HistoryDataPOJO> call(@Body HistoryBody historyBody);
}
