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
        if (!Build.BRAND.equalsIgnoreCase("xiaomi")) {
            File dir = Environment.getExternalStorageDirectory();
            try {
                audiofile = File.createTempFile("sound", ".amr", dir);
            } catch (IOException e) {
                Log.e(TAG, "external storage access error");
                return;
            }
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            recorder.setOutputFile(audiofile.getAbsolutePath());
            try {
                recorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.start();
        }

        //.makeText(ctx, "Incoming", Toast.LENGTH_LONG).show();
        //ctx.startActivity(new Intent(ctx, CallingScreen.class));
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        if(DbHandler.contains(ctx,"app")) {
            if (!Build.BRAND.equalsIgnoreCase("xiaomi")) {
                File dir = Environment.getExternalStorageDirectory();
                Log.e("dir", dir.toString());
                try {
                    audiofile = File.createTempFile("sound", ".amr", dir);
                } catch (IOException e) {
                    Log.e(TAG, "external storage access error");
                    return;
                }

                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setOutputFile(audiofile.getAbsolutePath());
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);



                try {
                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recorder.start();
            }
        }
//        Intent intent=new Intent(ctx,CallingScreen.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ctx.startActivity(intent);
        // Toast.makeText(ctx, "Outgoing", Toast.LENGTH_LONG).show();
        // ctx.startActivity(new Intent(ctx, CallingScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK ));
//        Intent intent=new Intent(ctx,CallingScreen.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ctx.startActivity(intent);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        number=DbHandler.getString(ctx,"mob_number","");
        if (!Build.BRAND.equalsIgnoreCase("xiaomi")) {

            recorder.stop();
            recorder.release();

            ContentValues values = new ContentValues(4);
            long current = System.currentTimeMillis();
            values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
            values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");

            String path = Environment.getExternalStorageDirectory().toString() + "/MIUI/sound_recorder/call_rec";
            File dir = new File(path);

            String fname = "BKIn_" + number + ".amr";
            File f = new File(dir, fname);
            audiofile.renameTo(f);

            values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());

            ContentResolver contentResolver = ctx.getContentResolver();
            Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Uri newUri = contentResolver.insert(base, values);

            ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));


            String queryString = "NUMBER=" + number;
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        else{
            String fname = "BKIn_" + number + ".amr";
            String path = Environment.getExternalStorageDirectory().toString() + "/MIUI/sound_recorder/call_rec";
            File dir2 = new File(path);
            File[] files = dir2.listFiles();


            for (File file : files) {
                Log.e("file_name",file.getName());
                if(file.getName().toLowerCase().contains(number)){
                    File f2 = new File(dir2, fname);
                    file.renameTo(f2);
                }
            }
        }

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        number=DbHandler.getString(ctx,"mob_number","");
        int flg=0;
        if(DbHandler.contains(ctx,"app")) {
            if (!Build.BRAND.equalsIgnoreCase("xiaomi")) {
                recorder.stop();
                recorder.release();


                ContentValues values = new ContentValues(4);
                long current = System.currentTimeMillis();
                values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
                values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");

                File dir = Environment.getExternalStorageDirectory();

                String fname = "BKOut_" + DbHandler.getString(ctx, "call_id", "") + ".amr";
                File f = new File(dir, fname);
                audiofile.renameTo(f);

                values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());

                ContentResolver contentResolver = ctx.getContentResolver();
                Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Uri newUri = contentResolver.insert(base, values);

                ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
                flg=1;
            }
            else {

                String fname = "BKOut_" + DbHandler.getString(ctx, "call_id", "") + ".amr";
                String path = Environment.getExternalStorageDirectory().toString() + "/MIUI/sound_recorder/call_rec";
                File dir2 = new File(path);
                File[] files = dir2.listFiles();

                for (File file : files) {
                    Log.e("file_name",file.getName());
                    if(file.getName().toLowerCase().contains(number)){
                        flg=1;
                        File f2 = new File(dir2, fname);
                        file.renameTo(f2);
                    }
                }

            }
            String queryString = "NUMBER=" + number;
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            ctx.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);

            if(flg==0){
                Toast.makeText(ctx,"Unable to record call. Please start auto call recorder",Toast.LENGTH_LONG).show();

            }
            else {
                Intent intent = new Intent(ctx, FeedbackActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }

        }
        // ((Activity) ctx).finishAffinity();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.e("test","test5");
    }

}