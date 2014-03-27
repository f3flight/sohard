package ru.freeflight.sohardnew;

import android.graphics.*;
import android.media.*;
import java.util.*;
import ru.freeflight.sohardnew.*;

public class GateList extends ArrayList
{
	MySurfaceView msv;
	int maxOffset = 0;
	int gateOffset = 0;
	Iterator iter;
	Gate currentGate;
	Paint debugPaint = new Paint();
	MediaPlayer hiplayer,loplayer;
	boolean isHi = false;
	
	public GateList(MySurfaceView msv)
	{
		super();
		this.msv = msv;
		debugPaint.setColor(Color.RED);
		hiplayer = MediaPlayer.create(MainActivity.getContext(), R.raw.hi);
		loplayer = MediaPlayer.create(MainActivity.getContext(), R.raw.lo);
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
			if (currentGate.getBonus(msv.birdVertPos))
			{
				msv.score = msv.score + 1;
				if (isHi)
					hiplayer.start();
				else
					loplayer.start();
				isHi = !isHi;
			}
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
