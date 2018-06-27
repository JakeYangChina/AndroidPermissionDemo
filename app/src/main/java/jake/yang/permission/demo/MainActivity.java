package jake.yang.permission.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import jake.yang.permission.library.annotation.RequestPermission;
import jake.yang.permission.library.annotation.RequestPermissionDenied;
import jake.yang.permission.library.annotation.RequestPermissionNoPassed;
import jake.yang.permission.library.core.Permission;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonA = findViewById(R.id.butAll);
        buttonA.setOnClickListener(this);
        Button buttonB = findViewById(R.id.butLocal);
        buttonB.setOnClickListener(this);
        Button butClass = findViewById(R.id.butClass);
        butClass.setOnClickListener(this);

    }

    /**
     * 说明：此权限框架可以运行在任意类内，可以是Activity，Fragment，Service或者是其它类内
     */

    /**
     * API：Permission类
     * public static void init(Application application)此方法可以不使用，如果使用了，可以直接调用requestPermission两个参数的方法
     *<p>
     * public static void requestPermission(Object currentObj, String requestMethodName)使用此方法前必须要先初始化init()方法
     * 参数一：当前类对象，参数二：要调用的方法名（必须是被@RequestPermission注解修饰的方法）
     *<p>
     * public static void requestPermission(Context context, Object currentObj, String requestMethodName)申请权限
     * 参数一：context，参数二：当前类对象，参数三：要调用的方法名（必须是被@RequestPermission注解修饰的方法）
     *<p>
     * public static void destroyPermission(Object currentObj)释放指定类内申请的权限，参数为：当前类对象
     *<p>
     * public static void destroyAllPermission()释放所有权限，应用退出时，调用此方法
     */

    /**
     * ①方法名任意写
     * <p>
     * ②@RequestPermission注解，指定要申请的权限（可以是多个）
     * 可以指定requestCode，不指定即为默认值，当权限全部授予时，就会回调此注解修饰的方法
     * <p>
     * ③@RequestPermissionNoPassed注解，可以指定requestCode，不指定即为默认值
     * 只要存在没有被授予的权限时（包括勾选拒绝后不再询问的权限）就会回调此注解修饰的方法
     * 可以指定参数为List<String>，未授予的权限集合
     * <p>
     * ④@RequestPermissionDenied注解，可以指定requestCode，不指定即为默认值
     * 只要有勾选拒绝后不再询问的权限，就会回调此注解修饰的方法
     * 可以指定参数为List<String>，被拒绝的权限的集合
     * <p>
     * ⑤@RequestPermissionAutoOpenSetting注解，可以指定requestCode，不指定即为默认值
     * 当指定的权限被勾选拒绝时，设置是否开启系统设置页面
     * 当被这个注解声明的方法需要指定一个参数Chain，这个参数控制是否开启系统设置页面，让用户手动开启权限
     * 当不使用此注解时，默认为不开启系统设置页面，可以在这个注解方法内弹对话框，让用户手动开启页面
     */

    //=================第一种写法，不指定requestCode请求码=======================
    @RequestPermission({Manifest.permission.ACCESS_COARSE_LOCATION})
    public void requestPermission() {
        Log.e(TAG, "requestPermission");
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
    }

    @RequestPermissionNoPassed
    public void requestPermissionNoPassed() {
        Log.e(TAG, "requestPermissionCanceled");
        Toast.makeText(this, "requestPermissionCanceled", Toast.LENGTH_SHORT).show();
    }

    @RequestPermissionDenied
    public void requestPermissionDenied() {
        Log.e(TAG, "requestPermissionDenied");
        Toast.makeText(this, "requestPermissionDenied", Toast.LENGTH_SHORT).show();
    }

    //=================第二种写法，指定requestCode请求码，相同的请求码为同一组=======================

    @RequestPermission(value = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode = 2)
    public void requestPermission2() {
        Log.e(TAG, "requestPermission2");
        Toast.makeText(this, "success2", Toast.LENGTH_SHORT).show();
    }

    //被@RequestPermissionNoPassed注解标注的方法可以指定一个List<String>参数，也可以不指定
    @RequestPermissionNoPassed(requestCode = 2)
    public void requestPermissionNoPassed2(List<String> noPassPermissionList) {
        Log.e(TAG, "requestPermissionCanceled2");
        for (String noPassPermission : noPassPermissionList) {
            Log.e(TAG, "未授予的权限：" + noPassPermission);
        }
        Toast.makeText(this, "requestPermissionCanceled2", Toast.LENGTH_SHORT).show();
    }

    @RequestPermissionDenied(requestCode = 2)
    public void requestPermissionDenied2(List<String> deniedPermissionList) {
        Log.e(TAG, "requestPermissionDenied2");
        for (String deniedPermission : deniedPermissionList) {
            Log.e(TAG, "勾选拒绝后不在询问选框的权限：" + deniedPermission);
        }
        Toast.makeText(this, "requestPermissionDenied2", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放权限
        Permission.destroyAllPermission();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butAll:
                //请求权限
                Permission.requestPermission(this, "requestPermission2");
                break;
            case R.id.butLocal:
                //请求权限
                Permission.requestPermission(this, this, "requestPermission");
                break;
            case R.id.butClass:
                //在其它类内请求权限
                TestDemo demo = new TestDemo();
                demo.requestPermission();
                demo.clear();
                break;
            default:
                break;
        }
    }
}
