package call_recording.jbglass.in.callrecording.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import call_recording.jbglass.in.callrecording.Config.DbHandler;
import call_recording.jbglass.in.callrecording.R;

/**
 * Created by satyam on 9/6/18.
 */

public class dialog_select_sim extends DialogFragment {

    RadioButton sim1,sim2;
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

    public static dialog_select_sim instance(String title,String number, String id){
        dialog_select_sim dialog_select_sim=new dialog_select_sim();
        Bundle b=new Bundle();
        b.putString("title",title);
        b.putString("number",number);
        b.putString("call_id",id);
        dialog_select_sim.setArguments(b);
        return dialog_select_sim;

    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View parentView = inflater.inflate(R.layout.dialog_select_sim, null);
        builder.setView(parentView);

        sim1=(RadioButton)parentView.findViewById(R.id.sim1);
        sim2=(RadioButton)parentView.findViewById(R.id.sim2);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(sim1.isChecked()){
                    item=0;
                }
                else{
                    item=1;
                }
                call();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void call() {

        DbHandler.putString(getActivity(),"call_id",getArguments().getString("call_id"));

        DbHandler.putString(getActivity(),"title",getArguments().getString("title"));
        TelecomManager telecomManager = (TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
        DbHandler.putString(getActivity(),"app","true");
        DbHandler.putString(getActivity(),"mob_number",getArguments().getString("number"));

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+getArguments().getString("number")));
       // intent.setData(Uri.parse("tel:9258697341"));

        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        if (item == 0) {//for sim1
            for (String s : simSlotName){
                intent.putExtra(s, 0); //0 or 1 according to sim.......
            }

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0)
            {
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                        phoneAccountHandleList.get(0));
            }
        } else {//for sim2
            for (String s : simSlotName) {
                intent.putExtra(s, 1); //0 or 1 according to sim.......
            }
            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1){
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                        phoneAccountHandleList.get(1));
            }
        }
        startActivity(intent);
        getActivity().finish();

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

}
