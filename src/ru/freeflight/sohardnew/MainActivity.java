package ru.freeflight.sohardnew;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import java.io.*;
import android.util.*;
import android.media.*;

public class MainActivity extends Activity
{
	MySurfaceView msv;
	public static String logtag = "sohard";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		Log.d(logtag,"onCreate started");
		if (!isTaskRoot ()) {
			Intent intent = getIntent();
			String action = intent.getAction();
			if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
				Log.d(logtag,"not root task and blabla - finish");
				finish();
				return;
			}
		}

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		msv = new MySurfaceView(this);	
        setContentView(msv);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		Log.d(logtag,"onCreate ended");
    }

	@Override
	protected void onDestroy()
	{
		Log.d(logtag,"onDestroy started");
		Log.d(logtag,"onDestroy ended");
		super.onDestroy();
	}
}
