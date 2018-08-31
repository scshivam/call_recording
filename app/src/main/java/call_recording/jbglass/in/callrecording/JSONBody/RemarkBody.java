package call_recording.jbglass.in.callrecording.JSONBody;

/**
 * Created by satyam on 15/6/18.
 */

public class RemarkBody {
    String remark;
    String call_id;

    public RemarkBody(String remark, String call_id){
        this.remark=remark;
        this.call_id=call_id;
    }
}
