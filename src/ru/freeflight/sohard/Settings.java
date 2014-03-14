package ru.freeflight.sohard;

import android.content.*;
import java.io.*;

public class Settings
{
	
	boolean mute, hard;
	
	public Settings()
	{
		try
		{
			FileInputStream f = MySurfaceView.context.openFileInput("settings.ser");
			if (f != null)
			{
				try
				{
					ObjectInputStream ois = new ObjectInputStream(f);
					readSettings(ois);
					ois.close();
				}
				catch (IOException e)
				{
					readSettings(null);
				}
				try
				{
					f.close();
				}
				catch (IOException e)
				{}
			}

		}
		catch (FileNotFoundException e)
		{}
	}

	public void setMute(boolean mute)
	{
		this.mute = mute;
	}

	public boolean isMute()
	{
		return mute;
	}

	public void setHard(boolean hard)
	{
		this.hard = hard;
	}

	public boolean isHard()
	{
		return hard;
	}
	
	void readSettings(ObjectInputStream ois)
	{
		if (ois!=null)
		{
		try
		{
			mute = ois.readBoolean();
			hard = ois.readBoolean();
		}
		catch (IOException e)
		{
			mute = false;
			hard = false;
		}
		}
		else
		{
			mute = false;
			hard = false;
		}

	}
	
	void writeSettings()
	{
		try
		{
			FileOutputStream f = MySurfaceView.context.openFileOutput("settings.ser", Context.MODE_PRIVATE);
			try
			{
				ObjectOutputStream oos = new ObjectOutputStream(f);
				oos.writeBoolean(mute);
				oos.writeBoolean(hard);
				oos.close();
			}
			catch (IOException e)
			{}
			try
			{
				f.close();
			}
			catch (IOException e)
			{}
		}
		catch (FileNotFoundException e)
		{}
	}
}
