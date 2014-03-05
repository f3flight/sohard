package ru.freeflight.sohard;

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
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		Log.d("FlappyPixel","onCreate started");
		if (!isTaskRoot ()) {
			Intent intent = getIntent();
			String action = intent.getAction();
			if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
				Log.d("FlappyPixel","not root task and blabla - finish");
				finish();
				return;
			}
		}

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		msv = new MySurfaceView(this);	
        setContentView(msv);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		Log.d("FlappyPixel","onCreate ended");
    }

	@Override
	protected void onDestroy()
	{
		Log.d("FlappyPixel","onDestroy started");
		msv.hs.write();
		Log.d("FlappyPixel","onDestroy ended");
		super.onDestroy();
	}
}
