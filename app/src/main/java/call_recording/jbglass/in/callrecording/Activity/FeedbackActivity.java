package call_recording.jbglass.in.callrecording.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import call_recording.jbglass.in.callrecording.Config.DbHandler;
import call_recording.jbglass.in.callrecording.Fragments.dialog_feedback;
import call_recording.jbglass.in.callrecording.JSONBody.DispositionBody;
import call_recording.jbglass.in.callrecording.JSONBody.IncomingBody;
import call_recording.jbglass.in.callrecording.JSONBody.RemarkBody;
import call_recording.jbglass.in.callrecording.JSONBody.ReplicateCallBody;
import call_recording.jbglass.in.callrecording.JSONBody.UploadBody;
import call_recording.jbglass.in.callrecording.Models.DispositionDatumPOJO;
import call_recording.jbglass.in.callrecording.Models.DispositionPOJO;
import call_recording.jbglass.in.callrecording.Models.IncomingPOJO;
import call_recording.jbglass.in.callrecording.Models.RemarkPOJO;
import call_recording.jbglass.in.callrecording.Networking.ServiceGenerator;
import call_recording.jbglass.in.callrecording.R;
import call_recording.jbglass.in.callrecording.Requests.Disposition2Request;
import call_recording.jbglass.in.callrecording.Requests.DispositionsRequest;
import call_recording.jbglass.in.callrecording.Requests.IncomingRequest;
import call_recording.jbglass.in.callrecording.Requests.RemarkRequest;
import call_recording.jbglass.in.callrecording.Requests.ReplicateCall;
import call_recording.jbglass.in.callrecording.Requests.UploadRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends Activity {

//    ViewPager viewPager;
//
//    LinearLayout dotsLayout;
//    private TextView[] dots;
//    ViewPagerAdapter mSectionsPagerAdapter;

    ProgressDialog progressDialog;
    //int size=0;
    ListView radioGroup;
    List<DispositionDatumPOJO> dispositionDatumPOJOList=new ArrayList<DispositionDatumPOJO>();
    String call_id="";
    Button next;
    String parent_id="";
    EditText remark;
    String callls[];
    int selected=-1;
    int ind=0;
    String sch="T";
    int cnt=0;
    String date_st="";

    Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Date today = new Date();
        date_st=formatter.format(today);

        call_id=DbHandler.getString(this,"call_id","");
        next=(Button)findViewById(R.id.next);
        next.setVisibility(View.GONE);
        remark=(EditText)findViewById(R.id.remark);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        radioGroup=(ListView) findViewById(R.id.radio_group);

        if(DbHandler.contains(FeedbackActivity.this,"app"))
            DbHandler.remove(FeedbackActivity.this,"app");

        DispositionsRequest dispositionsRequest= ServiceGenerator.createService(DispositionsRequest.class, DbHandler.getString(this,"bearer",""));
        Call<DispositionPOJO> call=dispositionsRequest.call();
        call.enqueue(new Callback<DispositionPOJO>() {
            @Override
            public void onResponse(Call<DispositionPOJO> call, Response<DispositionPOJO> response) {
                progressDialog.dismiss();
                if(response.code()==200){
//                    radioGroup.removeAllViews();
                    dispositionDatumPOJOList=response.body().getDispositions();
                    if(response.body().getDispositions().size()>0) {
                        String callls[]=new String[response.body().getDispositions().size()];
                        for (int i = 0; i < response.body().getDispositions().size(); i++) {
                            callls[i]=response.body().getDispositions().get(i).getName();
//                                RadioButton rdbtn = new RadioButton(FeedbackActivity.this);
//                                rdbtn.setId((ind * 2) + i);
//                                //
//                                // ind++;
//                                rdbtn.setText(response.body().getDispositions().get(i).getName());
//                                radioGroup.addView(rdbtn);
                        }
                        ArrayAdapter adapter = new ArrayAdapter<String>(FeedbackActivity.this,android.R.layout.simple_list_item_1
                                ,callls);
                        radioGroup.setAdapter(adapter);
                    }

                }
                else if (response.code()==403){
                    Toast.makeText(FeedbackActivity.this,"Not Authorized",Toast.LENGTH_LONG).show();
                    DbHandler.unsetSession(FeedbackActivity.this,"isforcedLoggedOut");
                }
                else{
                    new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                }
            }

            @Override
            public void onFailure(Call<DispositionPOJO> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).create().show();
            }
        });

