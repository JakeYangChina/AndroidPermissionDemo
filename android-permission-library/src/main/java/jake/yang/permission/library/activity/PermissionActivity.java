package jake.yang.permission.library.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import jake.yang.permission.library.R;
import jake.yang.permission.library.interfaces.IPermission;
import jake.yang.permission.library.utils.PermissionUtils;


public class PermissionActivity extends AppCompatActivity {
    private static final String PARAM_PERMISSION = "param_permission";
    private static final String PARAM_REQUEST_CODE = "param_request_code";
    //private static final String PARAM_IPERMISSION = "param_iPermission";
    private static IPermission mIPermission;
    private int mRequestCode;

    public static ArrayList<String> LIST = new ArrayList<>();
    public static ArrayList<String> LIST_PASS = new ArrayList<>();
    public static ArrayList<String> LIST_REQUEST_NO_PASS = new ArrayList<>();
    public static ArrayList<String> LIST_NO_PAST = new ArrayList<>();

    @SuppressWarnings("unused")
    public static void startPermissionActivity(Context context, String[] permissions, int requestCode, IPermission iPermission) {

        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putStringArray(PARAM_PERMISSION, permissions);
        bundle.putInt(PARAM_REQUEST_CODE, requestCode);
        mIPermission = iPermission;
        //bundle.putSerializable(PARAM_IPERMISSION, iPermission);
        intent.putExtras(bundle);
        context.startActivity(intent);
        if (context instanceof AppCompatActivity) {
            //屏蔽掉页面启动动画
            ((AppCompatActivity) context).overridePendingTransition(0, 0);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.permission);
        Intent intent = getIntent();
        mRequestCode = intent.getIntExtra(PARAM_REQUEST_CODE, -1);
        String[] permissions = intent.getStringArrayExtra(PARAM_PERMISSION);
        /*Serializable serializable = intent.getSerializableExtra(PARAM_IPERMISSION);

        if (serializable != null && serializable instanceof IPermission) {
            mIPermission = (IPermission) serializable;
        }*/
        if (permissions == null || mRequestCode < 0 || mIPermission == null) {
            this.finish();
            return;
        }

        LIST_PASS.clear();
        if (PermissionUtils.hasPermission(this, LIST_PASS, permissions)) {
            if (mIPermission != null) {
                //已经授权
                mIPermission.ganted();
            }
            finish();
            return;
        }

        LIST_REQUEST_NO_PASS.clear();
        for (String permission : permissions) {
            if (!LIST_PASS.contains(permission)) {
                //申请权限
                LIST_REQUEST_NO_PASS.add(permission);
            }
        }
        ActivityCompat.requestPermissions(this, LIST_REQUEST_NO_PASS.toArray(new String[LIST_REQUEST_NO_PASS.size()]), mRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (mRequestCode != requestCode) {
            finish();
            return;
        }


        //用户取消
        if (mIPermission != null) {
            LIST_NO_PAST.clear();
            PermissionUtils.getCanclePermission(LIST_NO_PAST, grantResults, permissions);
            if (LIST_NO_PAST.size() > 0) {
                //已拒绝
                mIPermission.noPast(LIST_NO_PAST);
            }
        }


        //请求权限成功
        if (PermissionUtils.verifyPermission(this, grantResults)) {
            if (mIPermission != null) {
                //已经授权
                mIPermission.ganted();
            }
            finish();
            return;
        }

        LIST.clear();
        //用户点击了不再显示
        if (!PermissionUtils.shouldShowRequestPermissionRationale(this, LIST, grantResults, permissions)) {
            if (mIPermission != null) {
                //已拒绝
                mIPermission.denied(LIST);
            }
            finish();
            return;
        }

        finish();
    }

    public static List<String> getList() {
        return LIST;
    }

    public static List<String> getNoPastList() {
        return LIST_NO_PAST;
    }

    public static List<String> getRequestNoPassList() {
        return LIST_REQUEST_NO_PASS;
    }

    public static List<String> getPassList() {
        return LIST_PASS;
    }

    @Override
    public void finish() {
        super.finish();
        //屏蔽页面退出动画
        overridePendingTransition(0, 0);
    }
}
