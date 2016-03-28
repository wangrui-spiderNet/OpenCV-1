Contact: wade.fs@gmail.com <BR/>
本範例一律採用 Android Studio 最新版編譯，目前是 2.1 preview3, build 143.2682553 <BR/>
系統採用 Ubuntu 15.10, 其他請見 http://source.android.com/source/initializing.html <BR/>
OpenCV 有可能因為引用的範例來自 opencv 2.x, 我盡可能採用的是 OpenCV 3.0.0 <BR/>
<P>
<UL>
<LI><a href="https://github.com/tesseract-ocr/tesseract">tesseract</a> <br />
<LI><A href="https://www.youtube.com/watch?v=nmDiZGx5mqU">教學@Youtube</a> <br />
<LI><A href="https://github.com/openalpr/openalpr">Open Alpr</a> <br />
<LI><A href="http://www.cs.northwestern.edu/~ago820/color2gray/color2gray.pdf">color2gray</a>
<LI><A href="http://docs.opencv.org/3.1.0/db/d28/tutorial_cascade_classifier.html">OpenCV-3.1.0 Face Detection</a>
</UL>
<P>
<H1>怎樣在 android studio 中使用 opencv-3.0?</H1><BR/>
   <A href="http://stackoverflow.com/questions/17767557/how-to-use-opencv-in-android-studio-using-gradle-build-tool/22427267#22427267">
怎樣在 android studio 中發展OpenCV 應用</a>
<OL>
<LI> 請直接下載 OpenCV-android-sdk<BR/>
<UL>
   <LI>http://sourceforge.net/projects/opencvlibrary/files/opencv-android/<BR/>
   <LI>http://opencv.org/platforms/android.html<BR/>
</UL>
  底下稱其解壓縮根目錄為 opencv<BR/>

<LI> 將前面下載的 OpenCV java code 複製到專案中:<Br/>
 $ mkdir $PROJ/libraries<br/>
 $ cp -a opencv/sdk/java $PROJ/libraries/opencv

<LI> vi $PROJ/libraries/opencv/build.gradle
=================== CUT HERE ====================
<PRE>
apply plugin: 'com.android.library'

android {
    compileSdkVersion 14
    buildToolsVersion "23.0.2"

    defaultConfig {
        minSdkVersion 14
        targetSdkVersion 23
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

<LI> add following into $PROJ/settings.gradle<BR/>
include ':libraries:opencv'<BR/>
PS: 這一步是相對應上面複製 opencv 原碼放的路徑，當然專案的源碼直接放 $PROJ/ 下
<LI> 修改 $PROJ/app/build.gradle，這個當是最最重要的一個<BR/>
=================== CUT HERE ===================<BR/>
<PRE>
import com.android.build.gradle.tasks.NdkCompile
apply plugin: 'com.android.application'

// If you want compile "jni", please remove comment below
// 如果程式碼需要用到 JNI 的話，例如 OpenCV 範例程式中就有用到，那麼
// 就將底下的註解都拿掉
android {
    compileSdkVersion 14
    buildToolsVersion "23.0.2"

    defaultConfig {
        applicationId "com.wade.myfacedetection"
        minSdkVersion 14
        targetSdkVersion 22
        versionCode 120
        versionName "1.2.0"
//        ndk {moduleName "detection_based_tracker"}
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
/*
    sourceSets.main {
        jniLibs.srcDir 'src/main/libs' //set .so files location to libs
        jni.srcDirs = [] //Disable automatic ndk-build call
    }
    task ndkBuild(type: Exec) {
        def ndkDir = android.ndkDirectory
        commandLine "$ndkDir/ndk-build",
                'NDK_PROJECT_PATH=build',
                'APP_BUILD_SCRIPT=src/main/jni/Android.mk',
                'NDK_APPLICATION_MK=src/main/jni/Application.mk',
                'NDK_APP_LIBS_OUT = src/main/libs'
    }
    task cleanNative(type: Exec, description: 'Clean JNI object files') {
        def ndkDir = android.ndkDirectory
        commandLine "$ndkDir/ndk-build",
                '-C', file('src/main/jni').absolutePath, // Change src/main/jni the relative path to your jni source
                'clean'
    }
    clean.dependsOn 'cleanNative'
    tasks.withType(JavaCompile) {
        compileTask -> compileTask.dependsOn ndkBuild
    }
*/
}

dependencies {
    compile fileTree(dir: 'libs')
    compile project(':libraries:opencv')
}
</PRE><BR/>
=================== CUT HERE ===================<BR/>
本範例已經將原本 OpenCV sample code 中的 Native JNI 拿掉所以不需要 JNI,<Br/>
 需要的話請按照說明將上面的註解拿掉。<BR/>
如果要編譯 Native JNI 的話就需要再進行下面的步驟:<Br/>
<LI> 將 OpenCV sdk/native/libs/armeabi-v7a 複製到 $PROJ/app/src/main/jniLibs/<BR/>
</OL>

<H1>怎樣略過 OpenCV Manager APK?</H1>
<OL>
<UL>
<LI> <A href="http://superzoro.logdown.com/posts/2015/08/24/opencv-30-for-android-in-android-studio">openCV 3.0 for android studio</a>
<LI> <A href="http://www.cnblogs.com/tail/p/4618790.html">怎麼略過 OpenCV Manager</a>
</UL>
<LI> 註銷掉OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback); 在語句上邊直接設為SUCCESS<Br/>
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
