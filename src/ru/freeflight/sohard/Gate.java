package ru.freeflight.sohard;

import android.graphics.*;
import android.util.*;

public class Gate
{
	Bitmap gateBitmap;
	Canvas gateCanvas;
	int birdRelativePos;
	public double doublePos = MySurfaceView.miniWidth;
	public int pos = MySurfaceView.miniWidth;
	public int length;
	int minLength, maxLength;
	int currentOffset;
	double apmax;
	double ymax;
	double ymin;
	double deviationDivider;
	double dt;
	double[] ypos;
	double[] velocity;
	double velocityMax;
	double[] accelerationMin1;
	double[] accelerationMin2;
	double[] accelerationMin1Time;
	double[] accelerationMin2Time;
	double[] accelerationMinCorrect;
	double[] accelerationMinChosen;
	double[] accelerationMax1;
	double[] accelerationMax2;
	double[] accelerationMax1Time;
	double[] accelerationMax2Time;
	double[] accelerationMaxChosen;
	double[] accelerationNormalDist;
	double[] accelerationChosen;
	int[] topBorder;
	int topMinBorder;
	int[] bottomBorder;
	int bottomMaxBorder;
	int elementNum, horIter;

//	public int up, down;
//	public int up1, up2, up3;
//	public int down1, down2, down3;
//	public int maxUp, minDown;
//	int minGap = 5;
	boolean[] bonusGained;
	boolean scored;
	double gateScore;

	Paint bonusPaint = new Paint();
	Paint gatePaint = new Paint();
	Paint transparentPaint = new Paint();
	
	void initialInit()
	{
		bonusPaint.setColor(MySurfaceView.bonusColor);
		transparentPaint.setColor(Color.TRANSPARENT);
		transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}
	
