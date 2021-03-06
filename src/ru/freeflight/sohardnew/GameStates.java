package ru.freeflight.sohardnew;

import android.content.*;
import java.io.*;

public enum GameStates
{
	start,
	startPaused,
	settings,
	on,
	paused,
	over,
	overStage2;
	
	public static GameStates loadGameState()
	{
		GameStates gameState = GameStates.start;
		try
		{
			FileInputStream f = MainActivity.getContext().openFileInput("gamestate.ser");
			if (f != null)
			{
				try
				{
					ObjectInputStream ois = new ObjectInputStream(f);
					try
					{
						gameState = (GameStates) ois.readObject();
					}
					catch (ClassNotFoundException e) {}
					catch (IOException e) {}
					ois.close();
				}
				catch (IOException e)
				{
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
					
		if (gameState == GameStates.paused)
			gameState = GameStates.start;
		
		return gameState;
	}
	
	public static void saveGameState(GameStates gameState)
	{
		try
		{
			FileOutputStream f = MainActivity.getContext().openFileOutput("gamestate.ser", Context.MODE_PRIVATE);
			try
			{
				ObjectOutputStream oos = new ObjectOutputStream(f);
				oos.writeObject(gameState);
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
