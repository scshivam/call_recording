package call_recording.jbglass.in.callrecording.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import call_recording.jbglass.in.callrecording.Activity.FeedbackActivity;
import call_recording.jbglass.in.callrecording.Activity.MainActivity;
import call_recording.jbglass.in.callrecording.Config.DbHandler;
import call_recording.jbglass.in.callrecording.Fragments.dialog_select_sim;
import call_recording.jbglass.in.callrecording.JSONBody.HistoryBody;
import call_recording.jbglass.in.callrecording.Models.HistoryDataPOJO;
import call_recording.jbglass.in.callrecording.Models.ListDatumPOJO;
import call_recording.jbglass.in.callrecording.Models.MemberInfoPOJO;
import call_recording.jbglass.in.callrecording.Networking.ServiceGenerator;
import call_recording.jbglass.in.callrecording.R;
import call_recording.jbglass.in.callrecording.Requests.HistoryRequest;
import call_recording.jbglass.in.callrecording.Requests.MemberInfoRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by satyam on 15/7/17.
 */

public class expandable_list_adapter_users extends BaseExpandableListAdapter {

    private Context context;
    ProgressDialog progressDialog;
    static private int CALL_PERMISSION_CODE = 23;
    public static MediaRecorder recorder;
    public static File audiofile = null;
    FragmentManager fragmentManager;
    public static final String TAG = "MediaRecording";

    List<ListDatumPOJO> listDatumPOJOS=new ArrayList<ListDatumPOJO>();
    public expandable_list_adapter_users(Context context, FragmentManager supportFragmentManager, List<ListDatumPOJO> data) {
        this.context = context;
        this.fragmentManager=supportFragmentManager;
        this.listDatumPOJOS=data;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.users_item, null);
        }
        TextView subtitle=(TextView)convertView.findViewById(R.id.sub_title);

        subtitle.setText(listDatumPOJOS.get(groupPosition).getSubtitle());
        final AppCompatButton call;
        call = (AppCompatButton) convertView.findViewById(R.id.call_now);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_select_sim dialog=dialog_select_sim.instance(listDatumPOJOS.get(groupPosition).getTitle(), listDatumPOJOS.get(groupPosition).getMobile(),listDatumPOJOS.get(groupPosition).getId());
                dialog.show(fragmentManager,"");
                //call();
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDatumPOJOS.size();
    }

    @Override
    public int getGroupCount() {
        return listDatumPOJOS.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.users_list_header, null);
        }

        final ImageView menu;
        TextView title,name;
        title=(TextView)convertView.findViewById(R.id.title);
        name=(TextView)convertView.findViewById(R.id.name);

        menu = (ImageView) convertView.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, FeedbackActivity.class));
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, menu);
                //inflating menu from xml resource
                popup.inflate(R.menu.recycler_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
//                            case R.id.remark:
//                                context.startActivity(new Intent(context, FeedbackActivity.class));
//                                return true;
                            case R.id.history:
                                final ProgressDialog progressDialog;
                                progressDialog=new ProgressDialog(context);
                                progressDialog.setMessage("Loading...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                HistoryBody historyBody=new HistoryBody(listDatumPOJOS.get(groupPosition).getMobile());

                                HistoryRequest historyRequest = ServiceGenerator.createService(HistoryRequest.class, DbHandler.getString(context, "bearer", ""));
                                Call<HistoryDataPOJO> historyDataPOJOCall = historyRequest.call(historyBody);
                                historyDataPOJOCall.enqueue(new Callback<HistoryDataPOJO>() {
                                    @Override
                                    public void onResponse(Call<HistoryDataPOJO> call, Response<HistoryDataPOJO> response) {

                                        if (response.code() == 200) {
                                            progressDialog.dismiss();
                                            String msg="<ul style=\"list-style-type:disc\">";
                                            for(int i=0;i<response.body().getData().size();i++){
                                                msg=msg+"<li>&nbsp "+response.body().getData().get(i).getEmpDetails().get(0).getName()+" ("+response.body().getData().get(i).getCallDate().split("T")[0]+")<p>"+response.body().getData().get(i).getRemarks()+"</p></li>";
                                            }
                                            msg=msg+"</ul>";

                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle("History");
                                            AlertDialog alertDialog=builder.create();

                                            builder.setTitle(Html.fromHtml(msg));
                                            alertDialog.getWindow().setLayout(600, 400);


                                            new AlertDialog.Builder(context).setTitle("History").setMessage(Html.fromHtml(msg)).setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).create().show();


                                        } else if (response.code() == 403) {
                                            progressDialog.dismiss();

                                            Toast.makeText(context, "Not Authorized", Toast.LENGTH_LONG).show();
                                            DbHandler.unsetSession(context, "isforcedLoggedOut");
                                        } else {
                                            progressDialog.dismiss();
                                            new android.support.v7.app.AlertDialog.Builder(context).setTitle("Error").setMessage("Unable to connect to server")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                        }
                                                    }).create().show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<HistoryDataPOJO> call, Throwable t) {
                                        progressDialog.dismiss();
                                        new android.support.v7.app.AlertDialog.Builder(context).setTitle("Error").setMessage("Unable to connect to server")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                }).create().show();

                                    }
                                });
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        title.setText(listDatumPOJOS.get(groupPosition).getTitle());
        name.setText(listDatumPOJOS.get(groupPosition).getName());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void startRecording() throws IOException {
        start();
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
        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }

//    private boolean isCallAllowed() {
//        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);
//
//        if (result == PackageManager.PERMISSION_GRANTED)
//            return true;
//
//        return false;
//    }
//
//    private void requestCallPermission() {
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)) {
//        }
//
//        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, CALL_PERMISSION_CODE);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if (requestCode == CALL_PERMISSION_CODE) {
//
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
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
//                context.startActivity(callIntent);
////                try {
////                    start();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//            }else{
//                Toast.makeText(context,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
//            }
//        }
//    }


}