	void betterInit(int offset)
	{
		//Log.d("FlappyPixel","betterInit...");

		maxLength = MySurfaceView.miniWidth*2;
		minLength = maxLength / 6;
		ypos = new double[maxLength+1];
		velocity = new double[maxLength+1];
		accelerationMin1 = new double[maxLength];
		accelerationMin2 = new double[maxLength];
		accelerationMin1Time = new double[maxLength];
		accelerationMin2Time = new double[maxLength];
		accelerationMinChosen = new double[maxLength];
		accelerationMax1 = new double[maxLength];
		accelerationMax2 = new double[maxLength];
		accelerationMax1Time = new double[maxLength];
		accelerationMax2Time = new double[maxLength];
		accelerationMaxChosen = new double[maxLength];
		accelerationNormalDist = new double[maxLength];
		accelerationChosen = new double[maxLength];
		topBorder = new int[maxLength];
		bottomBorder = new int[maxLength];
		
		bonusGained = new boolean[maxLength+1];
		scored = false;
		gateScore = 0;
		doublePos = MySurfaceView.miniWidth + offset;
		pos = MySurfaceView.miniWidth + offset;
		length = MySurfaceView.rand.nextInt(maxLength - 1) + 1;
		dt = Math.abs(1/MySurfaceView.nextGateSpeed);
		
		apmax = MySurfaceView.birdFlapVelocity*10+MySurfaceView.birdGravity;
		ymax = MySurfaceView.miniHeight - 1.5;
		ymin = 1.5;
		deviationDivider = Math.min(MySurfaceView.birdGravity,Math.abs(apmax))*2;

		//Log.d("FlappyPixel","gate length: " + length);

		ypos[0] = ymin + MySurfaceView.rand.nextDouble()*(ymax-ymin);
		velocityMax = Math.min(Math.sqrt(2*MySurfaceView.birdGravity*(ypos[0]-ymin)),Math.sqrt(2*apmax*(ypos[0]-ymax)));
		velocity[0] = - velocityMax + MySurfaceView.rand.nextDouble()*2*velocityMax;
		//Log.d("FlappyPixel", "ypos[0]: " + ypos[0]);
		for (elementNum = 0; elementNum < length; elementNum++)
		{
			if (elementNum < length-1)
			{
				accelerationMin1[elementNum] = -(Math.sqrt(-8*ymin*MySurfaceView.birdGravity + 8*ypos[elementNum]*MySurfaceView.birdGravity + 4*dt*velocity[elementNum]*MySurfaceView.birdGravity + dt*dt*MySurfaceView.birdGravity*MySurfaceView.birdGravity) + 2*velocity[elementNum] - dt*MySurfaceView.birdGravity)/(2*dt);
				accelerationMin2[elementNum] =  (Math.sqrt(-8*ymin*MySurfaceView.birdGravity + 8*ypos[elementNum]*MySurfaceView.birdGravity + 4*dt*velocity[elementNum]*MySurfaceView.birdGravity + dt*dt*MySurfaceView.birdGravity*MySurfaceView.birdGravity) - 2*velocity[elementNum] + dt*MySurfaceView.birdGravity)/(2*dt);
				accelerationMin1Time[elementNum] =  (Math.sqrt(-8*ymin*MySurfaceView.birdGravity + 8*ypos[elementNum]*MySurfaceView.birdGravity + 4*dt*velocity[elementNum]*MySurfaceView.birdGravity + dt*dt*MySurfaceView.birdGravity*MySurfaceView.birdGravity) - dt*MySurfaceView.birdGravity)/(2*MySurfaceView.birdGravity);
				accelerationMin2Time[elementNum] = -(Math.sqrt(-8*ymin*MySurfaceView.birdGravity + 8*ypos[elementNum]*MySurfaceView.birdGravity + 4*dt*velocity[elementNum]*MySurfaceView.birdGravity + dt*dt*MySurfaceView.birdGravity*MySurfaceView.birdGravity) + dt*MySurfaceView.birdGravity)/(2*MySurfaceView.birdGravity);
				if (accelerationMin1Time[elementNum]>0)
					accelerationMinChosen[elementNum] = (accelerationMin1[elementNum]<apmax) ? apmax : (accelerationMin1[elementNum]>MySurfaceView.birdGravity) ? MySurfaceView.birdGravity : accelerationMin1[elementNum];
				else if (accelerationMin2Time[elementNum]>0)
					accelerationMinChosen[elementNum] = (accelerationMin2[elementNum]<apmax) ? apmax : (accelerationMin2[elementNum]>MySurfaceView.birdGravity) ? MySurfaceView.birdGravity : accelerationMin2[elementNum];
				else
					accelerationMinChosen[elementNum] = MySurfaceView.birdGravity;

				accelerationMax1[elementNum] = -(Math.sqrt(-8*ymax*apmax + 8*ypos[elementNum]*apmax + 4*dt*velocity[elementNum]*apmax + dt*dt*apmax*apmax) + 2*velocity[elementNum] - dt*apmax)/(2*dt);
				accelerationMax2[elementNum] =  (Math.sqrt(-8*ymax*apmax + 8*ypos[elementNum]*apmax + 4*dt*velocity[elementNum]*apmax + dt*dt*apmax*apmax) - 2*velocity[elementNum] + dt*apmax)/(2*dt);
				accelerationMax1Time[elementNum] =  (Math.sqrt(-8*ymax*apmax + 8*ypos[elementNum]*apmax + 4*dt*velocity[elementNum]*apmax + dt*dt*apmax*apmax) - dt*apmax)/(2*apmax);
				accelerationMax2Time[elementNum] = -(Math.sqrt(-8*ymax*apmax + 8*ypos[elementNum]*apmax + 4*dt*velocity[elementNum]*apmax + dt*dt*apmax*apmax) + dt*apmax)/(2*apmax);
				if (accelerationMax1Time[elementNum]>0)
					accelerationMaxChosen[elementNum] = (accelerationMax1[elementNum]<apmax) ? apmax : (accelerationMax1[elementNum]>MySurfaceView.birdGravity) ? MySurfaceView.birdGravity : accelerationMax1[elementNum];
				else if (accelerationMax2Time[elementNum]>0)
					accelerationMaxChosen[elementNum] = (accelerationMax2[elementNum]<apmax) ? apmax : (accelerationMax2[elementNum]>MySurfaceView.birdGravity) ? MySurfaceView.birdGravity : accelerationMax2[elementNum];
				else
					accelerationMaxChosen[elementNum] = apmax;	
				
				accelerationNormalDist[elementNum] = MySurfaceView.rand.nextGaussian()*Math.abs(accelerationMinChosen[elementNum] - accelerationMaxChosen[elementNum])/deviationDivider;
				if (accelerationNormalDist[elementNum] > accelerationMaxChosen[elementNum] | accelerationNormalDist[elementNum] < accelerationMinChosen[elementNum])
					accelerationChosen[elementNum] = accelerationMinChosen[elementNum] + MySurfaceView.rand.nextDouble()*(accelerationMaxChosen[elementNum]-accelerationMinChosen[elementNum]);
				else
					accelerationChosen[elementNum] = accelerationNormalDist[elementNum];
					
				velocity[elementNum+1] = velocity[elementNum]
					+ accelerationChosen[elementNum]*dt;

			}
			ypos[elementNum+1] = ypos[elementNum]
				+ velocity[elementNum]*dt
				+ accelerationChosen[elementNum]*dt*dt/2;

		}

		if (length == 1)
		{
			//topBorder[0] = MySurfaceView.rand.nextInt(Math.max(1,(int)Math.floor(ypos[0])))-3;
			topBorder[0] = Math.max(0,(int)Math.floor(ypos[0]))-1;
			//bottomBorder[0] = (int) Math.floor(ypos[0])+MySurfaceView.rand.nextInt(Math.max(1,MySurfaceView.miniHeight-(int)Math.floor(ypos[0])))+2;
			bottomBorder[0] = (int) Math.floor(ypos[0])+2;
			gateScore = 1;
			//Log.d("FlappyPixel", "topBorder[0]: "+ topBorder[0]);
			//Log.d("FlappyPixel", "bottomBorder[0]: "+ bottomBorder[0]);

		} else 
			for (elementNum = 0; elementNum < length; elementNum++)
			{
				if (elementNum == length-1)
				{
					topBorder[elementNum] = MySurfaceView.rand.nextInt(Math.max(1,(int)Math.floor(Math.min(ypos[elementNum],ypos[elementNum+1]))))-1;
					//topBorder[elementNum] = Math.max(0,(int)Math.floor(Math.min(ypos[elementNum],ypos[elementNum+1])))-1;
					bottomBorder[elementNum] = (int) Math.floor(Math.max(ypos[elementNum],ypos[elementNum+1]))+MySurfaceView.rand.nextInt(Math.max(1,MySurfaceView.miniHeight-(int)Math.floor(Math.max(ypos[elementNum],ypos[elementNum+1]))))+2;
					//bottomBorder[elementNum] = (int) Math.floor(Math.max(ypos[elementNum],ypos[elementNum+1]))+2;
					//topBorder[elementNum] = (int) Math.floor(Math.min(ypos[elementNum],ypos[elementNum+1]))-2;
					//bottomBorder[elementNum] = (int) Math.floor(Math.max(ypos[elementNum],ypos[elementNum+1]))+2;
				} else if (elementNum == length-2)
				{
					topBorder[elementNum] = MySurfaceView.rand.nextInt(Math.max(1,(int)Math.floor(Math.min(Math.min(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]))))-1;
					//topBorder[elementNum] = Math.max(0,(int)Math.floor(Math.min(Math.min(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2])))-1;
					bottomBorder[elementNum] = (int) Math.floor(Math.max(Math.max(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]))+MySurfaceView.rand.nextInt(Math.max(1,MySurfaceView.miniHeight-(int)Math.floor(Math.max(Math.max(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]))))+2;
					//bottomBorder[elementNum] = (int) Math.floor(Math.max(Math.max(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]))+2;
					//topBorder[elementNum] = (int) Math.floor(Math.min(Math.min(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]))-2;
					//bottomBorder[elementNum] = (int) Math.floor(Math.max(Math.max(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]))+2;
				} else
				{
					topBorder[elementNum] = MySurfaceView.rand.nextInt(Math.max(1,(int)Math.floor(Math.min(Math.min(Math.min(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]),ypos[elementNum+3]))))-1;
					//topBorder[elementNum] = Math.max(0,(int)Math.floor(Math.min(Math.min(Math.min(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]),ypos[elementNum+3])))-1;
					bottomBorder[elementNum] = (int) Math.floor(Math.max(Math.max(Math.max(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]),ypos[elementNum+3]))+MySurfaceView.rand.nextInt(Math.max(1,MySurfaceView.miniHeight-(int)Math.floor(Math.max(Math.max(Math.max(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]),ypos[elementNum+3]))))+2;
					//bottomBorder[elementNum] = (int) Math.floor(Math.max(Math.max(Math.max(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]),ypos[elementNum+3]))+2;
					//topBorder[elementNum] = (int) Math.floor(Math.min(Math.min(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]))-2;
					//bottomBorder[elementNum] = (int) Math.floor(Math.max(Math.max(ypos[elementNum],ypos[elementNum+1]),ypos[elementNum+2]))+2;
				}
				
				gateScore = gateScore + (MySurfaceView.miniHeight - bottomBorder[elementNum] + topBorder[elementNum])*1D/MySurfaceView.miniHeight;
				//Log.d("FlappyPixel", "topBorder[" + elementNum + "]: "+ topBorder[elementNum]);
				//Log.d("FlappyPixel", "bottomBorder[" + elementNum + "]: "+ bottomBorder[elementNum]);

			}

			//Log.d("FlappyPixel","gateScore for new gate:"+Math.ceil(gateScore));
		//double playerPos = rand.nextDouble() * (miniHeight - 1.5) + 2.5;
		//double positiveVyMax = Math.sqrt(2 * (-birdFlapVelocity * 10 - birdGravity) * (miniHeight - playerPos - 1.5));
		//double negativeVyMax = Math.sqrt(2 * birdGravity * (playerPos - 2.5));
		//double chosenVy = rand.nextDouble() * positiveVyMax - rand.nextDouble() * negativeVyMax;

		//double currentDeltaT = Math.abs(1 / gateSpeed);
		//double playerNewPos = playerPos + chosenVy * currentDeltaT;

		//int intPlayerNewPos = rand.nextInt(miniHeight-2)+2;
		//int intPlayerNewPos = (int)Math.floor(playerNewPos);

		//up = 0;
		//down = MySurfaceView.miniHeight;
		//int handicap = rand.nextInt(Math.min(3,intPlayerNewPos-1));
		//up = intPlayerNewPos;
		//handicap = rand.nextInt(Math.min(3,miniHeight-intPlayerNewPos-2));
		//down = intPlayerNewPos + 2;
//			for (int i=1; i < length; i++)
//			{
//				for (int vert=0; vert < miniHeight; vert++)
//				{
//					if (vert < up | vert >= down) pixels.add(true);
//					else pixels.add(false);
//				}
//			}

		fillBitmap(MySurfaceView.gateColor);
	}

