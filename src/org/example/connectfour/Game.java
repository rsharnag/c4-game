package org.example.connectfour;

import org.example.connectfour.gameView.GameThread;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
public class Game extends Activity{
	private GameThread mgameThread; 
	private gameView mC4View;
	@Override 
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mC4View=(gameView) findViewById(R.id.c4game);
		mgameThread=mC4View.getThread();
		if (savedInstanceState == null) {
            // we were just launched: set up a new game
            mgameThread.setState(GameThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mgameThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }
	}
	
}