//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//             parent_id=dispositionDatumPOJOList.get(i).getId();
//             selected=i;
//            }
//        });

        radioGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(cnt==0) {
                    parent_id = dispositionDatumPOJOList.get(i).getId();
                    selected = i;
                    inflateRadioGroup();
                }
                else if(cnt==1){
                    if(i==0) {
                        sch = "T";
                        inflateRadioGroup();
                    }
                    else if(i==1) {
                        sch = "L";
                        inflateRadioGroup();
                    }
                    else if(i==2){
                        radioGroup.setVisibility(View.GONE);
                        remark.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        next.setText("Submit");
                    }


                }
                else if(cnt==2 && sch.equals("T")){
                    if(i<7){

                        int min=-1;
                        switch (i){
                            case 0:
                                min=5;
                                break;
                            case 1:
                                min=10;
                                break;
                            case 2:
                                min=30;
                                break;
                            case 3:
                                min=60;
                                break;
                            case 4:
                                min=90;
                                break;
                            case 5:
                                min=120;
                                break;
                            case 6:
                                min=360;
                                break;
                            default:
                                min=1;

                        }
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                        notificationIntent.putExtra("mob_no",DbHandler.getString(FeedbackActivity.this,"mob_number",""));
                        notificationIntent.putExtra("title",DbHandler.getString(FeedbackActivity.this,"title",""));
                        notificationIntent.addCategory("android.intent.category.DEFAULT");

                        PendingIntent broadcast = PendingIntent.getBroadcast(FeedbackActivity.this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                        // min=1;
                        Log.e("mn",String.valueOf(min*60));
                        Log.e("mn1",String.valueOf(DbHandler.getString(FeedbackActivity.this,"mob_number","")));
                        //min=1;
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.SECOND, min*60);

                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                        replicateCall();

                    }
                    else{
                        radioGroup.setVisibility(View.GONE);
                        remark.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        next.setText("Submit");
                    }

                }
                else{
                    radioGroup.setVisibility(View.GONE);
                    remark.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    next.setText("Submit");
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioGroup.getVisibility()==View.VISIBLE) {
                    if (selected == -1) {
                        Toast.makeText(FeedbackActivity.this,"Please select some option",Toast.LENGTH_LONG).show();
                    }
                    else{
                        inflateRadioGroup();
                        //ind++;
                    }
                }
                else{
                    submit_remark();
                    //Toast.makeText(FeedbackActivity.this,remark.getText().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    void replicateCall(){

        Log.e("date_string",date_st);
        progressDialog.show();

        ReplicateCallBody replicateCallBody=new ReplicateCallBody(call_id,date_st);
        ReplicateCall replicateCall= ServiceGenerator.createService(ReplicateCall.class, DbHandler.getString(FeedbackActivity.this,"bearer",""));
        Call<RemarkPOJO> call=replicateCall.call(replicateCallBody);
        call.enqueue(new Callback<RemarkPOJO>() {
            @Override
            public void onResponse(Call<RemarkPOJO> call, Response<RemarkPOJO> response) {
                progressDialog.dismiss();
                if(response.code()==200){


                }
                else if (response.code()==403){
                    Toast.makeText(FeedbackActivity.this,"Not Authorized",Toast.LENGTH_LONG).show();
                    DbHandler.unsetSession(FeedbackActivity.this,"isforcedLoggedOut");
                }
                else{
                    new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                }
            }

            @Override
            public void onFailure(Call<RemarkPOJO> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).create().show();
            }
        });

        radioGroup.setVisibility(View.GONE);
        remark.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        next.setText("Submit");
    }


    private void uploadFile(final String filePath, final String fileName,final String call_id) {
        class UF extends AsyncTask<String, String, String> {
            InputStream inputStream;
            private Dialog loadingDialog;
            String responseString="";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(FeedbackActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                // progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {

                Log.e("comes","comes");

//                String mob=params[2];
                InputStream is = null;
//                SharedPreferences sf=getSharedPreferences("MyData", Context.MODE_PRIVATE);
//
//                String token=sf.getString("token","");

//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                nameValuePairs.add(new BasicNameValuePair("token", token));
//                nameValuePairs.add(new BasicNameValuePair("text", "test"));


                String fp = params[0];
                String fn = params[1];
                try {
                    HttpPost httpPost;

                    HttpClient httpClient = new DefaultHttpClient();
                    httpPost = new HttpPost("http://139.59.83.5:8081/api/uploads");
                    httpPost.addHeader("Authorization",DbHandler.getString(FeedbackActivity.this,"bearer",""));
                    //httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    File file = new File(fp);

                    FileBody fileBody = new FileBody(file);
                    MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    multipartEntity.addPart("recording", fileBody);
                    httpPost.setEntity(multipartEntity);

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity entity = httpResponse.getEntity();
                    int statusCode = httpResponse.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        responseString = EntityUtils.toString(entity);
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }
                    Log.e("comes","no error");


                } catch (ClientProtocolException e) {
                    responseString = e.toString();
                    Log.e("comes","error1");

                } catch (IOException e) {
                    Log.e("comes","error2");

                    responseString = e.toString();
                }

                return responseString;

            }

            @Override
            protected void onPostExecute(final String result) {

                // progressDialog.dismiss();
                Log.e("TAG", "Response from server: " + result);

                Log.e("comes","comes123");

                super.onPostExecute(result);
                String s = result.trim();
                Log.e("TAG", "Response from server: " + s);

                File fil=new File(filePath);
                boolean bool = fil.delete();
//
//                Log.e("bool",String.valueOf(bool));
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    String filename=jsonObject.getString("filename");
                    Log.e("fname",filename+" "+call_id);

                    //progressDialog.show();
                    UploadBody uploadBody=new UploadBody(filename,call_id);
                    Log.e("str_test",new Gson().toJson(uploadBody));

                    UploadRequest uploadRequest=ServiceGenerator.createService(UploadRequest.class,DbHandler.getString(FeedbackActivity.this,"bearer",""));
                    Call<RemarkPOJO> call=uploadRequest.call(uploadBody);
                    call.enqueue(new Callback<RemarkPOJO>() {
                        @Override
                        public void onResponse(Call<RemarkPOJO> call, Response<RemarkPOJO> response) {
                            //progressDialog.dismiss();
                            Log.e("error1234",String.valueOf(response.code()));
                            //Log.e("error123",response.body());
                        }

                        @Override
                        public void onFailure(Call<RemarkPOJO> call, Throwable t) {
                            // progressDialog.dismiss();
                            Log.e("update_Error",t.getMessage());
                        }
                    });
                    if(DbHandler.contains(FeedbackActivity.this,"call_id"))
                        DbHandler.remove(FeedbackActivity.this,"call_id");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("comes","cerr");

                }




            }
        }



        UF l = new UF();
        l.execute(filePath,fileName);


    }

    void submit_remark(){
        selected=-1;
        final RemarkBody remarkBody=new RemarkBody(remark.getText().toString(),call_id);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RemarkRequest remarkRequest= ServiceGenerator.createService(RemarkRequest.class, DbHandler.getString(this,"bearer",""));
        Call<RemarkPOJO> call=remarkRequest.call(remarkBody);
        call.enqueue(new Callback<RemarkPOJO>() {
            @Override
            public void onResponse(Call<RemarkPOJO> call, Response<RemarkPOJO> response) {
                progressDialog.dismiss();
                if(response.code()==200){
                    File dir=null;
//                    if (!Build.BRAND.equalsIgnoreCase("xiaomi")) {
//                        dir = Environment.getExternalStorageDirectory();
//
//                    }
//                    else{
//                        String path = Environment.getExternalStorageDirectory().toString() + "/MIUI/sound_recorder/call_rec";
//                        dir = new File(path);
//
//                    }

                    dir = Environment.getExternalStorageDirectory();
                    File[] files = dir.listFiles();


                    for (File file : files) {
                        if (file.getName().startsWith("BKOut_")) {
                            Uri uri=Uri.fromFile(new File(file.getAbsolutePath()));
                            Log.e("file_name",file.getAbsolutePath());
                            Log.e("file_uri",String.valueOf(uri));

                            String call_id2=file.getName().split("_")[1];
                            Log.e("call_od",call_id2);
                            Log.e("call_od2",String.valueOf(call_id2.split(".wav")));
                            String call_id=call_id2.split(".wav")[0];



                            uploadFile(file.getAbsolutePath(),file.getName(),call_id);
                        }

                    }
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    for (final File file : files) {
                        if (file.getName().startsWith("BKIn_")) {
                            Uri uri=Uri.fromFile(new File(file.getAbsolutePath()));
                            Log.e("file_name",file.getAbsolutePath());
                            Log.e("file_uri",String.valueOf(uri));

                            progressDialog.show();
                            String cal_nu2=file.getName().split("_")[1];
                            String num=cal_nu2.split(".wav")[0];
                            Log.e("num",num);

                            IncomingBody incomingBody=new IncomingBody(num,"I");
                            IncomingRequest incomingRequest=ServiceGenerator.createService(IncomingRequest.class,DbHandler.getString(FeedbackActivity.this,"bearer",""));
                            Call<IncomingPOJO> call1=incomingRequest.call(incomingBody);
                            call1.enqueue(new Callback<IncomingPOJO>() {
                                @Override
                                public void onResponse(Call<IncomingPOJO> call, Response<IncomingPOJO> response) {
                                    progressDialog.dismiss();
                                    if(response.code()==200){
                                        Gson gson=new Gson();
                                        Log.e("get_data",gson.toJson(response.body()));
                                        uploadFile(file.getAbsolutePath(),file.getName(),response.body().getCall_id());
                                    }
                                    else if (response.code()==403){
                                        Toast.makeText(FeedbackActivity.this,"Not Authorized",Toast.LENGTH_LONG).show();
                                        DbHandler.unsetSession(FeedbackActivity.this,"isforcedLoggedOut");
                                    }
                                    else {
                                        new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        onBackPressed();
                                                    }
                                                }).create().show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<IncomingPOJO> call, Throwable t) {
                                    progressDialog.dismiss();
                                    new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    onBackPressed();
                                                }
                                            }).create().show();
                                }
                            });

                        }

                    }
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    //Toast.makeText(FeedbackActivity.this,response.body().getMsg(),Toast.LENGTH_LONG).show();
//                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//                    homeIntent.addCategory( Intent.CATEGORY_HOME );
//                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(homeIntent);
                    startActivity(new Intent(FeedbackActivity.this,MainActivity.class).putExtra("action","intent"));

                    finish();
                }
                else if (response.code()==403){
                    Toast.makeText(FeedbackActivity.this,"Not Authorized",Toast.LENGTH_LONG).show();
                    DbHandler.unsetSession(FeedbackActivity.this,"isforcedLoggedOut");
                }
                else{
                    new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                }
            }

            @Override
            public void onFailure(Call<RemarkPOJO> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).create().show();
            }
        });
    }

    void inflateRadioGroup(){
        selected=-1;
        final DispositionBody dispositionBody=new DispositionBody(parent_id,call_id);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Disposition2Request dispositionsRequest= ServiceGenerator.createService(Disposition2Request.class, DbHandler.getString(this,"bearer",""));
        Call<DispositionPOJO> call=dispositionsRequest.call(dispositionBody);
        call.enqueue(new Callback<DispositionPOJO>() {
            @Override
            public void onResponse(Call<DispositionPOJO> call, Response<DispositionPOJO> response) {
                progressDialog.dismiss();
                if(response.code()==200){

                    // radioGroup.removeAllViews();
                    dispositionDatumPOJOList=response.body().getDispositions();
                    // for (int row = 0; row < 1; row++) {
                    //    radioGroup.setOrientation(LinearLayout.HORIZONTAL);
                    if (dispositionDatumPOJOList != null) {
                        if (response.body().getDispositions().size() > 0) {
                            callls = new String[response.body().getDispositions().size()];

                            for (int i = 0; i < response.body().getDispositions().size(); i++) {
                                callls[i] = response.body().getDispositions().get(i).getName();

                            }
                            ArrayAdapter adapter = new ArrayAdapter<String>(FeedbackActivity.this, android.R.layout.simple_list_item_1, callls);
                            radioGroup.setAdapter(adapter);
                        }
                        else if(cnt  == 0){
                            callls = new String[3];
                            callls[0] = "Schedule for today";
                            callls[1] = "Schedule later";
                            callls[2] = "Cancel";
                            ArrayAdapter adapter = new ArrayAdapter<String>(FeedbackActivity.this, android.R.layout.simple_list_item_1, callls);
                            radioGroup.setAdapter(adapter);
                            cnt++;

                        }
                        else if(cnt==1 && sch.equals("T")){

                            callls = new String[8];
                            callls[0] = "Call after 5 minutes";
                            callls[1] = "Call after 10 minutes";
                            callls[2] = "Call after 30 minutes";
                            callls[3] = "Call after 60 minutes";
                            callls[4] = "Call after 90 minutes";
                            callls[5] = "Call after 120 minutes";
                            callls[6] = "Call after 360 minutes";
                            callls[7] = "Cancel";
                            ArrayAdapter adapter = new ArrayAdapter<String>(FeedbackActivity.this, android.R.layout.simple_list_item_1, callls);
                            radioGroup.setAdapter(adapter);
                            cnt++;

                        }
                        else if(cnt==1 && sch.equals("L")){
                            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                      int dayOfMonth) {
                                    // TODO Auto-generated method stub
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                    date_st=formatter.format(myCalendar.getTime());
                                    replicateCall();


                                }

                            };


                            new DatePickerDialog(FeedbackActivity.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                        }
                    }
                    else{
                        radioGroup.setVisibility(View.GONE);
                        remark.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        next.setText("Submit");
                    }

                }
                else if (response.code()==403){
                    Toast.makeText(FeedbackActivity.this,"Not Authorized",Toast.LENGTH_LONG).show();
                    DbHandler.unsetSession(FeedbackActivity.this,"isforcedLoggedOut");
                }
                else{
                    new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            }).create().show();
                }
            }

            @Override
            public void onFailure(Call<DispositionPOJO> call, Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(FeedbackActivity.this).setTitle("Error").setMessage("Unable to connect to server")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).create().show();
            }
        });

    }

}
