package jake.yang.permission.library.core;

import android.content.Context;

import jake.yang.permission.library.utils.PermissionUtils;

@SuppressWarnings("unused")
public class Chain {
    private boolean mIsOpen;
    private Context mContext;

    public void open() {
        PermissionUtils.goToMenu(mContext);
    }

    Chain(Context context ){
        this.mContext = context.getApplicationContext();
    }

}
