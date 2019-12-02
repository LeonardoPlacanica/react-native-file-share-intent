
package com.ajithab;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class RNFileShareIntentModule extends ReactContextBaseJavaModule {

  private Callback successShareCallback;
  private final ReactApplicationContext reactContext;

  public RNFileShareIntentModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }


  public void onNewIntent(Intent intent) {
    Activity mActivity = getCurrentActivity();

    if(mActivity == null) { return; }

    mActivity.setIntent(intent);
  }

  @ReactMethod
  public void getFilePath(Callback successCallback) {
    Activity mActivity = getCurrentActivity();

    if(mActivity == null) { return; }

    Intent intent = mActivity.getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    FileHelper fileHelper = new FileHelper(this.reactContext);

    WritableArray res = new WritableNativeArray();
    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if (type.startsWith("text")) {
        String input = intent.getStringExtra(Intent.EXTRA_TEXT);
        successCallback.invoke(input, type);
      } else if (type.startsWith("application/") || type.startsWith("audio/") || type.startsWith("image/") || type.startsWith("message/") ||
          type.startsWith("video/") || type.startsWith("x-world/")) {
        Uri fileUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (fileUri != null) {
          res.pushMap(fileHelper.getFileData(fileUri));
          successCallback.invoke(res);
        }
      }
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
      if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("audio/") || type.startsWith("application/")) {
        ArrayList<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileUris != null) {
          for (Uri uri : fileUris) {
            res.pushMap(fileHelper.getFileData(uri));
          }
          successCallback.invoke(res);
        }
      }
    }
  }

  @ReactMethod
  public void clearFilePath() {
    Activity mActivity = getCurrentActivity();

    if(mActivity == null) { return; }

    Intent intent = mActivity.getIntent();
    String type = intent.getType();
    if (type == null) { return; }

    if (type.startsWith("text/") || type.startsWith("x-world/")) {
      intent.removeExtra(Intent.EXTRA_TEXT);
    } else if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("application/") ||
            type.startsWith("message/") || type.startsWith("video/")) {
      intent.removeExtra(Intent.EXTRA_STREAM);
    }
  }
  @Override
  public String getName() {
    return "RNFileShareIntent";
  }
}
