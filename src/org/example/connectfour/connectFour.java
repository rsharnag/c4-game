package org.example.connectfour;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;


public class connectFour extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	C4_game new_game;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //new_game=new C4_game(7,9,4);
        setContentView(R.layout.main);
        
        //Set up the buttons
        View new_game_button=findViewById(R.id.new_game_button);
        new_game_button.setOnClickListener(this);
        View exit_button=findViewById(R.id.exit_button);
        exit_button.setOnClickListener(this);
        View pref_button=findViewById(R.id.setpreference);
        pref_button.setOnClickListener(this);
        
    }
    public void onClick(View v)
    {
    	switch(v.getId())
    	{
    	case R.id.new_game_button:
    		Intent i=new Intent(this,Game.class);
    		startActivity(i);
    		break;
    	case R.id.exit_button:
    		finish();
    		break;
    	case R.id.setpreference:
    		startActivity(new Intent(this, prefs.class));
    		
    	}
    	
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.menu, menu);
       return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
       case R.id.settings:
          startActivity(new Intent(this, prefs.class));
          return true;
       // More items go here (if any) ...
       }
       return false;
    }

    
}
