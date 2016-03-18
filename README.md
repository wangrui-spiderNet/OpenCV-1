Contact: wade.fs@gmail.com <BR/>
本範例一律採用 Android Studio 最新版編譯，目前是 2.0 preview 4 build 143.2489090 <BR/>
系統採用 Ubuntu 15.04, 其他請見 http://source.android.com/source/initializing.html <BR/>
OpenCV 有可能因為引用的範例來自 opencv 2.x, 我盡可能採用的是 OpenCV 3.0.0 <BR/>
<P>
<UL>
<LI><a href="https://github.com/tesseract-ocr/tesseract">tesseract</a> <br />
<LI><A href="https://www.youtube.com/watch?v=nmDiZGx5mqU">教學@Youtube</a> <br />
<LI><A href="https://github.com/openalpr/openalpr">Open Alpr</a> <br />
<LI><A href="http://www.cs.northwestern.edu/~ago820/color2gray/color2gray.pdf">color2gray</a>
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

<LI> 將 cp -a opencv/sdk/java $PROJ/libraries/opencv

<LI> vi $PROJ/libraries/opencv/build.gradle
=================== CUT HERE ====================
<PRE>
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'com.android.tools.build:gradle:2.1.0-alpha3'
    }
}

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
PS: 這一步是相對應上面將 opencv 的原碼放的路徑，opencv sample project 都是直接放 $PROJ/ 下的
<LI> 修改 $PROJ/app/build.gradle，這個當是最最重要的一個<BR/>
=================== CUT HERE ===================<BR/>
<PRE>
import org.apache.tools.ant.taskdefs.condition.Os
import com.android.build.gradle.tasks.NdkCompile

apply plugin: 'com.android.application'

android {
    compileSdkVersion 14
    buildToolsVersion "23.0.2"

    defaultConfig {
        applicationId "com.wade.myfacedetection" // 主要是要搭配自己的 package name 
        minSdkVersion 14
        targetSdkVersion 22
        versionCode 111
        versionName "1.1.2"
        ndk {moduleName "detection_based_tracker"} // 這邊要搭配 jni/Android.mk 中的 LOCAL_MODULE, 但是實情可能只是標註而已
    }

    sourceSets.main {
        jniLibs.srcDir 'src/main/libs' // 會把編譯出來的 JNI 整合進 apk 中
        jni.srcDirs = [] // 抑制自動呼叫 ndk-build, 也就是只編譯我們要的路徑即可
    }

    buildTypes { // 這邊是標準寫法，我沒改
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    task ndkBuild(type: Exec) {
        def ndkDir = android.ndkDirectory	// 取得 ndk-build 的目錄，請先安裝好, 在 local.properties 中設定 ndk.dir
        commandLine "$ndkDir/ndk-build",	// 執行 ndk-build, 底下是參數	
                'NDK_PROJECT_PATH=build',
                'APP_BUILD_SCRIPT=src/main/jni/Android.mk',	// 會呼叫 jni/Android.mk, 請注意這個檔要能夠手動下 ndk-build 成功
                'NDK_APPLICATION_MK=src/main/jni/Application.mk',
                'NDK_APP_LIBS_OUT = src/main/libs'	// 會把編譯出來的 JNI 放在此目錄
    }
    tasks.withType(JavaCompile) {
        compileTask -> compileTask.dependsOn ndkBuild	// 呼叫 task ndkBuild 
    }
}
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:22.0.0'
    compile project(':libraries:opencv')
}
</PRE><BR/>
=================== CUT HERE ===================<BR/>

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
