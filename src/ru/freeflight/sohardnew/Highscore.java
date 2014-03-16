package ru.freeflight.sohardnew;

import android.content.*;
import android.graphics.*;
import java.io.*;

public class Highscore
	{
		long highscore = 0;
		double doublePos = MySurfaceView.miniWidth;
		int intPos = MySurfaceView.miniWidth;
		double speed = 20;
		int length = 100;

		public Highscore()
		{
			try
			{
				FileInputStream f = MySurfaceView.context.openFileInput("hs.ser");
				if (f != null)
				{

					try
					{
						ObjectInputStream ois = new ObjectInputStream(f);
						highscore = ois.readLong();
						ois.close();
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
				
			}
			catch (FileNotFoundException e)
			{}
		}

		public void setHighscore(long highscore)
		{
			this.highscore = highscore;
		}

		public long getHighscore()
		{
			return highscore;
		}

		public void write()
		{
			try
			{
				FileOutputStream f = MySurfaceView.context.openFileOutput("hs.ser", Context.MODE_PRIVATE);
				try
				{
					ObjectOutputStream oos = new ObjectOutputStream(f);
					oos.writeLong(highscore);
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

		public void move(double deltaTime)
		{
			doublePos = doublePos - deltaTime * speed;
			intPos = (int) Math.round(doublePos);
			if (intPos + length < 0)
			{
				doublePos = MySurfaceView.miniWidth;
				intPos = MySurfaceView.miniWidth;
				
			}
		}

		void Draw(Canvas c, Paint p)
		{
			p.setColor(MySurfaceView.highscoreColor);
			p.setTextSize(11);
			p.setTypeface(Typeface.SANS_SERIF);
			c.drawText("HIGHSCORE:" + highscore, intPos, (int)(MySurfaceView.miniHeight*0.7 + 0.5 * p.getTextSize()), p);
		}

	}
