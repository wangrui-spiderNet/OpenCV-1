Contact: wade.fs@gmail.com
本範例一律採用 Android Studio 最新版編譯，目前是 2.0 preview 4 build 143.2489090
系統採用 Ubuntu 15.04, 其他請見 http://source.android.com/source/initializing.html
OpenCV 有可能因為引用的範例來自 opencv 2.x, 我盡可能採用的是 OpenCV 3.0.0
<P>
https://github.com/tesseract-ocr/tesseract <br />
https://www.youtube.com/watch?v=nmDiZGx5mqU <br />
https://github.com/openalpr/openalpr <br />
<P>
color2gray http://www.cs.northwestern.edu/~ago820/color2gray/color2gray.pdf
<P>
<H1>怎樣在 android studio 中使用 opencv-3.0?</H1>
   http://stackoverflow.com/questions/17767557/how-to-use-opencv-in-android-studio-using-gradle-build-tool/22427267#22427267
<OL>
<LI> 請直接下載 OpenCV-android-sdk
   http://sourceforge.net/projects/opencvlibrary/files/opencv-android/
   http://opencv.org/platforms/android.html
  底下稱其解壓縮根目錄為 opencv

<LI> 將 cp -a opencv/sdk/java $PROJ/libraries/opencv

<LI> vi $PROJ/libraries/opencv/build.gradle
=================== CUT HERE ====================
<PRE>
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'com.android.tools.build:gradle:2.0.0-alpha2'
    }
}

apply plugin: 'com.android.library'

android {
    compileSdkVersion 16
    buildToolsVersion "23.0.2"

    defaultConfig {
        minSdkVersion 16
        targetSdkVersion 22
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
        }
    }
}
</PRE>
=================== CUT HERE ====================

<LI> add following into $PROJ/settings.gradle
include ':libraries:opencv'

<LI> 參考 following into $PROJ/app/build.gradle
=================== CUT HERE ===================
<PRE>
import org.apache.tools.ant.taskdefs.condition.Os
import com.android.build.gradle.tasks.NdkCompile

apply plugin: 'com.android.application'

android {
    compileSdkVersion 22
    buildToolsVersion "22.0.1"

    defaultConfig {
        applicationId "com.derzapp.myfacedetection"
        minSdkVersion 10
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
        ndk {moduleName "NativeCode"}
    }

    sourceSets.main {
        jniLibs.srcDir 'src/main/libs' //set .so files location to libs
        jni.srcDirs = [] //Disable automatic ndk-build call
    }

    tasks.withType(NdkCompile) {
        compileTask -> compileTask.enabled = false
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:22.0.0'
    compile project(':libraries:opencv')
}
</PRE>
=================== CUT HERE ===================

<LI> 將 OpenCV sdk/native/libs/armeabi-v7a 複製到 $PROJ/app/src/main/jniLibs/
</OL>

<H1>怎樣略過 OpenCV Manager APK?</H1>
<OL>
<UL>
<LI> http://superzoro.logdown.com/posts/2015/08/24/opencv-30-for-android-in-android-studio
<LI> http://www.cnblogs.com/tail/p/4618790.html
</UL>
<LI> 註銷掉OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback); 在語句上邊直接設為SUCCESS
<PRE>
public void onResume()
    {
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
</PRE>
<LI> 在Activity類中添加靜態的方法
<PRE>
static{
        if(!OpenCVLoader.initDebug()){
          OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        }
        else {
          // System.loadLibrary("my_jni_lib1");
          // System.loadLibrary("my_jni_lib2");
          mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        System.loadLibrary("opencv_java3"); // System.loadLibrary("opencv_java");
    }

</PRE>
</OL>
