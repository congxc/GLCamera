/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.glcamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Presenter base class for Activity
 * Presenter层的实现基类
 *
 * @param <T> View delegate class type
 */
public abstract class ActivityPresenter<T extends IViewDelegate> extends AppCompatActivity {
    protected T viewDelegate;
    //显示上传异常记录广播
    public static final String ACTION_REPORT_UPLOAD_EXCEPTION = "ACTION_REPORT_UPLOAD_EXCEPTION";
    public final int REQUEST_CODE_0 = 10000;
    public final int REQUEST_CODE_1 = 10001;
    public final int REQUEST_CODE_2 = 10002;
    public final int REQUEST_CODE_3 = 10003;
    public final int REQUEST_CODE_4 = 10004;
    public final int REQUEST_CODE_5 = 10005;
    public final int RESULT_CODE_0  = 20000;
    public final int RESULT_CODE_1  = 20001;
    public final int RESULT_CODE_2  = 20002;
    public final int RESULT_CODE_3  = 20003;
    public final int RESULT_CODE_4  = 20004;
    public final int RESULT_CODE_5  = 20005;
    public final int PAGE_SIZE = 10;//每页加载条数
    public ActivityPresenter() {
        try {
            viewDelegate = getDelegateClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("create IDelegate error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDelegate.create(getLayoutInflater(), null, savedInstanceState);
        setContentView(viewDelegate.getRootView());
        initToolbar();
        viewDelegate.initWidget();
        bindEvenListener();
        loadData();
        if (softInputState()) {
            hideSoftInput();
        }

    }
    public void showToast(String text){
        if (viewDelegate != null) {
            viewDelegate.showToast(text);
        }
    }
    public void showToast(int textResId){
        if (viewDelegate != null) {
            viewDelegate.showToast(textResId);
        }
    }
    public void showLoadingView(){
        if (viewDelegate != null) {
          viewDelegate.showLoadingView();
        }
    }
    public void showLoadingView(int tipResId){
        if (viewDelegate != null) {
            viewDelegate.showLoadingView(tipResId);
        }
    }
    public void showLoadingView(String tip){
        if (viewDelegate != null) {
            viewDelegate.showLoadingView(tip);
        }
    }
    public void showContentView(){
        if (viewDelegate != null) {
            viewDelegate.showContentView();
        }
    }
    /**
     * 判断软键盘是否弹出
     *
     * @return
     */
    public boolean softInputState() {
        return getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }

    /**
     * 默认不弹出软键盘的方法
     */
    public void hideSoftInput() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 加载数据
     */
    protected void loadData(){

    }

    /**
     * 绑定事件
     */
    protected void bindEvenListener() {
    }

    protected void initToolbar() {
        Toolbar toolbar = viewDelegate.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (viewDelegate == null) {
            try {
                viewDelegate = getDelegateClass().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("create IDelegate error");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (viewDelegate.getOptionsMenuId() != 0) {
            getMenuInflater().inflate(viewDelegate.getOptionsMenuId(), menu);
        }
        super.onCreateOptionsMenu(menu);
        viewDelegate.initOptionMenu(menu);
        bindMenuEventListener();
        loadMenuData();
        return true;
    }

    /**
     * 加载菜单数据
     */
    protected void loadMenuData() {

    }

    /**
     * 绑定菜单事件
     */
    protected void bindMenuEventListener() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            hidesoftInputBord();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        hidesoftInputBord();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewDelegate = null;
    }

    protected abstract Class<T> getDelegateClass();

    /**
     * 启动新的activity
     *
     * @param clasz
     */
    private long lastClickTimer = 0;

    public void showActivity(Class<?> clasz) {
        if (System.currentTimeMillis() - lastClickTimer < 500) {
            lastClickTimer = System.currentTimeMillis();
            return;
        }
        lastClickTimer = System.currentTimeMillis();
        Intent intent = new Intent(getApplicationContext(), clasz);
        startActivity(intent);
    }

    /**
     * 启动新的activity，带数据传递
     *
     * @param clasz
     * @param data  传递的数据
     */
    public void showActivity(Class<?> clasz, Bundle data) {
        if (System.currentTimeMillis() - lastClickTimer < 500) {
            lastClickTimer = System.currentTimeMillis();
            return;
        }
        lastClickTimer = System.currentTimeMillis();
        Intent intent = new Intent(getApplicationContext(), clasz);
        intent.putExtras(data);
        startActivity(intent);
    }

    /**
     * 启动新的activity，带数据传递，并返回结果
     *
     * @param clasz
     * @param requestCode
     */
    public void showActivityForResult(Class<?> clasz, int requestCode) {
        if (System.currentTimeMillis() - lastClickTimer < 500) {
            lastClickTimer = System.currentTimeMillis();
            return;
        }
        lastClickTimer = System.currentTimeMillis();
        Intent intent = new Intent(getApplicationContext(), clasz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 启动新的activity，带数据传递，并返回结果
     *
     * @param clasz
     * @param requestCode
     */
    public void showActivityForResult(Class<?> clasz, Bundle data, int requestCode) {
        if (System.currentTimeMillis() - lastClickTimer < 500) {
            lastClickTimer = System.currentTimeMillis();
            return;
        }
        lastClickTimer = System.currentTimeMillis();
        Intent intent = new Intent(getApplicationContext(), clasz);
        intent.putExtras(data);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        viewDelegate.onWindowFocusChanged(hasFocus);
    }

    /**
     * 隐藏软键盘  系統強制关闭软键盘方法 v为获得焦点的View
     */
    public void hidesoftInputBord(View v) {
        if (v != null)
            ((InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    /**
     * 隐藏软键盘  系統強制关闭软键盘方法 v为获得焦点的View
     */
    public void hidesoftInputBord() {
        if (this.getCurrentFocus() != null)
            ((InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 验证暴力连续点击
     * @return
     */
    public boolean checkSingClick(){
        if (System.currentTimeMillis() - lastClickTimer < 500) {
            lastClickTimer = System.currentTimeMillis();
            return false;
        }
        lastClickTimer = System.currentTimeMillis();
        return true;
    }

}
