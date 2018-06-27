package jake.yang.permission.library.core;

@SuppressWarnings("unused")
//@Aspect
public class PermissionAspect {
    private static final String TAG = "PermissionAspect";

    /*@Pointcut("execution(@jake.yang.permission.library.annotation.RequestPermission * *(..)) && @annotation(permission)")
    public void requestPermission(RequestPermission permission) {

    }

    @Around("requestPermission(permission)")
    public void aroundJointPoint(final ProceedingJoinPoint joinPoint, RequestPermission permission) throws Throwable{
        Log.d(TAG, "aroundJonitPoint error ");
        //初始化context
        Context context = null;

        final Object aThis = joinPoint.getThis();
        if (aThis instanceof Context) {
            context = (Context) aThis;
        } else if (aThis instanceof android.support.v4.app.Fragment) {
            context = ((android.support.v4.app.Fragment) aThis).getActivity();
        } else if (aThis instanceof android.app.Fragment) {
            context = ((android.app.Fragment) aThis).getActivity();
        }

        if (context == null || permission == null) {
            Log.d(TAG, "aroundJonitPoint error ");
            return;
        }
        final Context finalContext = context;
        PermissionActivity.startPermissionActivity(finalContext, permission.value(), permission.requestCode(), new IPermission() {
            @Override
            public void ganted(String[] gantedPermission) {
                try {
                    //权限全部申请成功
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void cancled(String[] cancledPermission) {
                PermissionUtils.invokAnnotation(aThis, RequestPermissionCanceled.class);
            }

            @Override
            public void denied(String[] deniedPermission) {
                PermissionUtils.invokAnnotation(aThis, RequestPermissionDenied.class);
                //弹出对话框
                PermissionUtils.goToMenu(finalContext);
            }

            @Override
            public void finish() {

            }
        });

    }*/
}
