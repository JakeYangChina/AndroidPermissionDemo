package jake.yang.permission.library.core;

import android.app.Application;
import android.content.Context;
import android.util.ArrayMap;

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

@SuppressWarnings({"unused", "WeakerAccess"})
public class Permission {
    private static final ArrayMap<String, ArrayMap<Integer, Obj>> MAP = new ArrayMap<>();
    private static Application sApplication;

    public static void init(Application application) {
        sApplication = application;
    }

    public static void requestPermission(final Object currentObj) {
        if (sApplication == null) {
            throw new RuntimeException("Application is null.");
        }
        requestPermission(sApplication.getApplicationContext(), currentObj, PermissionUtils.DEFAULT_REQUEST_CODE);
    }

    public static void requestPermission(Object currentObj, int requestCode) {
        if (sApplication == null) {
            throw new RuntimeException("Application is null.");
        }
        requestPermission(sApplication.getApplicationContext(), currentObj, requestCode);
    }

    public static void requestPermission(Context context, Object currentObj) {
        requestPermission(sApplication.getApplicationContext(), currentObj, PermissionUtils.DEFAULT_REQUEST_CODE);
    }

    public static void requestPermission(final Context context, final Object currentObj, int requestCode) {
        checkNull(currentObj);

        final Class classes = currentObj.getClass();
        ArrayMap<Integer, Obj> arrayMap = MAP.get(classes.getSimpleName());
        if (arrayMap == null) {
            arrayMap = new ArrayMap<>();
            MAP.put(classes.getSimpleName(), arrayMap);

            Method[] declaredMethods = classes.getDeclaredMethods();

            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(RequestPermission.class)) {
                    RequestPermission requestPermission = method.getAnnotation(RequestPermission.class);
                    Chain chain = new Chain(context);
                    apply(requestPermission,
                            null,
                            null,
                            null,
                            arrayMap, method,
                            currentObj,
                            requestPermission.requestCode(),
                            chain);
                }

                if (method.isAnnotationPresent(RequestPermissionNoPassed.class)) {
                    RequestPermissionNoPassed requestPermissionNoPassed = method.getAnnotation(RequestPermissionNoPassed.class);
                    apply(null,
                            requestPermissionNoPassed,
                            null,
                            null,
                            arrayMap, method,
                            currentObj,
                            requestPermissionNoPassed.requestCode(),
                            null);
                }

                if (method.isAnnotationPresent(RequestPermissionDenied.class)) {
                    RequestPermissionDenied requestPermissionDenied = method.getAnnotation(RequestPermissionDenied.class);
                    apply(null,
                            null,
                            requestPermissionDenied,
                            null,
                            arrayMap, method,
                            currentObj,
                            requestPermissionDenied.requestCode(),
                            null);
                }

                if (method.isAnnotationPresent(RequestPermissionAutoOpenSetting.class)) {
                    RequestPermissionAutoOpenSetting requestPermissionAutoOpenSetting = method.getAnnotation(RequestPermissionAutoOpenSetting.class);
                    apply(null,
                            null,
                            null,
                            requestPermissionAutoOpenSetting,
                            arrayMap, method,
                            currentObj,
                            requestPermissionAutoOpenSetting.requestCode(),
                            null);
                }
            }
        }

        Set<Integer> integers = arrayMap.keySet();
        for (int i : integers) {
            final Obj o = arrayMap.get(i);
            if (o.mRequestCode == requestCode) {
                startPermissionActivity(context, currentObj, o);
                return;
            }
        }
    }

    private static void startPermissionActivity(Context context, final Object currentObj, final Obj o) {
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

                            if (o.mRequestPermissionAutoOpenSettingMethod != null && o.mChain != null) {
                                o.mRequestPermissionAutoOpenSettingMethod.invoke(currentObj, o.mChain);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private static void apply(
            RequestPermission requestPermission,
            RequestPermissionNoPassed requestPermissionNoPassed,
            RequestPermissionDenied requestPermissionDenied,
            RequestPermissionAutoOpenSetting requestPermissionAutoOpenSetting,
            ArrayMap<Integer, Obj> arrayMap,
            Method method,
            Object currentObj,
            int code,
            Chain chain) {

        if (requestPermission != null) {
            method.setAccessible(true);
            Obj o = arrayMap.get(code);
            if (o == null) {
                o = new Obj();
                arrayMap.put(code, o);
            }
            o.setChain(chain);
            o.applyPermission(currentObj, method, requestPermission.value(), code);
        }

        if (requestPermissionNoPassed != null) {
            method.setAccessible(true);
            Obj o = arrayMap.get(code);
            if (o == null) {
                o = new Obj();
                arrayMap.put(code, o);
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            o.applyPermissionNoPassed(currentObj, method, code, parameterTypes);
        }
        if (requestPermissionDenied != null) {
            Obj o = arrayMap.get(code);
            method.setAccessible(true);
            if (o == null) {
                o = new Obj();
                arrayMap.put(code, o);
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            o.applyPermissionDenied(currentObj, method, code, parameterTypes);
        }
        if (requestPermissionAutoOpenSetting != null) {
            Obj o = arrayMap.get(code);
            method.setAccessible(true);
            if (o == null) {
                o = new Obj();
                arrayMap.put(code, o);
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            o.applyAutoOpenSetting(currentObj, method, code, parameterTypes);
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
        destroy(arrayMap);
        MAP.remove(aClass.getSimpleName());
        clear();
    }

    private static void destroy(ArrayMap<Integer, Obj> arrayMap) {
        if (arrayMap != null) {
            Set<Integer> arrays = arrayMap.keySet();
            for (int key : arrays) {
                Obj obj = arrayMap.get(key);
                obj.clear();
            }
            arrays.clear();
            arrayMap.clear();
        }
    }

    public static void destroyAllPermission() {
        Set<String> keySet = MAP.keySet();
        for (String key : keySet) {
            ArrayMap<Integer, Obj> arrayMap = MAP.get(key);
            destroy(arrayMap);
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
