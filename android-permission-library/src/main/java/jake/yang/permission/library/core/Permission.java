package jake.yang.permission.library.core;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import jake.yang.permission.library.activity.PermissionActivity;
import jake.yang.permission.library.annotation.RequestPermission;
import jake.yang.permission.library.annotation.RequestPermissionAutoOpenSetting;
import jake.yang.permission.library.annotation.RequestPermissionDenied;
import jake.yang.permission.library.annotation.RequestPermissionNoPassed;
import jake.yang.permission.library.bean.Obj;
import jake.yang.permission.library.interfaces.IPermission;
import jake.yang.permission.library.utils.PermissionUtils;

@SuppressWarnings("unused")
public class Permission {
    private static final ArrayMap<String, ArrayMap<Integer, Obj>> MAP = new ArrayMap<>();
    private static Application sApplication;
    private static Chain sChain = new Chain();

    public static void requestPermission(final Object currentObj, String requestMethodName) {
        if (sApplication == null) {
            throw new RuntimeException("Application is null.");
        }
        requestPermission(sApplication.getApplicationContext(), currentObj, requestMethodName);
    }

    public static void init(Application application) {
        sApplication = application;
    }

    public static void requestPermission(final Context context, final Object currentObj, String requestMethodName) {
        checkNull(currentObj, requestMethodName);

        final Class classes = currentObj.getClass();
        ArrayMap<Integer, Obj> arrayMap = MAP.get(classes.getSimpleName());
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            MAP.put(classes.getSimpleName(), arrayMap);

            Method[] declaredMethods = classes.getDeclaredMethods();

            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(RequestPermission.class)) {
                    method.setAccessible(true);
                    RequestPermission requestPermission = method.getAnnotation(RequestPermission.class);
                    int code = requestPermission.requestCode();
                    Obj o = arrayMap.get(code);
                    if (o == null) {
                        o = new Obj();
                        arrayMap.put(code, o);
                    }

                    o.mRequestMethodName = method.getName();
                    o.mRequestPermissionMethod = method;
                    o.mObject = currentObj;
                    o.mRequestCode = code;
                    o.mPermission = requestPermission.value();

                }

                if (method.isAnnotationPresent(RequestPermissionNoPassed.class)) {
                    method.setAccessible(true);
                    RequestPermissionNoPassed requestPermissionNoPassed = method.getAnnotation(RequestPermissionNoPassed.class);
                    int code = requestPermissionNoPassed.requestCode();
                    Obj o = arrayMap.get(code);
                    if (o == null) {
                        o = new Obj();
                        arrayMap.put(code, o);
                    }

                    Class<?>[] parameterTypes = method.getParameterTypes();
                    o.mIsHaveParmPasted = parameterTypes != null && parameterTypes.length > 0;
                    o.mRequestPermissionCanceledMethod = method;
                    o.mObject = currentObj;
                    o.mRequestCode = code;
                }

                if (method.isAnnotationPresent(RequestPermissionDenied.class)) {
                    RequestPermissionDenied requestPermissionDenied = method.getAnnotation(RequestPermissionDenied.class);
                    int code = requestPermissionDenied.requestCode();
                    Obj o = arrayMap.get(code);
                    method.setAccessible(true);
                    if (o == null) {
                        o = new Obj();
                        arrayMap.put(code, o);
                    }

                    Class<?>[] parameterTypes = method.getParameterTypes();
                    o.mIsHaveParmDenied = parameterTypes != null && parameterTypes.length > 0;
                    o.mRequestPermissionDeniedMethod = method;
                    o.mObject = currentObj;
                    o.mRequestCode = code;
                }

                if (method.isAnnotationPresent(RequestPermissionAutoOpenSetting.class)) {
                    RequestPermissionAutoOpenSetting requestPermissionAutoOpenSetting = method.getAnnotation(RequestPermissionAutoOpenSetting.class);
                    int code = requestPermissionAutoOpenSetting.requestCode();
                    Obj o = arrayMap.get(code);
                    method.setAccessible(true);
                    if (o == null) {
                        o = new Obj();
                        arrayMap.put(code, o);
                    }

                    Class<?>[] parameterTypes = method.getParameterTypes();
                    o.mIsHaveParmDenied = parameterTypes != null && parameterTypes.length > 0;
                    o.mRequestPermissionAutoOpenSettingMethod = method;
                    o.mObject = currentObj;
                    o.mRequestCode = code;
                }
            }
        }

        Set<Integer> integers = arrayMap.keySet();
        for (int i : integers) {
            final Obj o = arrayMap.get(i);
            if (o.mRequestMethodName.equals(requestMethodName)) {
                PermissionActivity.startPermissionActivity(
                        sApplication != null ? sApplication.getApplicationContext() : context.getApplicationContext(),
                        o.mPermission,
                        o.mRequestCode,
                        new IPermission() {
                            @Override
                            public void ganted() {
                                try {
                                    if (o.mRequestPermissionMethod != null)
                                        o.mRequestPermissionMethod.invoke(currentObj);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void noPast(List<String> cancledPermission) {
                                try {
                                    if (o.mRequestPermissionCanceledMethod != null) {
                                        if (o.mIsHaveParmPasted) {
                                            o.mRequestPermissionCanceledMethod.invoke(currentObj, PermissionActivity.getNoPastList());
                                        } else {
                                            o.mRequestPermissionCanceledMethod.invoke(currentObj);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void denied(List<String> deniedPermission) {
                                try {
                                    if (o.mRequestPermissionDeniedMethod != null) {
                                        if (o.mIsHaveParmDenied) {
                                            o.mRequestPermissionDeniedMethod.invoke(currentObj, PermissionActivity.getList());
                                        } else {
                                            o.mRequestPermissionDeniedMethod.invoke(currentObj);
                                        }
                                    }

                                    if (o.mRequestPermissionAutoOpenSettingMethod != null){
                                        o.mRequestPermissionAutoOpenSettingMethod.invoke(currentObj, sChain);
                                        if (sChain.getState()){
                                            Log.e("MainActivity", "=========");
                                            PermissionUtils.goToMenu(context);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                break;
            }
        }

    }

    private static void checkNull(Object currentObj, String requestMethodName) {
        checkNull(currentObj);
        checkNull(requestMethodName);
    }

    private static void checkNull(String requestMethodName) {
        if (TextUtils.isEmpty(requestMethodName)) {
            throw new RuntimeException("requestMethodName is null.");
        }
    }


    private static void checkNull(Object currentObj) {
        if (currentObj == null) {
            throw new RuntimeException("currentObj is null.");
        }
    }

    public static void destroyPermission(final Object currentObj) {
        checkNull(currentObj);

        Class<?> aClass = currentObj.getClass();
        ArrayMap<Integer, Obj> arrayMap = MAP.get(aClass.getSimpleName());
        if (arrayMap != null) {
            arrayMap.clear();
            MAP.remove(aClass.getSimpleName());
        }
        clear();
    }

    public static void destroyAllPermission() {
        Set<String> keySet = MAP.keySet();
        for (String key : keySet) {
            ArrayMap<Integer, Obj> arrayMap = MAP.get(key);
            arrayMap.clear();
        }
        MAP.clear();
        clear();
    }

    private static void clear() {
        PermissionActivity.getList().clear();
        PermissionActivity.getNoPastList().clear();
        PermissionActivity.getPassList().clear();
        PermissionActivity.getRequestNoPassList().clear();
    }
}
