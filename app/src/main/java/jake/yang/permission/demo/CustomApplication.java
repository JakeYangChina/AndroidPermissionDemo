package jake.yang.permission.demo;

import android.app.Application;

import jake.yang.permission.library.core.Permission;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Permission.init(this);
    }
}
