package org.example.connectfour;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class DropAreaView extends View{
	private Matrix translate; 
	private Drawable mBallImage;
    private int mBallHeight;
    private int mBallWidth;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private float totalAnimDx;
    private float totalAnimDy;
    private Rect mBallRect;
    public static int mColumns;
    private GestureDetector gestures;
    private static String TAG = "DropAreaView";
	private boolean post;
	private int mBallSize;
	public DropAreaView(Context context) {
		super(context);
		  gestures = new GestureDetector(context,
                  new GestureListener(this));
		mBallImage=context.getResources().getDrawable(
                R.drawable.ball);
		mBallRect=new Rect();
		mColumns=7;
		
	}
	public void onAnimateMove(float dx, float dy)
	{
		 totalAnimDx = dx;
         totalAnimDy = dy;
         post = post(new Runnable() {
             public void run() {
                 onAnimateStep();
             }
         });
		
	}
	private void onAnimateStep()
	{
		drawImage((int)(totalAnimDx));
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		
		mBallImage.setBounds(mBallRect);
		mBallImage.draw(canvas);
	}
	@Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh)
	{
		Log.v(TAG,"entered in onSizeChanged");
		mCanvasWidth=w;
		mCanvasHeight=h;
		mBallSize=(mCanvasWidth/mColumns);
		mBallRect.set(2, mCanvasHeight-mBallSize,mBallSize, mCanvasHeight);
	}
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestures.onTouchEvent(event);
    }
	public void drawImage( int iX) {

    		mBallRect.set( iX, mCanvasHeight-mBallSize, iX+mBallSize, mCanvasHeight );
    		invalidate();
    }
	public void  onMove(float dx,float dy)
	{
		mBallRect=mBallImage.getBounds();
		mBallRect.left=(int) dx;
		invalidate();
	}


}
class GestureListener implements GestureDetector.OnGestureListener,
	GestureDetector.OnDoubleTapListener {

	DropAreaView view;
	private static String DEBUG_TAG="GestureDetecter";
	public GestureListener(DropAreaView view) {
		this.view = view;
	}

	public boolean onDown(MotionEvent e) {
		Log.v(DEBUG_TAG, "onDown");
		view.drawImage((int)(e.getX()));
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2,
			final float velocityX, final float velocityY) {
		Log.v(DEBUG_TAG, "onFling");
		return false;
	}

	public boolean onDoubleTap(MotionEvent e) {
		Log.v(DEBUG_TAG, "onDoubleTap");
	//	view.onResetLocation();
		return true;
	}

	public void onLongPress(MotionEvent e) {
		Log.v(DEBUG_TAG, "onLongPress");
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2,
			float distanceX, float distanceY) {
		Log.v(DEBUG_TAG, "onScroll");

		view.onMove(distanceX, distanceY);
		return true;
	}

	public void onShowPress(MotionEvent e) {
		Log.v(DEBUG_TAG, "onShowPress");
	}
	
	public boolean onSingleTapUp(MotionEvent e) {
		Log.v(DEBUG_TAG, "onSingleTapUp");
		return false;
	}
	
	public boolean onDoubleTapEvent(MotionEvent e) {
		Log.v(DEBUG_TAG, "onDoubleTapEvent");
		return false;
	}
	
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.v(DEBUG_TAG, "onSingleTapConfirmed");
		return false;
	}

}