	public Gate()
	{
		//init(0);
		gateBitmap = Bitmap.createBitmap(MySurfaceView.maxMiniWidth*2+1,MySurfaceView.miniHeight,Bitmap.Config.ARGB_8888);
		gateCanvas = new Canvas(gateBitmap);
		initialInit();
		betterInit(0);
		currentOffset = 0;
	}

	public Gate(int offset)
	{
		//init(offset);
		gateBitmap = Bitmap.createBitmap(MySurfaceView.maxMiniWidth*2+1,MySurfaceView.miniHeight,Bitmap.Config.ARGB_8888);
		gateCanvas = new Canvas(gateBitmap);
		initialInit();
		betterInit(offset);
		currentOffset = offset;
	}

	void fillBitmap(int col)
	{
		drawTunnel(gateCanvas,col);
	}
	
	public void Draw(Canvas c, int col)
	{
		if (col!=MySurfaceView.gateColor)
		{
			fillBitmap(col);
		}
		c.drawBitmap(gateBitmap,pos-1,0,null);
	}
	
	public void debugDraw(Canvas c, int vpos)
	{
		c.drawBitmap(gateBitmap,0,vpos,null);
	}
	
	public boolean getBonus(int birdPos)
	{
		birdRelativePos = MySurfaceView.birdHorPos+1-pos;
		if (birdRelativePos >= 0 & birdRelativePos <= length+1)
		{
			//gateCanvas.drawColor(MySurfaceView.gateColor);
			if (!bonusGained[birdRelativePos] & birdPos == (int)Math.floor(ypos[birdRelativePos]))
				if (gateBitmap.getPixel(birdRelativePos,(int)Math.floor(ypos[birdRelativePos])) == MySurfaceView.bonusColor)
				{
					bonusGained[birdRelativePos] = true;
					gateCanvas.drawRect(birdRelativePos,(int)Math.floor(ypos[birdRelativePos]),birdRelativePos+1,(int)Math.floor(ypos[birdRelativePos])+1,transparentPaint);
					return true;
				}
		}
		return false;
	}
	
