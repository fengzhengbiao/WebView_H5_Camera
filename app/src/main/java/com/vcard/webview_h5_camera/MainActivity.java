package com.vcard.webview_h5_camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

public class MainActivity extends AppCompatActivity implements VChromeClient.OpenFileChooserCallBack {
    private static final int CAMERA_STORAGE = 0x123;
    private static final String TAG = "CameraDemo";
    private ValueCallback<Uri> mUploadMsg;
    private ValueCallback<Uri[]> mUploadMsgForAndroid5;
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rxPermissions = new RxPermissions(this);
        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setBuiltInZoomControls(true);
        VChromeClient vChromeClient = new VChromeClient(this);
        webView.setWebChromeClient(vChromeClient);
        webView.loadUrl("http://mx.liujingongchang.com/");
        findViewById(R.id.btn_camera).setOnClickListener(v -> rxPermissions
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        takePhoto();
                    } else {
                        // At least one permission is denied
                        Toast.makeText(this, "没有权限", Toast.LENGTH_LONG).show();
                    }
                }));

    }

    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
        this.mUploadMsg = uploadMsg;
        takePhoto();
    }

    @Override
    public boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        this.mUploadMsgForAndroid5 = filePathCallback;
        takePhoto();
        return true;
    }

    private String photoPath;
    private Uri photoUri;

    private void takePhoto() {
        File photoFile = new File(Environment.getExternalStorageDirectory(), "image/" + System.currentTimeMillis() + ".jpg");
        photoPath = photoFile.getAbsolutePath();
        if (!photoFile.getParentFile().exists()) photoFile.getParentFile().mkdirs();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
        } else {
            photoUri = Uri.fromFile(photoFile);
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, CAMERA_STORAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_STORAGE:
                try {
                    if (resultCode != Activity.RESULT_OK) {
                        if (mUploadMsg != null) {
                            mUploadMsg.onReceiveValue(null);
                        }

                        if (mUploadMsgForAndroid5 != null) {         // for android 5.0+
                            mUploadMsgForAndroid5.onReceiveValue(null);
                        }
                        return;
                    }


                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsg == null) {
                            return;
                        }


                        if (!new File(photoPath).exists()) {
                            Log.e(TAG, "sourcePath empty or not exists.");
                            break;
                        }
                        Toast.makeText(MainActivity.this, "拍照成功", Toast.LENGTH_LONG).show();
                        mUploadMsg.onReceiveValue(photoUri);

                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mUploadMsgForAndroid5 == null) {        // for android 5.0+
                            return;
                        }
                        if (!new File(photoPath).exists()) {
                            Log.e(TAG, "sourcePath empty or not exists.");
                            break;
                        }
                        Toast.makeText(MainActivity.this, "拍照成功", Toast.LENGTH_LONG).show();
                        mUploadMsgForAndroid5.onReceiveValue(new Uri[]{photoUri});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }
}
