package ttit.com.shuvo.spotring.permission_checker;

import android.content.Context;
import android.provider.Settings;

public class OverlayPermissionChecker {
    public static boolean isOverlayEnabled(Context context) {
        return Settings.canDrawOverlays(context);
    }
}
