package jake.yang.permission.demo;

import android.Manifest;
import android.util.Log;

import jake.yang.permission.library.annotation.RequestPermission;
import jake.yang.permission.library.annotation.RequestPermissionAutoOpenSetting;
import jake.yang.permission.library.annotation.RequestPermissionDenied;
import jake.yang.permission.library.annotation.RequestPermissionNoPassed;
import jake.yang.permission.library.core.Chain;
import jake.yang.permission.library.core.Permission;

@SuppressWarnings("unused")
public class TestDemo {
    public void requestPermission() {
        Permission.requestPermission(this, 6);
    }

    @RequestPermission(value = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode = 6)
    public void request() {
        Log.e(MainActivity.TAG, "request");
    }

    @RequestPermissionNoPassed(requestCode = 6)
    public void noPass() {
        Log.e(MainActivity.TAG, "noPass");
    }

    @RequestPermissionDenied(requestCode = 6)
    public void denied() {
        Log.e(MainActivity.TAG, "denied");
    }

    @RequestPermissionAutoOpenSetting(requestCode = 6)
    public void autoOpenSetting(Chain chain) {
        chain.open();
        Log.e(MainActivity.TAG, "autoOpenSetting");
    }

    public void clear() {
        Permission.destroyPermission(this);
    }

}
