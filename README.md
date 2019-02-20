# AndroidMuPDF
AndroidMuPDF 提供一种集成第三方开源库 [MuPDF](https://mupdf.com) 的方案，支持预览PDF、EPub、PNG、JPG、BMP、TIFF、GIF、SVG、CBZ、CBR、XPS文件。

所集成的 MuPDF 的版本是 1.12.0

Demo安装包：<https://github.com/FantasyLWX/AndroidMuPDF/raw/master/other/AndroidMuPDF_V1.0_1_180401_1.apk>

## 效果图

<img src="https://github.com/FantasyLWX/AndroidMuPDF/raw/master/other/screenshot1.png" width="33%" />    <img src="https://github.com/FantasyLWX/AndroidMuPDF/raw/master/other/screenshot2.png" width="33%" />    <img src="https://github.com/FantasyLWX/AndroidMuPDF/raw/master/other/screenshot3.png" width="33%" />

## 用法

* Android Studio

直接导入 mupdf 模块就可以了

* Eclipse

将 mupdf 模块中“java”文件夹中的源码、“jniLibs”文件夹中的so文件、“res”文件夹中的资源文件和AndroidManifest.xml复制到自己的项目工程中。

**温馨提示：** 一般情况下，“jniLibs”文件夹中的so文件，只要用“armeabi”和“armeabi-v7a”这两个就可以了。

## 示例

调用 MuPDF 预览文件的方式：

```Java
import com.artifex.mupdf.viewer.DocumentActivity;

public void startMuPDFActivity(String path) {
    Intent intent = new Intent(MainActivity.this, DocumentActivity.class);
    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); // API>=21，launch as a new document
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    intent.setAction(Intent.ACTION_VIEW);
    intent.setData(Uri.fromFile(new File(path)));
    startActivity(intent);
}
```

## 混淆

```
-dontwarn com.artifex.mupdf.**
-keep class com.artifex.mupdf.** {*;}
```
