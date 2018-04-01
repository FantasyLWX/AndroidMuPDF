package com.fantasy.androidmupdf;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.artifex.mupdf.viewer.DocumentActivity;

import java.io.File;

/**
 * 使用 MuPDF 1.12.0 预览文件<br>
 * 支持的文件格式有：PDF、EPub、PNG、JPG、BMP、TIFF、GIF、SVG、CBZ、CBR、XPS
 * <pre>
 *     author  : Fantasy
 *     version : 1.0, 2018-04-01
 *     since   : 1.0, 2018-04-01
 * </pre>
 */
public class MainActivity extends AppCompatActivity {
    private String mAppName;
    // 下面是申请权限的请求码
    private static final int PERMISSIONS_REQUEST_CODE_ON_CREATE = 0;
    private static final int PERMISSIONS_REQUEST_CODE_SELECT_FILE = 1;

    private static final int REQUEST_CODE_SELECT_FILE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppName = getResources().getString(R.string.app_name);

        findViewById(R.id.btn_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用系统自带的文件选择器
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            showAlertDialog("检测到您禁止 " + mAppName + " 读写手机存储，这将会导致无法正常" +
                    "读取本地文件。建议您允许读写手机存储。", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CODE_ON_CREATE);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SELECT_FILE:
                if (resultCode == RESULT_OK) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        showAlertDialog("打开文件失败，请允许 " + mAppName + " 读写手机储存。",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                PERMISSIONS_REQUEST_CODE_SELECT_FILE);
                                    }
                                });
                    } else {
                        // TODO 使用 MuPDF 打开文件
                        String path = getPath(MainActivity.this, data.getData());

                        Intent intent = new Intent(MainActivity.this, DocumentActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); // API>=21，launch as a new document
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); // launch as a new document
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.fromFile(new File(path)));
                        //intent.setData(data.getData()); // 会报错
                        //intent.setData(Uri.parse(path)); // 会报错
                        startActivity(intent);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ON_CREATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showLongToast("您已允许 " + mAppName + " 读写手机存储");
                } else {
                    showLongToast("您已拒绝 " + mAppName + " 读写手机存储");
                }
                break;
            case PERMISSIONS_REQUEST_CODE_SELECT_FILE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showLongToast("您已允许 " + mAppName + " 读写手机存储，请重新选择文件");
                } else {
                    showLongToast("您已拒绝 " + mAppName + " 读写手机存储，文件打开失败");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取文件选择器选中的文件绝对路径
     *
     * @param context 上下文
     * @param uri     文件URI
     * @return 文件绝对路径
     */
    private String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                throw e;
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 提示对话框，带有“确定”按钮
     *
     * @param message 提示内容
     * @param listener “确定”按钮的点击监听器
     */
    private void showAlertDialog(String message, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton("确定", listener).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
