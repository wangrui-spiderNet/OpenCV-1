package com.wade.myfacedetection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {
    private static final String    TAG                     = "FaceDetect";
    public static Activity  mMainActivity;
    public static int startMode = 0;
    private MenuItem    mItemStart; // default
    private MenuItem    mItemLbFace;
    private MenuItem    mItemProfileFace;
    private MenuItem    mItemAltTree;
    private MenuItem    mItemAlt2;
    private MenuItem    mItemAlt;
    private MenuItem    mItemExtended;
    private MenuItem    mItemFrontCatFace;
    private MenuItem    mItemUpperBody;
    private MenuItem    mItemLowerBody;
    private MenuItem    mItemEye;
    private MenuItem    mItemFaceDefault;
    private MenuItem    mItemEyeGlasses;
    private MenuItem    mItemLeftEye;
    private MenuItem    mItemRightEye;
    private MenuItem    mItemFullBody;
    private MenuItem    mItemRussian;
    private MenuItem    mItemSmile;
    private MenuItem    mItemPlate;

    public int getStartMode() { return startMode; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mItemStart = menu.add(R.string.start);
        mItemLbFace = menu.add(R.string.lbFace);
        mItemProfileFace = menu.add(R.string.ProfileFace);
        mItemExtended = menu.add(R.string.Extended);
        mItemFrontCatFace = menu.add(R.string.FrontCatFace);
        mItemAlt2 = menu.add(R.string.Alt2);
        mItemAltTree = menu.add(R.string.AltTree);
        mItemAlt = menu.add(R.string.Alt);
        mItemFaceDefault = menu.add(R.string.FaceDefault);
        mItemLowerBody = menu.add(R.string.LowerBody);
        mItemUpperBody = menu.add(R.string.UpperBody);
        mItemFullBody = menu.add(R.string.FullBody);
        mItemEyeGlasses = menu.add(R.string.EyeGlasses);
        mItemEye = menu.add(R.string.Eye);
        mItemLeftEye = menu.add(R.string.LeftEye);
        mItemRightEye = menu.add(R.string.RightEye);
        mItemPlate = menu.add(R.string.Plate);
        mItemRussian = menu.add(R.string.Russian);
        mItemSmile = menu.add(R.string.Smile);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == mItemStart)             { startMode = 0; }
        else if (item == mItemLbFace)      { startMode = 0; }
        else if (item == mItemProfileFace) { startMode = 1; }
        else if (item == mItemExtended)    { startMode = 2; }
        else if (item == mItemFrontCatFace){ startMode = 3; }
        else if (item == mItemAlt2)         { startMode = 4; }
        else if (item == mItemAltTree)      { startMode = 5; }
        else if (item == mItemAlt)          { startMode = 6; }
        else if (item == mItemFaceDefault) { startMode = 7; }
        else if (item == mItemLowerBody)    { startMode = 8; }
        else if (item == mItemUpperBody)    { startMode = 9; }
        else if (item == mItemFullBody)     { startMode = 10; }
        else if (item == mItemEyeGlasses)   { startMode = 11; }
        else if (item == mItemEye)          { startMode = 12; }
        else if (item == mItemLeftEye)      { startMode = 13; }
        else if (item == mItemRightEye)     { startMode = 14; }
        else if (item == mItemPlate)        { startMode = 15; }
        else if (item == mItemRussian)      { startMode = 16; }
        else if (item == mItemSmile)        { startMode = 17; }
        Log.d(TAG, "Options selected "+startMode + ": "+item.toString());
        Intent intent = new Intent(MainActivity.this, FdActivity.class);
        startActivity(intent);
        return true;
    }
}
