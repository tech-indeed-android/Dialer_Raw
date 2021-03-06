package com.android.dialer;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.android.contacts.common.activity.TransactionSafeActivity;
import com.android.contacts.common.interactions.TouchPointManager;
import com.android.dialer.m1000systemdialog.SetupDialog;

/**
 * Created by Administrator on 2016/7/26.
 */
public class TestFragmentActivity  extends TransactionSafeActivity implements DialtactsFragment.IControlDeleteBtn{
    private static final String TAG_Dialtacts_Fragment = "dialtacts_fragment";
    private DialtactsFragment mDlf = null;//主fragment
    private SetupDialog mSetupdialog = null;//设置dialog
    private ImageButton mMenuButtonDelete = null;//删除按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_fragment_layout);

        ActionBar actionBar = getActionBar();
        //在actionbar中放入搜索框
        actionBar.setCustomView(R.layout.dialtacts_actionbar);
        actionBar.setDisplayShowCustomEnabled(true);

        mMenuButtonDelete = (ImageButton)findViewById(R.id.dialtacts_bottom_menu_button_delete1);
        mMenuButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDlf != null && mDlf.isVisible()) {
                    mDlf.processDeleteBtn();
                }
            }
        });

        //绑定fragment
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        DialtactsFragment fragment = new DialtactsFragment();

        mDlf = fragment;

        transaction.add(R.id.test_fragment_back, fragment);
        transaction.commitAllowingStateLoss();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_MENU) {
            // 弹出设置按钮

            SetupDialog.Builder customBuilder = new SetupDialog.Builder(this);
            mSetupdialog = customBuilder.create();
            customBuilder.setNegativeButton(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent callSettingsIntent = new Intent(TelecomManager.ACTION_SHOW_CALL_SETTINGS);
                    callSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(callSettingsIntent);
                    mSetupdialog.dismiss();
                }
            });
            mSetupdialog = customBuilder.create();
            mSetupdialog.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            TouchPointManager.getInstance().setPoint((int) ev.getRawX(), (int) ev.getRawY());
        }
        return super.dispatchTouchEvent(ev);

    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);

        if (mDlf != null && mDlf.isVisible()){
            setIntent(newIntent);

            mDlf.mStateSaved = false;
            mDlf.displayFragment(newIntent);

        }
    }

    @Override
    public void onBackPressed() {
        if (mDlf != null && mDlf.isVisible()){
            if (mDlf.mStateSaved) {
                return;
            }
            if (mDlf.mIsDialpadShown) {
                if(!mDlf.isInSearchUi()) {
                    finish();
                    return;
                }
                if(mDlf.mSmartDialSearchFragment!=null && mDlf.mSmartDialSearchFragment.popupWindowIsShowing()){
                    mDlf.mSmartDialSearchFragment.popupWindowDismiss();
                }else {
                    if (TextUtils.isEmpty(mDlf.mSearchQuery) ||
                            (mDlf.mSmartDialSearchFragment != null && mDlf.mSmartDialSearchFragment.isVisible()
                                    && mDlf.mSmartDialSearchFragment.getAdapter().getCount() == 0)) {
                        mDlf.exitSearchUi();
                    }
                    mDlf.hideDialpadFragment(true, true);
                }
//            hideDialpadFragment(true, true);
//        } else if (isInSearchUi()) {
//            exitSearchUi();
//            DialerUtils.hideInputMethod(mParentLayout);
            } else {
                super.onBackPressed();
            }
        }


    }

    @Override
    public void bottomMenuButtonDeleteSlideOut() {
        if (null != mMenuButtonDelete){
            mMenuButtonDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void bottomMenuButtonDeleteSlideIn() {
        if (null != mMenuButtonDelete){
            mMenuButtonDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setEnable(boolean b) {
        if (null != mMenuButtonDelete){
            mMenuButtonDelete.setEnabled(b);
        }
    }
}
