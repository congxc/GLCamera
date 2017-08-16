package com.glcamera;

import android.view.View;
import android.widget.Button;

import butterknife.BindView;

/**
 * 身份证扫描
 */
public class CardCameraActivityViewDelegate extends AppViewDelegate {

    @BindView(R.id.camPreview)
    CameraPreviewView mCameraPreview;
    @BindView(R.id.btn_take_pic)
    Button mBtnTakePic;//拍照

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_card_camera;
    }

    public CameraPreviewView getCameraPreview() {
        return mCameraPreview;
    }


    public void releaseCameraRes() {
        if (mCameraPreview != null) {
            mCameraPreview.releaseRes();
        }
    }
    public void onDestory(){
        if (mCameraPreview != null) {
            mCameraPreview.releaseRes();
            mCameraPreview = null;
        }
    }
    public void onResume(){
        if (mCameraPreview != null) {
            mCameraPreview.onResume();
        }
    }


    public void setEnablePicButton(boolean isEnabled) {
        mBtnTakePic.setEnabled(isEnabled);
    }


    @Override
    public void initWidget() {
        mBtnTakePic.setEnabled(true);
        mCameraPreview.setCameraFrontBack(CameraPreviewView.CameraBack);
    }
    public void bindClickListener(final View.OnClickListener onClickListener){
        mBtnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkSingClick()) {
                    return;
                }
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }

            }
        });
    }
}
