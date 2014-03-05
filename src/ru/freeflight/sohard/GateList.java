package ru.freeflight.sohard;

import android.graphics.*;
import android.util.*;
import java.util.*;

public class GateList extends ArrayList
{
	int maxOffset = 0;
	int gateOffset = 0;
	Iterator iter;
	Gate currentGate;
	public GateList()
	{
		super();
	}

	public void addGate()
	{
		updateGateOffset();
		currentGate = new Gate(maxOffset);
		maxOffset += currentGate.length+gateOffset;
		//Log.d("FlappyPixel","maxOffset changed to:"+maxOffset);
		super.add(currentGate);
	}

	public void move(double deltaMove)
	{
		//Log.d("FlappyPixel","move started");
		updateGateOffset();
		maxOffset = 0;
		iter = iterator();
		while (iter.hasNext())
		{
			currentGate = (Gate) iter.next();
			maxOffset = Math.max(maxOffset, currentGate.pos + currentGate.length + gateOffset - MySurfaceView.miniWidth);
		}
		//Log.d("FlappyPixel","move first cycle ended");
		iter = iterator();
		while (iter.hasNext())
		{
			//Log.d("FlappyPixel","move second cycle...");
			currentGate = (Gate) iter.next();
			currentGate.move(deltaMove);
			if (currentGate.pos + currentGate.length < 0)
			{
				//Log.d("FlappyPixel"," ");
				//Log.d("FlappyPixel","Move gate back to right. maxOffset is:"+maxOffset);
				currentGate.doublePos = MySurfaceView.miniWidth+maxOffset;
				currentGate.pos = MySurfaceView.miniWidth+maxOffset;
				currentGate.betterInit(maxOffset);
				//Log.d("FlappyPixel","New gate position is:"+currentGate.pos);
				maxOffset = maxOffset + currentGate.length + gateOffset;
			}
		}
	}

	public void Draw(Canvas c, Paint p)
	{
		iter = iterator();
		while (iter.hasNext())
		{
			currentGate = (Gate) iter.next();
			currentGate.Draw(c,p);
		}
	}
	
	void updateGateOffset()
	{
		//gateOffset = 1;
		gateOffset = (int)(Math.abs(MySurfaceView.gateSpeed)*2);
	}

}
