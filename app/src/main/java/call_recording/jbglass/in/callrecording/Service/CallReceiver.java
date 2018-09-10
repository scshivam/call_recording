package call_recording.jbglass.in.callrecording.Service;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import net.callrec.library.fix.CallRecService;
import net.callrec.library.fix.ProcessingBase;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import call_recording.jbglass.in.callrecording.Activity.CallingScreen;
import call_recording.jbglass.in.callrecording.Activity.FeedbackActivity;
import call_recording.jbglass.in.callrecording.Config.DbHandler;
import call_recording.jbglass.in.callrecording.Fragments.dialog_feedback;

import static android.content.Context.AUDIO_SERVICE;
import static call_recording.jbglass.in.callrecording.Activity.MainActivity.TAG;
import static call_recording.jbglass.in.callrecording.Activity.MainActivity.audiofile;
import static call_recording.jbglass.in.callrecording.Activity.MainActivity.recorder;

/**
 * Created by satyam on 19/4/18.
 */

public class CallReceiver extends PhoneCallReceiver {

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        number=DbHandler.getString(ctx,"mob_number","");
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startRecordingService(ctx,number,ProcessingBase.TypeCall.INSTANCE.getINC());
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        if(DbHandler.contains(ctx,"app")) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startRecordingService(ctx,DbHandler.getString(ctx, "call_id", ""),ProcessingBase.TypeCall.INSTANCE.getINC());
        }
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        ctx.stopService(new Intent(ctx, CallRecService.class));
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        ctx.stopService(new Intent(ctx, CallRecService.class));
        number=DbHandler.getString(ctx,"mob_number","");
        if(DbHandler.contains(ctx,"app")) {
            String queryString = "NUMBER=" + number;
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            ctx.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
                Intent intent = new Intent(ctx, FeedbackActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
        }

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.e("test","test5");
    }

    private void startRecordingService(Context ctx, String number, int callType) {
        Intent phoneCall = new Intent(ctx, CallRecService.class);
        phoneCall.putExtra(ProcessingBase.IntentKey.INSTANCE.getCALL_ID(), number);
        phoneCall.putExtra(ProcessingBase.IntentKey.INSTANCE.getTYPE_CALL(), callType);
        ctx.startService(phoneCall);
    }
}