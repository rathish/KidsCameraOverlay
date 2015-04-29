package com.yobny.touchapps.kidscameraoverlay_0_1;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


public class CameraActivity extends Activity implements SurfaceHolder.Callback, SimpleGestureFilter.SimpleGestureListener {

    private Integer[] imageArray = {            // ARRAY IMAGES ITEMS
            R.drawable.img1, R.drawable.img2,   // (My test was with 18 items)
            R.drawable.img3, R.drawable.img4,
            R.drawable.img5, R.drawable.img6,
            R.drawable.img7, R.drawable.img8,
            R.drawable.img9, R.drawable.img10,
            R.drawable.img11
    };

    private Camera camera = null;

    private SurfaceView cameraSurfaceView = null;

    private SurfaceHolder cameraSurfaceHolder = null;

    private boolean previewing = false;

    RelativeLayout relativeLayout;

    private SimpleGestureFilter detector;

    private ImageView imageView;

    int currentImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set the window format
        //
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        // Request for non title feature
        //
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set the window flags for the full screen
        //
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        relativeLayout = (RelativeLayout) findViewById(R.id.containerImg);

        relativeLayout.setDrawingCacheEnabled(true);

        cameraSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);

        cameraSurfaceHolder = cameraSurfaceView.getHolder();

        cameraSurfaceHolder.addCallback(this);

        imageView = (ImageView) findViewById(R.id.imageView1);

        detector = new SimpleGestureFilter(this,this);
    }

    Camera.ShutterCallback cameraShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback cameraPictureCallbackRaw = new Camera.PictureCallback()  {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    Camera.PictureCallback cameraPictureCallbackJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    camera = Camera.open(camIdx);
                    break;
                }
            }

        }
        catch(RuntimeException e) {
            Toast.makeText(getApplicationContext(),
                    "Device camera  is not working properly, please try after sometime.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if(previewing) {
            camera.stopPreview();
            previewing = false;
        }
        try {



              Camera.Parameters parameters = camera.getParameters();
              parameters.setPreviewSize(640, 480);
              parameters.setPictureSize(640, 480);

            if (this.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "landscape");
                parameters.set("rotation", "90");
            } else if (this.getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT) {

                parameters.set("orientation", "portrait");
                parameters.set("rotation", "90");

            }
            camera.setParameters(parameters);
            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();
            previewing = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try{
            camera.stopPreview();
            camera.release();
            camera = null;
            previewing = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT : str = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";

                if ((currentImage < imageArray.length - 1) && (currentImage >=0)) {
                    currentImage++;
                } else {
                    currentImage = 0;
                }
                imageView.setImageResource(imageArray[currentImage]);
                break;
            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
                break;

        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
