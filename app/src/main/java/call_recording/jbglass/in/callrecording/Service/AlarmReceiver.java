package call_recording.jbglass.in.callrecording.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import java.util.List;
import java.util.Random;

import call_recording.jbglass.in.callrecording.Activity.FeedbackActivity;
import call_recording.jbglass.in.callrecording.Activity.MainActivity;
import call_recording.jbglass.in.callrecording.Config.DbHandler;
import call_recording.jbglass.in.callrecording.R;

/**
 * Created by satyam on 16/6/18.
 */

public class AlarmReceiver extends BroadcastReceiver {
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

    @Override
    public void onReceive(Context context, Intent intent2) {

        Log.e("comes","hello");
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
//        Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setData(Uri.parse("tel:"+ DbHandler.getString(context, "mob_number","")));
//        //intent.setData(Uri.parse("tel:9258697341"));
//
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
//
//        PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        int notificationId = 0; // just use a counter in some util class...
//        PendingIntent dismissIntent = PendingIntent.getDismissIntent(notificationId, context);

        Log.e("mn2",String.valueOf(DbHandler.getString(context,"mob_number","")));
        Log.e("mn3",String.valueOf(intent2.getExtras().getString("mob_no")));
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("number",intent2.getExtras().getString("mob_no"));
        notificationIntent.putExtra("action","call");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        long[] pattern = {500,500,500,500,500,500,500,500,500};

        Notification notification = builder.setContentTitle("Calling App")
                .setContentText("Call "+intent2.getExtras().getString("title"))
                .setTicker("New Message Alert!")
                .setSound(alarmSound)
                .setOnlyAlertOnce(true)
                .setVibrate(pattern)
                .setLights(Color.BLUE,500,500)
                //.addAction(R.drawable.ic_call_black_24dp,"Call Now",pendingIntentMaybe)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}