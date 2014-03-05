package ru.freeflight.sohard;

import android.content.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.util.*;
import android.view.*;
import java.util.*;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{

	public static Random rand = new Random();

	public static Context context;
	MainThread mt;
	Bitmap miniScreen;
	Rect miniRect = new Rect(), screenRect = new Rect();
	Canvas miniCanvas, screenCanvas;
	Paint miniPaint = new Paint();
	float miniZoom;
	Matrix miniMatrix = new Matrix();
	public static int miniWidth;
	int maxMiniWidth;
	public static int miniHeight = 27;
	double birdDoublePos;
	int birdVertPos;
	int birdHorPos;
	double birdVelocity;
	public static double birdGravity;
	public static double birdFlapVelocity;
	double deltaTime, time;
	public static double gateSpeed;
	double maxSpeed;
	boolean maxSpeedReached;
	double initialGateSpeed;
	boolean gameOver, afterGameOver, gameLaunch = true, gameInitialized = false;
	long tick;
	public static long score;
	boolean highScoreSet;
	boolean gameLaunchTextShow = true;
	int backgroundColor = Color.rgb(255, 0, 58);
	int birdColor = Color.BLACK;
	int birdDeadColor = Color.rgb(63, 15, 102);
	int gateColor = Color.BLACK;
	int scoreColor = Color.WHITE;
	int tempInt;
	public static int highscoreColor = Color.rgb(67, 204, 0);

	//Gate gate1;
	//Gate gate2;
	GateList gateList;
	public Highscore hs;
	MediaPlayer player;

	public MySurfaceView(Context context)
	{
		super(context);
		Log.d("FlappyPixel", "MySurfaceView constructor started");
		getHolder().addCallback(this);
		setFocusable(true);	
		this.context = context;
		mt = new MainThread(this);
		Log.d("FlappyPixel", "MySurfaceView constructor ended");
	}

	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		player = MediaPlayer.create(context, R.raw.intro);
        player.start();
		Log.d("FlappyPixel", "surfaceCreated started");
		setMatrix();
		if (mt != null)
		{
			if (mt.getState() == Thread.State.TERMINATED)
			{
				Log.d("FlappyPixel", "mainThread state is 'terminated'. Creating new thread.");
				mt = new MainThread(this);
			}
			Log.d("FlappyPixel", "mainThread starting");
			mt.start();
		}
		else
		{
			Log.d("FlappyPixel", "mainThread is null, nothing to do?");
		}

		Log.d("FlappyPixel", "surfaceCreated ended");
	}

	@Override
	public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
	{
		Log.d("FlappyPixel", "surfaceChanged started");
		setMatrix();
		Log.d("FlappyPixel", "surfaceChanged ended");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder p1)
	{
		if (player != null)
		{
			player.stop();
		}
		Log.d("FlappyPixel", "surfaceDestroyed started");
		mt.stopRunning();
		try
		{
			mt.join();
		}
		catch (InterruptedException e)
		{}
		Log.d("FlappyPixel", "surfaceDestroyed ended");
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);
		miniPaint.setColor(backgroundColor);
		miniCanvas.drawRect(0, 0, miniWidth, miniHeight, miniPaint);
		if (gameLaunch)
		{
			if (tick % 60 == 0) 
			{
				gameLaunchTextShow = !gameLaunchTextShow;
			}

			if (gameLaunchTextShow)
			{
				miniPaint.setColor(scoreColor);
				miniPaint.setTypeface(Typeface.SERIF);
				miniPaint.setTextSize(8);
				miniPaint.setTextAlign(Paint.Align.CENTER);
				miniCanvas.drawText("START", (int)(miniHeight * 0.5F), (int)(miniHeight * 0.25 + 0.5 * miniPaint.getTextSize()), miniPaint);
				miniPaint.setTextAlign(Paint.Align.LEFT);
			}
			hs.Draw(miniCanvas, miniPaint);
		}
		else
		{
			if (!gameOver)
			{
				miniPaint.setColor(scoreColor);
				miniPaint.setTextSize(11);
				miniPaint.setTypeface(Typeface.SANS_SERIF);
				miniPaint.setTextAlign(Paint.Align.RIGHT);
				miniCanvas.drawText(score + "", miniWidth - 1, 9, miniPaint);
				miniPaint.setTextAlign(Paint.Align.LEFT);
				miniPaint.setColor(gateColor);
			}
			else
			{
				miniPaint.setColor(birdDeadColor);
			}
			gateList.Draw(miniCanvas, miniPaint);
			if (!gameOver) miniPaint.setColor(birdColor);
			else
			{
				if (highScoreSet)
					miniPaint.setColor(highscoreColor);
				else
					miniPaint.setColor(scoreColor);
				miniPaint.setTextSize(11);
				miniPaint.setTypeface(Typeface.SANS_SERIF);
				miniPaint.setTextAlign(Paint.Align.RIGHT);
				miniCanvas.drawText(score + "", miniWidth - 1, 9, miniPaint);
				miniPaint.setTextAlign(Paint.Align.LEFT);
				miniPaint.setColor(birdDeadColor);
			}
			miniCanvas.drawRect(1, birdVertPos, 2, birdVertPos + 1, miniPaint);
		}
		canvas.setMatrix(miniMatrix);
		miniRect.right = miniWidth;
		miniRect.bottom = miniHeight;
		screenRect.right = getWidth();
		screenRect.bottom = getHeight();
		canvas.drawBitmap(miniScreen, miniRect, miniRect, null);
		canvas.setMatrix(null);
		if (!gameOver & !gameLaunch)
		{
			miniPaint.setColor(Color.WHITE);
			miniPaint.setAlpha(80);
			canvas.drawRect(miniZoom + 1 + (getWidth() - miniWidth * miniZoom) / 2, (int)(getHeight() / 2 - miniHeight * miniZoom / 2 + birdDoublePos * miniZoom), miniZoom * 2 + (getWidth() - miniWidth * miniZoom) / 2, (int)(getHeight() / 2 - miniHeight * miniZoom / 2 + (birdDoublePos + 1) * miniZoom) - 1, miniPaint);
			miniPaint.setAlpha(255);
			miniPaint.setColor(birdColor);
			miniPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(miniZoom + 1 + (getWidth() - miniWidth * miniZoom) / 2, (int)(getHeight() / 2 - miniHeight * miniZoom / 2 + birdDoublePos * miniZoom), miniZoom * 2 + (getWidth() - miniWidth * miniZoom) / 2, (int)(getHeight() / 2 - miniHeight * miniZoom / 2 + (birdDoublePos + 1) * miniZoom) - 1, miniPaint);
			miniPaint.setStyle(Paint.Style.FILL_AND_STROKE);

		}
		//canvas.drawBitmap(miniScreen, 0, 0, null);



		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (gameLaunch)
		{
			gameLaunch = false;
			time = SystemClock.uptimeMillis() / 1000D;
			player = MediaPlayer.create(context, R.raw.theme);
			player.setLooping(true);
			player.start();
		}
		else if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			synchronized (this)
			{
				birdVelocity = birdVelocity + birdFlapVelocity;
			}
			if (afterGameOver)
			{
				mt.stopRunning();
				try
				{
					mt.join();
				}
				catch (InterruptedException e)
				{}
				init();
				player = MediaPlayer.create(context, R.raw.theme);
				player.setLooping(true);
				player.start();
				mt = new MainThread(this);
				mt.start();
			}
		}
		return true;
	}

	void initialInit()
	{
		if (getWidth() > 0)
		{
			maxMiniWidth = getWidth() / 4;
			maxSpeed = maxMiniWidth / 3;
			miniScreen = Bitmap.createBitmap(maxMiniWidth, miniHeight, Bitmap.Config.RGB_565);
			miniCanvas = new Canvas(miniScreen);
			miniPaint.setTypeface(Typeface.SANS_SERIF);
			miniPaint.setTextSize(12);
			hs = new Highscore();
			init();
			gameInitialized = true;
		}
	}

	void init()
	{
		backgroundColor = Color.rgb(255, 0, 58);
		birdColor = Color.BLACK;
		birdDeadColor = Color.rgb(63, 15, 102);
		gateColor = Color.BLACK;
		scoreColor = Color.WHITE;
		highscoreColor = Color.rgb(67, 204, 0);
		miniWidth = 27;
		initialGateSpeed = -miniWidth / 10;
		gateSpeed = initialGateSpeed;
		birdDoublePos = miniHeight / 2;
		birdVertPos = miniHeight / 2;
		birdVelocity = 0;
		birdGravity = miniHeight / 2;
		birdFlapVelocity = -miniHeight / 6 ;
		deltaTime = 0;
		time = SystemClock.uptimeMillis() / 1000D;
		gameOver = false;
		afterGameOver = false;
		maxSpeedReached = false;
		score = 0;
		tick = 0;
		highScoreSet = false;
		gateList = new GateList();
		gateList.addGate();
		gateList.addGate();
		setMatrix();
	}

	boolean collision()
	{
		if (
			miniScreen.getPixel(birdHorPos, birdVertPos - 1) == gateColor |
			miniScreen.getPixel(birdHorPos + 1, birdVertPos - 1) == gateColor |
			miniScreen.getPixel(birdHorPos + 2, birdVertPos - 1) == gateColor |
			miniScreen.getPixel(birdHorPos, birdVertPos) == gateColor |
			miniScreen.getPixel(birdHorPos + 2, birdVertPos) == gateColor |
			miniScreen.getPixel(birdHorPos, birdVertPos + 1) == gateColor |
			miniScreen.getPixel(birdHorPos + 1, birdVertPos + 1) == gateColor |
			miniScreen.getPixel(birdHorPos + 2, birdVertPos + 1) == gateColor
			)
		{
			return true;
		}
		return false;
	}

	void setMatrix()
	{
		miniZoom = Math.min(getWidth() * 1F / miniWidth, getHeight() * 1F / miniHeight);
		miniMatrix.reset();
		miniMatrix.setScale(miniZoom, miniZoom);
		miniMatrix.postTranslate((getWidth() - miniZoom * miniWidth) * 0.5F, (getHeight() - miniZoom * miniHeight) * 0.5F);
	}

	void gameOver()
	{
		gameOver = true;
		if (player != null)
		{
			player.setLooping(false);
			player.stop();
		}
		player = MediaPlayer.create(context, R.raw.hit);
		player.start();
		if (hs.getHighscore() < score)
		{
			hs.setHighscore(score);
			highScoreSet = true;
		}
	}

	class MainThread extends Thread
	{

		MySurfaceView msv;

		boolean running = true;

		public MainThread(MySurfaceView msv)
		{
			this.msv = msv;
		}
		@Override
		public void run()
		{
			while (!gameInitialized)
			{
				initialInit();
				try
				{
					sleep(50);
				}
				catch (InterruptedException e)
				{}
			}	
			
			while (running)
			{
				tick++;
				deltaTime = SystemClock.uptimeMillis() / 1000D - time;
				time = SystemClock.uptimeMillis() / 1000D;
				if (!gameLaunch)
				{
					if (!maxSpeedReached & Math.abs(gateSpeed) >= maxSpeed)
					{
						maxSpeedReached = true;
						tempInt = backgroundColor;
						backgroundColor = highscoreColor;
						highscoreColor = tempInt;
					}
					if (tick % 60 == 0 & !maxSpeedReached)
					{
						gateSpeed = gateSpeed - 0.05D;
						miniWidth = (int)Math.floor((0.9 + 0.1 * gateSpeed / initialGateSpeed) * miniHeight);
						setMatrix();
					}

					if (collision())
					{
						gameOver();
					}
					if (birdVertPos < miniHeight - 1 & birdVertPos > 0 & !gameOver)
					{
						birdVelocity = birdVelocity + birdGravity * deltaTime;
						birdDoublePos = birdDoublePos + birdVelocity * deltaTime;
						if (Math.abs(birdVertPos - birdDoublePos) > 1)
							birdVertPos = (birdDoublePos > birdVertPos) ? (int) Math.floor(birdDoublePos) : (int) Math.floor(birdDoublePos) + 1;
						gateList.move(gateSpeed * deltaTime);	
					}

					if (birdVertPos < 1)
					{
						birdVertPos = 0;
						gameOver();
					}
					else if (birdVertPos >= miniHeight - 1)
					{
						birdVertPos = miniHeight - 1;
						gameOver();
					}
				}
				else
				{
					hs.move(deltaTime);
				}

				screenCanvas = msv.getHolder().lockCanvas();
				if (screenCanvas != null)
				{
					msv.onDraw(screenCanvas);
					msv.getHolder().unlockCanvasAndPost(screenCanvas);
				}
				if (!msv.gameOver)
				{

					if (SystemClock.uptimeMillis() - (time - deltaTime) * 1000D < 15)
						try
						{
							sleep(15 - (long)(SystemClock.uptimeMillis() - (time - deltaTime) * 1000D));
						}
						catch (InterruptedException e)
						{}
				}
				else
				{
					running = false;
					try
					{
						sleep(500);
					}
					catch (InterruptedException e)
					{}
					afterGameOver = true;
				}
			}
		}

		public void stopRunning()
		{
			running = false;
		}
	}
}
