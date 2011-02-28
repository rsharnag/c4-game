package org.example.connectfour;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AnimationUtils;

public class gameView extends SurfaceView implements SurfaceHolder.Callback  {
	

	private static final String TAG = "PuzzleView";
	class GameThread extends Thread
	{
		/*
         * Difficulty setting constants
         */
        public static final int DIFFICULTY_LEVEL_1 = 3;
        public static final int DIFFICULTY_LEVEL_2 = 4;
        public static final int DIFFICULTY_LEVEL_3 = 5;
        public static final int DIFFICULTY_LEVEL_4 = 6;
        
        /*
         * State-tracking constants
         */
        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;
        
        private long mLastTime;
        private int mMode;
        private Bitmap mBackgroundImage;
        private int mCanvasHeight = 1;
        private int mCanvasWidth = 1;
        private SurfaceHolder mSurfaceHolder;
        private Handler mHandler;
        private Context mContext;
        private Drawable mBallImage;
        private int mBallHeight;
        private int mBallWidth;
        private int mDifficulty;
        private boolean mRun;
        private int mCellSpace;
        private int mGridTop;
        public GameThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;
            
            Resources res = context.getResources();
         // cache handles to our key sprites & other drawables
            mBallImage = context.getResources().getDrawable(
                    R.drawable.ball);
            //Set Cell space
            
            mBackgroundImage=BitmapFactory.decodeResource(res, R.drawable.grid);
            mBallWidth=100;
            mBallHeight=100;
            mDifficulty=DIFFICULTY_LEVEL_1;

            
        }
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
            }
        }
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
            }
        }
        @Override
        public void run() {
        	
			
			
            while (mRun) {
            	try {
    				Thread.sleep(30);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mMode == STATE_RUNNING)
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
                if (map != null) {
                }
            }
            return map;
        }
        public void setDifficulty(int difficulty) {
            synchronized (mSurfaceHolder) {
                mDifficulty = difficulty;
            }
        }
        public void setRunning(boolean b) {
            mRun = b;
            setState(GameThread.STATE_RUNNING);
        }
    public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }
        public void setState(int mode, CharSequence message) {
        	synchronized (mSurfaceHolder) {
                mMode = mode;
        	}
        }
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
                int Bgheight=(int) (height*0.8);
                // don't forget to resize the background image
                mBackgroundImage = Bitmap.createScaledBitmap(
                       mBackgroundImage, width, Bgheight, true);
                mCellSpace=(int)(mCanvasWidth/7);
            	mGridTop=(int)(mCanvasHeight*0.2);
            }
        }
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
            }
            setState(STATE_RUNNING);
        }
        boolean doKeyDown(int keyCode, KeyEvent msg) {
            synchronized (mSurfaceHolder) {
            }
            return false;
        }
        boolean doKeyUp(int keyCode, KeyEvent msg) {
            boolean handled = false;

            synchronized (mSurfaceHolder) {
            	
            }
            return handled;
        }
        private void doDraw(Canvas canvas) {
        	//canvas.drawBitmap(mBackgroundImage, 0,(int)(mCanvasHeight*0.2), null);

        	Paint CLine=new Paint();
        	CLine.setARGB(255, 0, 0, 0);
        	Paint CBackground=new Paint();
        	CBackground.setARGB(200,100,100,100);
        	canvas.drawRect(0,0,getWidth(),getHeight(),CBackground);
        	
        	for(int start=2;start<mCanvasWidth;start=start+mCellSpace)
        		canvas.drawLine(start, 0, start, mCanvasHeight-2, CLine);
        	canvas.drawLine(2,mCanvasHeight-2,mCanvasWidth-2,mCanvasHeight-2,CLine);

        }
	}
    private GameThread thread;
    private static int mDifficulty; 
	public GameThread getThread() {
        return thread;
    }
	public gameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mDifficulty = Integer.parseInt(prefs.getLevel(context));
        thread = new GameThread(holder, context, new Handler() );
        setFocusable(true);
	}
	 @Override
	 public void onWindowFocusChanged(boolean hasWindowFocus) {
	        if (!hasWindowFocus) thread.pause();
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();

		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		return false;
	}
	
}
/*
private  final Game game;
public gameView(Context context) {
	super(context);
	this.game=(Game)context;
	setFocusable(true);
	setFocusableInTouchMode(true);
	// 
}
private float width; // width of one column
private float height; // height of one ball
private int selX; // X index of selection
private int selY; // Y index of selection
private final Rect selRect = new Rect();
@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh) {
width = w / 9f;
height = h / 9f;
getRect(selX, selY, selRect);
Log.d(TAG, "onSizeChanged: width " + width + ", height "
+ height);
super.onSizeChanged(w, h, oldw, oldh);
}
private void getRect(int x, int y, Rect rect) {
rect.set((int) (x * width), (int) (y * height), (int) (x
* width + width), (int) (y * height + height));
}
@Override
protected void onDraw(Canvas canvas) {
// Drawing commands go here
	Paint background= new Paint();
	background.setColor(getResources().getColor(R.color.c4_background));
	canvas.drawRect(0,0,getWidth(),getHeight(),background);
}*/
