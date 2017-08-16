package com.glcamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import com.glcamera.rx.RxAndroidUtils;
import com.glcamera.rx.SimpleFlowableOnSubscribe;
import com.glcamera.rx.SimpleFlowableSubscriber;

import org.reactivestreams.Subscription;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import io.reactivex.functions.Consumer;

public class CardCameraActivity extends ActivityPresenter<CardCameraActivityViewDelegate> implements View.OnClickListener {

    @Override
    protected Class<CardCameraActivityViewDelegate> getDelegateClass() {
        return CardCameraActivityViewDelegate.class;
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.getCameraPreview().setOnTakePicCallBack(new CameraPreviewView.OnTakePicCallBack() {
            @Override
            public void onPictureTaken(byte[] data) {
                viewDelegate.setEnablePicButton(true);
                byteToBitmap(data);
            }
        });

        viewDelegate.bindClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_pic:
                viewDelegate.setEnablePicButton(false);
                viewDelegate.showLoadingView();
                viewDelegate.getCameraPreview().takePicture();
                break;
        }
    }

    public  void byteToBitmap(final byte[] imgByte) {
        RxAndroidUtils.createObservable(new SimpleFlowableOnSubscribe<Bitmap>() {
            @Override
            protected Bitmap callNext() throws Exception {
                Bitmap bitmap = null,bitmap1 = null;
                InputStream input = null;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    input = new ByteArrayInputStream(imgByte);
                    SoftReference softRef = new SoftReference<>(BitmapFactory.decodeStream(
                            input, null, options));
                    bitmap = (Bitmap) softRef.get();
                    bitmap1 = centerSquareScaleBitmap(bitmap);
                } finally {
                    if (input != null) {
                        input.close();
                    }
                    return bitmap1;
                }
            }
        }, new SimpleFlowableSubscriber<Bitmap>() {
            @Override
            public void invokeOnNext(Bitmap bitmap) {
               showToast("take pic success");
            }

            @Override
            public void invokeOnError(Throwable t) {
                t.printStackTrace();
                showContentView();
                showToast("take pic failed");
            }

            @Override
            public void invokeOnComplete() {
                showContentView();
            }
        }, new Consumer<Subscription>() {
            @Override
            public void accept(@NonNull Subscription subscription) throws Exception {
                showLoadingView();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (viewDelegate != null) {
            viewDelegate.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (viewDelegate != null) {
            viewDelegate.releaseCameraRes();
        }
        super.onPause();
    }

    @Override
    public void onDestroy(){
        if (viewDelegate != null) {
            viewDelegate.onDestory();
        }
        super.onDestroy();
        setResult(RESULT_OK);
    }
    /**

     * @param bitmap      原图
     * @return  缩放截取正中部分后的位图。
     */
    public  Bitmap centerSquareScaleBitmap(Bitmap bitmap){
        if(null == bitmap ){
            return  null;
        }
        //w:2592 h:1944 原图大小   w :110 85  7  9  h: 73  48  15  13

        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();
        int edgeWidth = (int) (widthOrg*100/110.0f); //有效宽度
        int edgeHeight = (int) (heightOrg*58/73.0f); //有效高度
        Bitmap result = null;
        //1.截取
        if(widthOrg > edgeWidth && heightOrg > edgeHeight)  {
            //从图中截取正中间的部分。
            int xTopLeft = (widthOrg - edgeWidth) / 2;
            int yTopLeft = (heightOrg - edgeHeight) / 2;
            result = Bitmap.createBitmap(bitmap, xTopLeft-10, yTopLeft, edgeWidth, edgeHeight);
            bitmap.recycle();
        }
        int max = Math.max(edgeWidth, edgeHeight);
        //2.缩放
        if(max > 1024){//设置最大宽高为1024个像素
            int newWidth;
            int newHeight;
            if (max == edgeWidth) {
                newWidth = 1024;
                newHeight = (int) (1024.0f/edgeWidth*edgeHeight);
            }else{
                newHeight = 1024;
                newWidth = (int) (1024.0f/edgeHeight*edgeWidth);
            }
            result =  Bitmap.createScaledBitmap(result,newWidth,newHeight,true);
        }
        return result;
    }

//    /**
//     *
//     * @param itemBitmap
//     * @param type
//     * @return 图片解析得到的文字内容
//     * @throws Exception
//     */
//    private String getPicText(final Bitmap itemBitmap, String type) throws Exception{
//        if (itemBitmap == null) {
//            return "";
//        }
//        long statTime = System.currentTimeMillis();
//        //1.判断字典是否存在 不存在就复制
//        if (!mCheckedTessdata) {
//            String path = SDCardUtils.getRootPath()+ Config.TESSDATA_PATH;
//            String pathName = path + "ch_xc.traineddata";
//            String pathNameNumber = path + "nnumber.traineddata";
//            String pathNameSexNation = path + "sexnation.traineddata";
//            File file = new File(pathName);
//            if (!file.exists() || file.length() <= 0) {
//                InputStream inputStream = getAssets().open("ch_xc.traineddata");//所有汉字库
//                FileUtil.copyFile(inputStream,"ch_xc.traineddata");
//            }
//            File numberfile = new File(pathNameNumber);
//            if (!numberfile.exists() || numberfile.length() <= 0) {
//                InputStream inputStream = getAssets().open("nnumber.traineddata");//出生日期、有效期、身份证号字库
//                FileUtil.copyFile(inputStream,"nnumber.traineddata");
//            }
//            File nationfile = new File(pathNameSexNation);
//            if (!nationfile.exists() || nationfile.length() <= 0) {
//                InputStream inputStream = getAssets().open("sexnation.traineddata");//性别、名族字库
//                FileUtil.copyFile(inputStream,"sexnation.traineddata");
//            }
//
//        }
//        mCheckedTessdata = true;
//        Bitmap bitmap = ImgPretreatment
//                .converyToGrayImg(itemBitmap);//图片进行灰度处理
//        TessBaseAPI  mBaseApi = new TessBaseAPI();
//        //为识别库设置 训练好的字体的路劲，字体一定要放置在一个名为tessdata的文件夹下，否则会报错，无法正确初始化
//        if(TextUtils.equals(IDCardColumnType.IDNO.getValue(),type) ||
//                TextUtils.equals(IDCardColumnType.BIRTHDAY.getValue(),type) ||
//                TextUtils.equals(IDCardColumnType.VALIDATE.getValue(),type)){
//
//            mBaseApi.init(SDCardUtils.getRootPath()+ Config.APP_PATH, "nnumber");
//
//        }else if(TextUtils.equals(IDCardColumnType.SEX.getValue(),type) ||
//                TextUtils.equals(IDCardColumnType.NATION.getValue(),type)){
//
//            mBaseApi.init(SDCardUtils.getRootPath()+ Config.APP_PATH, "sexnation");
//        } else {
//
//            mBaseApi.init(SDCardUtils.getRootPath()+ Config.APP_PATH, "ch_xc");
//        }
//        mBaseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_COLUMN);
//
//        // 必须加此行，tess-two要求BMP必须为此配置
//        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//        mBaseApi.setImage(bitmap);
//        String text = mBaseApi.getUTF8Text();
//        mBaseApi.clear();
//        mBaseApi.end();
//        LogUtils.info("TAGxc", "getPicText: "+type+":"+(System.currentTimeMillis()-statTime));
//        return text;
//    }



    /**
     * 图像识别
     */
//    private void processImage(final boolean isForWard,final Bitmap bitmap){
//        RxAndroidUtils.createObservable(new SimpleFlowableOnSubscribe<JSONObject>() {
//            @Override
//            protected JSONObject callNext() throws Exception {
//                int width = bitmap.getWidth();
//                int height = bitmap.getHeight();
//                int lineHeight = (int) (height*8/75.0f);//每一行字的高度
//
//                if (mJsonObject == null) {
//                    mJsonObject = new JSONObject();
//                }
//                if (isForWard) {//是头像面
//                    int headWidth = (int) (width * 41/130.0f),headHeight = (int) (height * 48/75.0f),
//                            headLeft = (int) (width * 81/130.0f),headTop = (int) (height *10/75.0f);
//                    int left = (int) (width * 23/130.0f);
//                    int idLeft = (int) (width *43/130.0f+0.5f);//身份证左边
//                    int idWidth = (int) (width * 72/130.0f+0.5f);
//                    int nameWidth = (int) (width * 27/130.0f);
//                    int sexWidth = (int) (width * 7/130.0f);
//                    int birthdayWidth = (int) (width * 45/130.0f);
//                    int addressWidth = (int) (width * 60/130.0f);
//
//                    int top = (int) (height * 9/75.0f);
//                    int sexTop = (int) (height * 19/75.0f );
//                    int birthdayTop = (int) (height * 29/75.0f - 0.5f);
//                    int addressTop = (int) (height * 39/75.0f - 0.5f);
//                    int idTop = (int) (height - (height * 12/75.0f) );
//                    int nationLeft = (int) (width * 50/130.0f);
//                    Bitmap IDBitmap = Bitmap.createBitmap(bitmap, idLeft, idTop , idWidth, lineHeight);
//                    String id = getPicText(IDBitmap,IDCardColumnType.IDNO.getValue());
//                    if (!ValidatorUtils.isIDCard(id)) {//验证身份证格式是否正确
//                        mJsonObject.put(IDCardColumnType.IDNO.getValue(),IDCardColumnType.ERROR);
//                        return mJsonObject;
//                    }else{
//                        mJsonObject.put(IDCardColumnType.IDNO.getValue(),id);
//                    }
//                    //1.截取姓名
//                    Bitmap nameBitmap = Bitmap.createBitmap(bitmap, left, top, nameWidth, lineHeight);
//                    //2.截取性别
//                    Bitmap sexBitmap = Bitmap.createBitmap(bitmap, left, sexTop, sexWidth, lineHeight);
//                    Bitmap nationBitmap = Bitmap.createBitmap(bitmap, nationLeft, sexTop, nameWidth, lineHeight);
//                    Bitmap birthdayBitmap = Bitmap.createBitmap(bitmap, left, birthdayTop , birthdayWidth, lineHeight);
//                    Bitmap addressBitmap = Bitmap.createBitmap(bitmap, left, addressTop , addressWidth, lineHeight);
//                    Bitmap headBitmap = Bitmap.createBitmap(bitmap, headLeft, headTop , headWidth, headHeight);
////                    FileUtil.saveBitmap(nameBitmap,"namebitmap");
////                    FileUtil.saveBitmap(sexBitmap,"sexbitmap");
////                    FileUtil.saveBitmap(nationBitmap,"nationbitmap");
////                    FileUtil.saveBitmap(birthdayBitmap,"birthdaybitmap");
////                    FileUtil.saveBitmap(addressBitmap,"addressbitmap");
////                    FileUtil.saveBitmap(IDBitmap,"idbitmap");
////                    FileUtil.saveBitmap(headBitmap,"headbitmap");
//                    String name = getPicText(nameBitmap, IDCardColumnType.NAME.getValue());
//                    String sex = getPicText(sexBitmap, IDCardColumnType.SEX.getValue());
//                    if (!"男".equals(sex)) {
//                        List<String> sexList = new ArrayList<>();
//                        sexList.add("女");
//                        sexList.add("汉");
//                        for (String s : sexList) {//形近字处理
//                            if (s.equals(sex)) {
//                                sex = "女";
//                                break;
//                            }
//                        }
//                        if (TextUtils.isEmpty(sex)) {
//                           sex = "男";
//                        }
//                    }
//
//                    String nation = getPicText(nationBitmap,IDCardColumnType.NATION.getValue());
//                    String birthday = getPicText(birthdayBitmap, IDCardColumnType.BIRTHDAY.getValue());
//                    String address = getPicText(addressBitmap, IDCardColumnType.ADDRESS.getValue());
//                    LogUtils.info("TAGxc", "callNext: idcardNo:"+id);
//                    mJsonObject.put(IDCardColumnType.NAME.getValue(),name);
//                    mJsonObject.put(IDCardColumnType.SEX.getValue(),sex);
//                    mJsonObject.put(IDCardColumnType.NATION.getValue(),nation);
//                    mJsonObject.put(IDCardColumnType.BIRTHDAY.getValue(),birthday);
//                    mJsonObject.put(IDCardColumnType.ADDRESS.getValue(),address);
//                    mJsonObject.put(IDCardColumnType.HEAD.getValue(),headBitmap);
//                    mJsonObject.put(IDCardColumnType.IMG.getValue(),bitmap);
//                }else{
//                    int validateWidth = (int) (width * 72/130.0f+0.5f);
//                    int backLeft = (int) (width * 50/130.0f);
//                    int licenseTop = (int) (height * 55/75.0f);
//                    int validateTop = (int) (height * 66/75.0f);
//                    Bitmap licenseBitmap = Bitmap.createBitmap(bitmap, backLeft, licenseTop , validateWidth, lineHeight);
//                    Bitmap validateBitmap = Bitmap.createBitmap(bitmap, backLeft, validateTop , validateWidth, lineHeight);
////                    FileUtil.saveBitmap(licenseBitmap,"licensebitmap");
////                    FileUtil.saveBitmap(validateBitmap,"validatebitmap");
//                    mJsonObject.put(IDCardColumnType.LICENSEISSUE.getValue(),getPicText(licenseBitmap, IDCardColumnType.LICENSEISSUE.getValue()));
//                    mJsonObject.put(IDCardColumnType.VALIDATE.getValue(),getPicText(validateBitmap, IDCardColumnType.VALIDATE.getValue()));
//                }
//
//                LogUtils.info("TAGxc", "invokeOnNext: "+mJsonObject);
//                //1.保存身份证图片
//                return mJsonObject;
//            }
//        }, new SimpleFlowableSubscriber<JSONObject>() {
//            @Override
//            public void invokeOnNext(JSONObject jsonObject) {
//                LogUtils.info("TAGxc", "invokeOnNext: time = " + (System.currentTimeMillis() - mStartTime));
//                try {
//                    if (jsonObject.get(IDCardColumnType.IDNO.getValue()) == IDCardColumnType.ERROR) {
//                        mJsonObject = null;
//                        viewDelegate.showToast(R.string.tip_idcard_message_error);
//                    }else{
//                        if (isForWard) {
//                            viewDelegate.showToast(R.string.please_capture_id_card_national);
//                            ObjectCacheManager.put(IDCardColumnType.class.getSimpleName(),jsonObject);
//                            viewDelegate.updateUI(false);
//                        }else{
//                            if(TextUtils.isEmpty(jsonObject.getString(IDCardColumnType.LICENSEISSUE.getValue()))||
//                                    TextUtils.isEmpty(jsonObject.getString(IDCardColumnType.VALIDATE.getValue()))){
//                                viewDelegate.showToast(R.string.tip_idcard_message_error);
//                            }else{
//                                viewDelegate.showToast(R.string.tip_scan_success);
//                                ObjectCacheManager.put(IDCardColumnType.class.getSimpleName(),jsonObject);
//                                setResult(RESULT_OK);
//                                finish();
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    mJsonObject = null;
//                    viewDelegate.showToast(R.string.tip_idcard_message_error);
//                }
//            }
//
//            @Override
//            public void invokeOnError(Throwable t) {
//                viewDelegate.showContentView();
//                t.printStackTrace();
//                mJsonObject = null;
//                viewDelegate.showToast(R.string.tip_scan_failed);
//            }
//
//            @Override
//            public void invokeOnComplete() {
//                viewDelegate.showContentView();
//            }
//        });
//
//
//    }


}
