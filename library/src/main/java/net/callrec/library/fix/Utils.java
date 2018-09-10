package net.callrec.library.fix;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Viktor Degtyarev on 16.10.17
 * E-mail: viktor@degtyarev.biz
 */
public class Utils {
    public static String getDefaultPath(Context context) {
        return String.format("%s%s",
                Utils.normalDir(Environment.getExternalStorageDirectory().getAbsolutePath()),
                "/");
    }

    private static String normalDir(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return dir;
        }

        dir = dir.replace('\\', '/');
        if (!dir.substring(dir.length() - 1, dir.length()).equals("/")) {
            dir += "/";
        }
        return dir;
    }

    public static String makeFileName(int typeCall, String callId) {
        String nameSubscr = typeCall==ProcessingBase.TypeCall.INSTANCE.getINC()?"BKIn":"BKOut";

        return String.format("%s_%s", nameSubscr, callId);
    }
}
