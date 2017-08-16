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

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.ButterKnife;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * View delegate base class
 * 视图层代理的基类
 *
 */
public abstract class AppViewDelegate implements IViewDelegate {
    private View rootView;
    protected FrameLayout mContentView;
    protected LayoutInflater mInflater;
    private Toolbar mToolbar;
    private int mToolBarSize;
    private View mLoadingView;

    public abstract int getRootLayoutId();
    /**
     * 隐藏软键盘  系統強制关闭软键盘方法 v为获得焦点的View
     */
    public void hidesoftInputBord(View v) {
        if (v != null)
            ((InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    public void create(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int rootLayoutId = getRootLayoutId();
        mInflater = inflater;
        rootView = inflater.inflate(rootLayoutId, container, false);
        initContentView();
        ButterKnife.bind(this, rootView);
    }



    @Override
    public void initOptionMenu(Menu menu) {
    }

    @Override
    public int getOptionsMenuId() {
        return 0 ;
    }

    public Toolbar getToolbar() {
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.tool_bar_layout, mContentView);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.icon_back_selector);
        return mToolbar;
    }
    public void setToolBarVisible(boolean visible){
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
        if (rootView != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootView.setLayoutParams(params);
        }
    }

    @Override
    public View getRootView() {
        return mContentView;
    }
    private void initContentView() {
        /*直接创建一个帧布局，作为视图容器的父容器*/
        mContentView = new FrameLayout(getContext());
        mContentView.setBackgroundColor(Color.WHITE);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(android.support.v7.appcompat.R.styleable.AppCompatTheme);
        /*获取主题中定义的悬浮标志*/
        boolean overly = typedArray.getBoolean(android.support.v7.appcompat.R.styleable.AppCompatTheme_windowActionBarOverlay, false);
        /*获取主题中定义的toolbar的高度*/
        mToolBarSize = (int) typedArray.getDimension(android.support.v7.appcompat.R.styleable.AppCompatTheme_actionBarSize, (int) getContext().getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
        typedArray.recycle();
        /*如果是悬浮状态，则不需要设置间距*/
        params2.topMargin = overly ? 0 : mToolBarSize;
        mContentView.addView(rootView, params2);

        mLoadingView = View.inflate(getContext(), R.layout.loading_view, null);
        mLoadingView.setVisibility(View.GONE);
        mContentView.addView(mLoadingView,params2);

    }
    public int getToolBarHeight(){
        return mToolBarSize;
    }
    public void setRootView(View rootView) {
        this.rootView = rootView;
        ButterKnife.bind(this, rootView);
    }

    @Override
    public void initWidget() {

    }

    public Context getContext() {
        return rootView.getContext();
    }
    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }
    /**
     * @param tipResId 提示信息资源ID
     */
    public void showToast(int tipResId){
        showToast(getContext().getString(tipResId));
    }

    /**
     * @param tips 提示信息
     */
    public void showToast(final String tips){
        Toast.makeText(getContext(),tips,Toast.LENGTH_LONG).show();
    }
    @Override
    public void showLoadingView(){
        showLoadingView(0);
    }

    @Override
    public void showLoadingView(String tip) {
        showLoadingViewOnMainThread(tip);
    }

    @Override
    public void showLoadingView(int tipResId){
        if(tipResId != 0){
            showLoadingView(getContext().getString(tipResId));
        }else{
            showLoadingView("");
        }
    }
    public void showLoadingViewOnMainThread(final String tip){
        if("main".equals(Thread.currentThread().getName())){
            try {
                if (mLoadingView == null) {
                    FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params2.topMargin =  mToolBarSize;
                    mLoadingView = View.inflate(getContext(), R.layout.loading_view, null);
                    mContentView.addView(mLoadingView,params2);
                }
                mLoadingView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                ((TextView)mLoadingView.findViewById(R.id.tv_tip)).setText(tip);
                mLoadingView.setVisibility(View.VISIBLE);
                mContentView.bringChildToFront(mLoadingView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void showContentView(){
            if(mLoadingView != null){
                mLoadingView.setVisibility(View.GONE);
            }
    }

    public void showDatePickerDialog(String date,DatePickerDialog.OnDateSetListener dateListener ){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
//        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year,
//                                  int month, int dayOfMonth) {
//                // Calendar月份是从0开始,所以month要加1
//                showToast(year+"-"+(month+1)+"-"+dayOfMonth);
//            }
//        };
        DatePickerDialog  dialog;
        int year = -1,month = -1,day = -1;
        try {
            if (!TextUtils.isEmpty(date)) {
                String[] split = date.split("-");
                year = Integer.parseInt(split[0]);
                month = Integer.parseInt(split[1]) - 1;
                day = Integer.parseInt(split[2]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(year != -1 && month != -1 && day != -1){
            dialog = new DatePickerDialog(getContext(), dateListener, year,month,day);

        }else{
            dialog = new DatePickerDialog(getContext(), dateListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

        }
        dialog.show();
    }
    private long lastClickTimer = 0;
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
