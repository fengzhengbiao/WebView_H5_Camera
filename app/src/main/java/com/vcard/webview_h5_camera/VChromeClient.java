package com.vcard.webview_h5_camera;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/*********************************************
 *  Author  JokerFish 
 *  Create   2018/5/9
 *  Description 
 *  Email fengzhengbiao@vcard100.com
 **********************************************/
public class VChromeClient extends WebChromeClient {

    private OpenFileChooserCallBack mOpenFileChooserCallBack;

    public VChromeClient(OpenFileChooserCallBack openFileChooserCallBack) {
        mOpenFileChooserCallBack = openFileChooserCallBack;
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mOpenFileChooserCallBack.openFileChooserCallBack(uploadMsg, acceptType);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        return mOpenFileChooserCallBack.openFileChooserCallBackAndroid5(webView, filePathCallback, fileChooserParams);
    }

    public interface OpenFileChooserCallBack {
        // for API - Version below 5.0.
        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);

        // for API - Version above 5.0 (contais 5.0).
        boolean openFileChooserCallBackAndroid5(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                                FileChooserParams fileChooserParams);
    }
}