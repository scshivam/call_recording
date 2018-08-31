package call_recording.jbglass.in.callrecording.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import android.telephony.TelephonyManager;


import call_recording.jbglass.in.callrecording.R;
import call_recording.jbglass.in.callrecording.Requests.ITelephony;

public class CallingScreen extends AppCompatActivity {

    Button end_call,speaker;
    boolean speaker_b=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        setContentView(R.layout.activity_calling_screen);

        end_call=(Button)findViewById(R.id.end_call);
        speaker=(Button)findViewById(R.id.speaker);

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!speaker_b) {
                    AudioManager audioManager = (AudioManager)
                            getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    audioManager.setSpeakerphoneOn(true);
                    speaker_b=true;
                    speaker.setText("Turn off speaker");
                }
                else{
                    AudioManager audioManager = (AudioManager)
                            getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                    audioManager.setSpeakerphoneOn(false);
                    speaker_b=false;
                    speaker.setText("Turn on speaker");

                }
            }
        });
        end_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//
//                Class clazz = null;
//                try {
//                    clazz = Class.forName(telephonyManager.getClass().getName());
//                    Method method = clazz.getDeclaredMethod("getITelephony");
//                    method.setAccessible(true);
//                    ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
//                    telephonyService.endCall();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectCall();
                    }
                }).start();


            }
        });

//
//        final View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        decorView.setOnSystemUiVisibilityChangeListener
//                (new View.OnSystemUiVisibilityChangeListener() {
//                    @Override
//                    public void onSystemUiVisibilityChange(int visibility) {
//
//                        Timer timer = new Timer();
//                        TimerTask task = new TimerTask() {
//                            @Override
//                            public void run() {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//                                        // Remember that you should never show the action bar if the
//                                        // status bar is hidden, so hide that too if necessary.
//                                     //   android.app.ActionBar actionBar = getActionBar();
//                                      //  actionBar.hide();
//                                        getSupportActionBar().hide();
//                                    }
//                                });
//                            }
//                        };
//
//                        timer.scheduleAtFixedRate(task, 0, 1);
//                    }
//                });

    }

    public void disconnectCall(){
//        TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
//
//        if (tm != null) {
//            boolean success = tm.;
//            // success == true if call was terminated.
//        }
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error1234",
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("err", "Exception object: " + e);
        }
    }

//    @Override
//    public void onAttachedToWindow() {
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
//        super.onAttachedToWindow();
//    }
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
////        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
////        Class clazz = null;
////        try {
////            clazz = Class.forName(telephonyManager.getClass().getName());
////            Method method = clazz.getDeclaredMethod("getITelephony");
////            method.setAccessible(true);
////            ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
////            telephonyService.endCall();
////        } catch (ClassNotFoundException e) {
////            e.printStackTrace();
////        } catch (NoSuchMethodException e) {
////            e.printStackTrace();
////        } catch (IllegalAccessException e) {
////            e.printStackTrace();
////        } catch (InvocationTargetException e) {
////            e.printStackTrace();
////        }
//        disconnectCall();
//
//        Toast.makeText(CallingScreen.this,"Code 123",Toast.LENGTH_LONG).show();
//        onBackPressed();
//        return true;
//        //return super.onKeyUp(keyCode, event);
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
////        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
////        Class clazz = null;
////        try {
////            clazz = Class.forName(telephonyManager.getClass().getName());
////            Method method = clazz.getDeclaredMethod("getITelephony");
////            method.setAccessible(true);
////            ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
////            telephonyService.endCall();
////        } catch (ClassNotFoundException e) {
////            e.printStackTrace();
////        } catch (NoSuchMethodException e) {
////            e.printStackTrace();
////        } catch (IllegalAccessException e) {
////            e.printStackTrace();
////        } catch (InvocationTargetException e) {
////            e.printStackTrace();
////        }
//      //  disconnectCall();
//
//        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
//            disconnectCall();
//
//            Toast.makeText(CallingScreen.this,"Clicked Home",Toast.LENGTH_LONG).show();
//
//            return true;
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Toast.makeText(CallingScreen.this,"Clicked back",Toast.LENGTH_LONG).show();
//            disconnectCall();
//
////
////            Intent nextFlow = new Intent(this, AppActionDropDownActivity.class);
////
////            startActivity(nextFlow);
//
//            return true;
//
//        }
//
//        return super.onKeyDown(keyCode, event);
//
//    }
//
//    interface ITelephony {
//
//        boolean endCall();
//
//        void answerRingingCall();
//
//        void silenceRinger();
//
//    }

//    @Override
//    public void onBackPressed() {
////        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
////        Class clazz = null;
////        try {
////            clazz = Class.forName(telephonyManager.getClass().getName());
////            Method method = clazz.getDeclaredMethod("getITelephony");
////            method.setAccessible(true);
////            ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
////            telephonyService.endCall();
////        } catch (ClassNotFoundException e) {
////            e.printStackTrace();
////        } catch (NoSuchMethodException e) {
////            e.printStackTrace();
////        } catch (IllegalAccessException e) {
////            e.printStackTrace();
////        } catch (InvocationTargetException e) {
////            e.printStackTrace();
////        }
//        disconnectCall();
//
//        Toast.makeText(CallingScreen.this,"Clicked back1",Toast.LENGTH_LONG).show();
//
//        //super.onBackPressed();
//    }


//    private void endCallIfBlocked() {
//        try {
//            // Java reflection to gain access to TelephonyManager's
//            // ITelephony getter
////            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
////            Class<?> c = Class.forName(tm.getClass().getName());
////            Method m = c.getDeclaredMethod("getITelephony");
////            m.setAccessible(true);
////            Telephony telephonyService = (Telephony) m.invoke(tm);
////
////            //if (new BlockNumberHelper(context).isBlocked(outGoingNumber)){
////                telephonyService = (Telephony) m.invoke(tm);
////                telephonyService.silenceRinger();
////                telephonyService.endCall();
//            //}
//            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//            Class clazz = Class.forName(telephonyManager.getClass().getName());
//            Method method = clazz.getDeclaredMethod("getITelephony");
//            method.setAccessible(true);
//            ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
//            telephonyService.endCall();
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public int  getDefaultSimmm(Context context) {
//
//        Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
//        Method method_getDefaultSim;
//        int defaultSimm = -1;
//        try {
//            method_getDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
//            method_getDefaultSim.setAccessible(true);
//            defaultSimm = (Integer) method_getDefaultSim.invoke(tm);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        Method method_getSmsDefaultSim;
//        int smsDefaultSim = -1;
//        try {
//            method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getSmsDefaultSim");
//            smsDefaultSim = (Integer) method_getSmsDefaultSim.invoke(tm);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return smsDefaultSim;
//    }
}
