package org.example.connectfour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

public class dropView extends View{

	private Drawable mBallImage;
	private Rect mBallRect;
	private int mCellSpace;
	private int mCanvasHeight;
	private int mCanvasWidth;
	private static int numOfColumns;
	public dropView(Context context) {
		super(context);
		numOfColumns=7;
		mBallImage = context.getResources().getDrawable(
                 R.drawable.ball);
		mBallRect=new Rect();
		
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		Paint CLine=new Paint();
        CLine.setARGB(255, 0, 0, 0);
        Paint CBackground=new Paint();
        CBackground.setARGB(200,100,100,100);
        canvas.drawRect(0,0,getWidth(),getHeight(),CBackground);
        
        for(int start=2;start<mCanvasWidth;start=start+mCellSpace)
                canvas.drawLine(start, 0, start, mCanvasHeight-2, CLine);
        canvas.drawLine(2,mCanvasHeight-2,mCanvasWidth-2,mCanvasHeight-2,CLine);
		mBallImage.setBounds(mBallRect);
		mBallImage.draw(canvas);
		
	}
	@Override 
	protected void onSizeChanged(int w,int h,int oldw,int oldh)
	{
		mCanvasWidth=w;
		mCanvasHeight=h;
		mCellSpace=(int)(mCanvasWidth/numOfColumns);
		int mBallSize = (mCanvasWidth/numOfColumns);
		mBallRect.set(2, mCanvasHeight-mBallSize,mBallSize, mCanvasHeight);
	}

}
