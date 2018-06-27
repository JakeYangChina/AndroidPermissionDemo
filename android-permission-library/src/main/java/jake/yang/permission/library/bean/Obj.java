package jake.yang.permission.library.bean;

import java.lang.reflect.Method;

public class Obj {
    public int mRequestCode;
    public String mRequestMethodName;

    public Method mRequestPermissionMethod;
    public Method mRequestPermissionCanceledMethod;
    public Method mRequestPermissionDeniedMethod;
    public Method mRequestPermissionAutoOpenSettingMethod;
    public Object mObject;
    public String[] mPermission;

    public boolean mIsHaveParmPasted;
    public boolean mIsHaveParmDenied;
}
