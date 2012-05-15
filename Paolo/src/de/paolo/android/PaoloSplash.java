package de.paolo.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PaoloSplash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash);
		
		Thread timer = new Thread() {  
            public void run() {  
                try {  
                    sleep(2500); 
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                } finally {  
                    Intent intent = new Intent();
                    intent.setClass(PaoloSplash.this, PaoloActivity.class);
                    startActivity(intent);  
                }  
            }  
        };  
        timer.start();  
	}
	
	@Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
        finish();  
    }  
	
}