	public void drawTunnel(Canvas c, int col)
	{
		gateBitmap.eraseColor(Color.TRANSPARENT);
		bonusPaint.setColor(MySurfaceView.bonusColor);
		gatePaint.setColor(col);
		if (!bonusGained[0])
		c.drawRect(0,(int)Math.floor(ypos[0]),1,(int)Math.floor(ypos[0])+1,bonusPaint);
		for (elementNum = 0; elementNum < length; elementNum++)
		{
			if (topBorder[elementNum] > 0)
				c.drawRect(1+elementNum,0,1+elementNum+1,topBorder[elementNum],gatePaint);
			if (bottomBorder[elementNum] < MySurfaceView.miniHeight)
				c.drawRect(1+elementNum,bottomBorder[elementNum],1+elementNum+1,MySurfaceView.miniHeight,gatePaint);
			if ((elementNum+1)%4==0)
				if (!bonusGained[elementNum+1])
					c.drawRect(1+elementNum,(int)Math.floor(ypos[elementNum+1]),1+elementNum+1,(int)Math.floor(ypos[elementNum+1])+1,bonusPaint);
		}
		//c.drawRect(pos+elementNum,(int)Math.floor(ypos[elementNum+1]),pos+elementNum+1,(int)Math.floor(ypos[elementNum+1])+1,debugPaint);
//			iter = pixels.iterator();
//			int hor = pos, vert = 0;
//			while (iter.hasNext())
//			{
//				if ((Boolean)iter.next())
//					c.drawRect(hor, vert, hor + 1, vert + 1, p);
//				vert++;
//				if (vert > MySurfaceView.miniHeight - 1)
//				{
//					vert = 0;
//					hor++;
//				}
//			}
	}

	public void move(double deltaMove)
	{
		doublePos = doublePos + deltaMove;
		pos = (int) Math.round(doublePos);
		if (pos + length <= 0 & !scored)
		{
			scored = true;
			MySurfaceView.score = MySurfaceView.score + (int)Math.ceil(gateScore);
		}
	}
}
