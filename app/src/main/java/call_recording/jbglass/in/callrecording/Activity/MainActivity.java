package call_recording.jbglass.in.callrecording.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import call_recording.jbglass.in.callrecording.Adapters.expandable_list_adapter_users;
import call_recording.jbglass.in.callrecording.Config.DbHandler;
import call_recording.jbglass.in.callrecording.Fragments.dialog_change_pwd;
import call_recording.jbglass.in.callrecording.Fragments.dialog_feedback;
import call_recording.jbglass.in.callrecording.Fragments.dialog_select_sim;
import call_recording.jbglass.in.callrecording.JSONBody.CallingListBody;
import call_recording.jbglass.in.callrecording.Models.CallingListPOJO;
import call_recording.jbglass.in.callrecording.Models.MemberInfoPOJO;
import call_recording.jbglass.in.callrecording.Networking.ServiceGenerator;
import call_recording.jbglass.in.callrecording.R;
import call_recording.jbglass.in.callrecording.Requests.CallingListRequest;
import call_recording.jbglass.in.callrecording.Requests.MemberInfoRequest;
import call_recording.jbglass.in.callrecording.UI.SelectDateFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static MediaRecorder recorder;
    public static File audiofile = null;
    public static final String TAG = "MediaRecording";
    //public static Button startButton, stopButton;
    static private int CALL_PERMISSION_CODE = 23;
    ExpandableListView expListView;
    expandable_list_adapter_users listAdapter;
    DisplayMetrics metrics;
    int width;
    ProgressDialog progressDialog;
    Button button;
    TextView date_tv;
    String date_st="";
    LinearLayout date_layout;
    Calendar myCalendar = Calendar.getInstance();

    int item = 0;// 0 for sim1 & 1 for sim2
    private List<PhoneAccountHandle> phoneAccountHandleList;
    private final static String simSlotName[] = {
            "extra_asus_dial_use_dualsim",
            "com.android.phone.extra.slot",
            "slot",
            "simslot",
            "sim_slot",
            "subscription",
            "Subscription",
            "phone",
            "com.android.phone.DialingMode",
            "simSlot",
            "slot_id",
            "simId",
            "simnum",
            "phone_type",
            "slotId",
            "slotIdx"
    };
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //1 this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        setContentView(R.layout.activity_main);
        Date today = new Date();
        date_st=formatter.format(today);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                view.setMaxDate(new Date().getTime());
                date_tv.setText(formatter.format(myCalendar.getTime()));
                date_st=formatter.format(myCalendar.getTime());
                callingList();
            }

        };

        date_layout=(LinearLayout)findViewById(R.id.date_layout);
        date_tv=(TextView)findViewById(R.id.date);
        date_tv.setText(date_st);
        date_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        nMgr.cancelAll();


//        button=(Button)findViewById(R.id.btn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //call();
//                dialog_select_sim dialog= dialog_select_sim.instance("8808518643","");
//                dialog.show(getSupportFragmentManager(),"");
//            }
//        });

