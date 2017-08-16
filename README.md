# GLCamera
Preview camera use GLSurfaceView  include landscape and portrait 
## 适用横屏 竖屏（默认为横屏模式，只需要到CameraPreviewView下替换Vss即可适配竖屏）
## 自动手动对焦功能，默认视野拉近可做修改
 mParameters = mCamera.getParameters();
  if (mParameters.isZoomSupported()){
      mParameters.setZoom(14);
      mCamera.startSmoothZoom(14);
  }
## 包含RXJava2.0封装适用
