package jake.yang.permission.library.bean;

import java.lang.reflect.Method;

import jake.yang.permission.library.core.Chain;

@SuppressWarnings("WeakerAccess")
public class Obj {
    public int mRequestCode;
    public String mRequestMethodName;

    public Method mRequestPermissionMethod;
    public Method mRequestPermissionCanceledMethod;
    public Method mRequestPermissionDeniedMethod;
    public Method mRequestPermissionAutoOpenSettingMethod;
    public Object mObject;
    public String[] mPermission;

    public Chain mChain;

    public boolean mIsHaveParmPasted;
    public boolean mIsHaveParmDenied;

    public void applyPermission(Object currentObj, Method method, String[] requestPermission, int code) {
        this.mRequestMethodName = method.getName();
        this.mRequestPermissionMethod = method;
        this.mObject = currentObj;
        this.mRequestCode = code;
        this.mPermission = requestPermission;
    }

    public void applyPermissionNoPassed(Object currentObj, Method method, int code, Class<?>[] parameterTypes) {
        this.mRequestPermissionCanceledMethod = method;
        this.mIsHaveParmPasted = parameterTypes != null && parameterTypes.length > 0;
        this.mObject = currentObj;
        this.mRequestCode = code;
    }

    public void applyPermissionDenied(Object currentObj, Method method, int code, Class<?>[] parameterTypes) {
        this.mRequestPermissionDeniedMethod = method;
        this.mIsHaveParmDenied = parameterTypes != null && parameterTypes.length > 0;
        this.mObject = currentObj;
        this.mRequestCode = code;
    }

    public void applyAutoOpenSetting(Object currentObj, Method method, int code, Class<?>[] parameterTypes) {
        this.mIsHaveParmDenied = parameterTypes != null && parameterTypes.length > 0;
        this.mRequestPermissionAutoOpenSettingMethod = method;
        this.mObject = currentObj;
        this.mRequestCode = code;
    }

    public void clear(){
        this.mObject = null;
        this.mRequestPermissionAutoOpenSettingMethod = null;
        this.mRequestPermissionDeniedMethod = null;
        this.mRequestPermissionCanceledMethod = null;
        this.mRequestPermissionMethod = null;
        this.mPermission = null;
        this.mChain = null;
    }

    public void setChain(Chain chain){
        this.mChain = chain;
    }

}
