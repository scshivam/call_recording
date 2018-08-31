package call_recording.jbglass.in.callrecording.JSONBody;

/**
 * Created by satyam on 15/6/18.
 */

public class DispositionBody {
    String parent_id;
    String call_id;

    public DispositionBody(String parent_id, String call_id){
        this.parent_id=parent_id;
        this.call_id=call_id;
    }
}
