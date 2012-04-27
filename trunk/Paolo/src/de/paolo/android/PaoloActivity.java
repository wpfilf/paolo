package de.paolo.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PaoloActivity extends Activity implements OnClickListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    protected Context mContext;
    
    private ListView mList;
    private String mInput;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.main);
        
        //setContext
        mContext = getApplicationContext();
        
        // Get display items for later interaction
        Button speakButton = (Button) findViewById(R.id.speakbtn);

        mList = (ListView) findViewById(R.id.historylist);

        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }

        // Most of the applications do not have to handle the voice settings. If the application
        // does not require a recognition in a specific language (i.e., different from the system
        // locale), the application does not need to read the voice settings.
    }
    
    /*******************************************
     * 
     * Handle the click on the start recognition button.
     * 
     */
    public void onClick(View v) {
        if (v.getId() == R.id.speakbtn) {
            startVoiceRecognitionActivity();
        }
    }

    /*****************************************
     * 
     * Fire an intent to start the speech recognition activity.
     * 
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");

        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Specify how many results you want to receive. The results will be sorted
        // where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        // Specify the recognition language. This parameter has to be specified only if the
        // recognition has to be done in a specific language and not the default one (i.e., the
        // system locale). Most of the applications do not have to set this parameter.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"de-DE");

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**************************************
     * 
     * Handle the results from the recognition activity.
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
           
        	
        	// Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    matches));
            
            //get first Match
            mInput = matches.get(0);
            //send to paolo asyncTask
            PaoloSays paolo = new PaoloSays(PaoloActivity.this);
            paolo.execute("TestBot",mInput);
            
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    /************************************
     *
     * Spreche den Antwort-Text
     * 
     * @param antw
     */
    public void talk(String antw){
    	Toast.makeText(mContext, antw, Toast.LENGTH_SHORT).show();
    }
    
}