//        if(getIntent().getExtras().getString("action").equals("call")){
//           // call();
//        }
//
//        else {
        if(!DbHandler.contains(MainActivity.this,"not_first")) {
//            Toast.makeText(MainActivity.this, "Please give auto start permission to app", Toast.LENGTH_LONG).show();
//            if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
//
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
//                startActivity(intent);
//
//
//            } else if (Build.BRAND.equalsIgnoreCase("Letv")) {
//
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
//                startActivity(intent);
//
//            } else if (Build.BRAND.equalsIgnoreCase("Honor")) {
//
//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
//                startActivity(intent);
//
//            }
        }


        DbHandler.putBoolean(MainActivity.this,"not_first",true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        MemberInfoRequest memberInfoRequest = ServiceGenerator.createService(MemberInfoRequest.class, DbHandler.getString(this, "bearer", ""));
        Call<MemberInfoPOJO> memberInfoPOJOCall = memberInfoRequest.call();
        memberInfoPOJOCall.enqueue(new Callback<MemberInfoPOJO>() {
            @Override
            public void onResponse(Call<MemberInfoPOJO> call, Response<MemberInfoPOJO> response) {

                // Log.e("res_code", String.valueOf(response.code()));
//                Log.e("res_data",response.body().getSuccess().toString());
                if (response.code() == 200) {
                    progressDialog.dismiss();
                    DbHandler.putString(MainActivity.this, "member_info", new Gson().toJson(response.body().getData()));

                    callingList();

                } else if (response.code() == 403) {
                    progressDialog.dismiss();

                    Toast.makeText(MainActivity.this, "Not Authorized", Toast.LENGTH_LONG).show();
                    DbHandler.unsetSession(MainActivity.this, "isforcedLoggedOut");
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(MainActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                }
            }

            @Override
            public void onFailure(Call<MemberInfoPOJO> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(MainActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).create().show();

            }
        });
        // }

//        startButton = (Button) findViewById(R.id.button1);
//        stopButton = (Button) findViewById(R.id.button2);
    }


    void callingList(){
        progressDialog.show();
        CallingListBody callingListBody=new CallingListBody(date_st);

        CallingListRequest callingListRequest = ServiceGenerator.createService(CallingListRequest.class, DbHandler.getString(MainActivity.this, "bearer", ""));
        Call<CallingListPOJO> callingListPOJO = callingListRequest.call(callingListBody);
        callingListPOJO.enqueue(new Callback<CallingListPOJO>() {
            @Override
            public void onResponse(Call<CallingListPOJO> call, Response<CallingListPOJO> response) {
//                                Log.e("res_code", String.valueOf(response.code()));
//                                Log.e("res_data", response.body().getSuccess().toString());

                progressDialog.dismiss();
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    DbHandler.putString(MainActivity.this, "calling_list", gson.toJson(response.body().getData()));
                    expListView = (ExpandableListView) findViewById(R.id.expandable_list);

                    metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    width = metrics.widthPixels;
                    expListView.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(10));

                    listAdapter = new expandable_list_adapter_users(MainActivity.this, getSupportFragmentManager(), response.body().getData());
                    expListView.setAdapter(listAdapter);

                    int PERMISSION_ALL = 1;
                    String[] PERMISSIONS = {Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.MODIFY_PHONE_STATE, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE, Manifest.permission.CAPTURE_AUDIO_OUTPUT};

                    if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                } else if (response.code() == 403) {

                    Toast.makeText(MainActivity.this, "Not Authorized", Toast.LENGTH_LONG).show();
                    DbHandler.unsetSession(MainActivity.this, "isforcedLoggedOut");
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                }
            }

            @Override
            public void onFailure(Call<CallingListPOJO> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(MainActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).create().show();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void call() {

        DbHandler.putString(MainActivity.this,"call_id","");
        TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
        //Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent intent = new Intent(Intent.ACTION_CALL);
        //intent.setData(Uri.parse("tel:"+getArguments().getString("number")));
        intent.setData(Uri.parse("tel:"+getIntent().getExtras().getString("number")));

//        intent.putExtra("com.android.phone.force.slot", true);
//        intent.putExtra("Cdma_Supp", true);
//        if (item == 0) {//for sim1
//            for (String s : simSlotName){
//                intent.putExtra(s, 0); //0 or 1 according to sim.......
//            }
//
//            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0)
//            {
//                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE",
//                        phoneAccountHandleList.get(0));
//            }
//        } else {//for sim2
//            for (String s : simSlotName) {
//                intent.putExtra(s, 1); //0 or 1 according to sim.......
//            }
//            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1){
//                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE",
//                        phoneAccountHandleList.get(1));
//            }
//        }
        startActivity(intent);
        finish();

//        Intent callIntent = new Intent(Intent.ACTION_CALL)
//                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        callIntent.setData(Uri.parse("tel:8266063906"));
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#request
//            // Permissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        callIntent.putExtra("com.android.phone.force.slot", true);
//        callIntent.putExtra("Cdma_Supp", true);
//        callIntent.putExtra("simSlot", 0);
//        callIntent.putExtra("com.android.phone.extra.slot", 0);
//        context.startActivity(callIntent);

//        try {
//            startRecording();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void startRecording(View view) throws IOException {
        start();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void start() throws IOException{
//        startButton.setEnabled(false);
//        stopButton.setEnabled(true);
        //Creating file
        File dir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("sound", ".mp3", dir);
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            return;
        }
        File f=new File(dir,"BK.mp3");
        audiofile.renameTo(f);
        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

    public void stopRecording(View view) {
        stop();
    }

    public void stop(){
//        startButton.setEnabled(true);
//        stopButton.setEnabled(false);
        //stopping recorder
        recorder.stop();
        recorder.release();
        //after stopping the recorder, create the sound file and add it to media library.
        addRecordingToMediaLibrary();
    }

    public void call(View view) {
        if (isCallAllowed()) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:8266063906"));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
//            try {
//                start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else {
            requestCallPermission();
        }
    }

    private boolean isCallAllowed() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestCallPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {
        }

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CALL_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:8266063906"));
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                startActivity(callIntent);
//                try {
//                    start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }else{
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        invalidateOptionsMenu();
//        DbHandler.unsetSession(this,"logout");
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logout){
            DbHandler.unsetSession(this,"logout");
        }
        else if(id==R.id.change_pwd){
            dialog_change_pwd dialog= new dialog_change_pwd();
            dialog.show(getSupportFragmentManager(),"");
        }


        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onAttachedToWindow() {
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//        super.onAttachedToWindow();
//    }

    protected void addRecordingToMediaLibrary() {
        //creating content values of size 4
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gp");
        //values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");

        values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());

        //creating content resolver and storing it in the external content uri
        ContentResolver contentResolver = getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        //sending broadcast message to scan the media file so that it can be available
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
    }
}
