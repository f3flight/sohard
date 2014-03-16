package ru.freeflight.sohard;

import android.graphics.*;
import android.util.*;
import java.util.*;

public class GateList extends ArrayList
{
	MySurfaceView msv;
	int maxOffset = 0;
	int gateOffset = 0;
	Iterator iter;
	Gate currentGate;
	Paint debugPaint = new Paint();
	
	public GateList(MySurfaceView msv)
	{
		super();
		this.msv = msv;
		debugPaint.setColor(Color.RED);
	}

	public void addGate()
	{
		updateGateOffset();
		currentGate = new Gate(maxOffset);
		maxOffset += currentGate.length+gateOffset;
		MySurfaceView.nextGateSpeed = MySurfaceView.gateSpeed - 0.05D*currentGate.length/2;
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
				msv.speedUp(currentGate.length);
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

	public void Draw(Canvas c, int col)
	{
		iter = iterator();
		while (iter.hasNext())
		{
			currentGate = (Gate) iter.next();
			currentGate.Draw(c,col);
		}
	}
	
	public void debugDraw(Canvas c)
	{
		iter = iterator();
		int vpos = 0;
		while (iter.hasNext())
		{
			currentGate = (Gate) iter.next();
			c.drawRect(0,vpos,MySurfaceView.maxMiniWidth*2+1,vpos+MySurfaceView.miniHeight,debugPaint);
			currentGate.debugDraw(c,vpos);
			vpos = vpos + MySurfaceView.miniHeight+1;
		}
	}
	
	void updateGateOffset()
	{
		//gateOffset = 1;
		gateOffset = (int)(Math.abs(MySurfaceView.nextGateSpeed)*2);
	}

}
