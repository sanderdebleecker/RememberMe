package be.sanderdebleecker.herinneringsapp.Helpers;

import android.content.pm.PackageManager;

public class HardwareHelper {
    //readability
    public static boolean hasCamera(PackageManager packageManager) {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
    public static boolean hasMic(PackageManager packageManager) {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }
}
