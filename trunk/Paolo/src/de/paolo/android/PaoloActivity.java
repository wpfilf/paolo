package de.paolo.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class PaoloActivity extends Activity implements OnClickListener, OnInitListener, OnUtteranceCompletedListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private static final int MY_DATA_CHECK_CODE = 5678;

    protected Context mContext;
    
    private ListView mList;
    private TextToSpeech mTts;
    private String mAntw;
    private boolean mTTSReady = false;
    private HashMap<String, String> mHash;
    private ArrayList<String> mListItems;
    private ArrayAdapter<String> mAdapter;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.main);
        //setContentView(R.drawable.button);
        
        //setContext
        mContext = getApplicationContext();
        
        // Get display items for later interaction
        Button speakButton = (Button) findViewById(R.id.speakbtn);

        //init list
        mList = (ListView) findViewById(R.id.historylist);
        mListItems = new ArrayList<String>();
        mAdapter = new PaoloArrayAdapter(this, R.layout.historyrow, mListItems);
        
        mList.setAdapter(mAdapter);
        
        
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

       //Check if TTS Engine is available on phone
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
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
     *****************************************/
    
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Sprich mit Paolo");

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

    /****************************************************
     * 
     * Handle the results from the recognition activity.
     *
     ****************************************************/
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
           
        	// Fill the list view with the string the recognizer thought it could have heard
            ArrayList<String> recList = new ArrayList<String>();
        	recList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            
        	//get first response
            String input = recList.get(0);
            
            //put in list
            mListItems.add(input);
    		mAdapter.notifyDataSetChanged();
            
            
            //send to paolo asyncTask to get answer
            PaoloSays paolo = new PaoloSays(PaoloActivity.this);
            paolo.execute("TestBot",input);
            
        }else {
        	
        	if (requestCode == MY_DATA_CHECK_CODE) {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    // success, create the TTS instance
                    mTts = new TextToSpeech(mContext, this);
                    mTts.setOnUtteranceCompletedListener(this);
                } else {
                    // missing data, install it
                    Intent installIntent = new Intent();
                    installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
            }
        	
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
    	
    	mAntw = antw;
    	
    	if(mTTSReady){
    		
    		//speak answer
    		mTts.speak(antw, TextToSpeech.QUEUE_FLUSH, mHash);
    		
    		//fill item in list
    		mListItems.add(mAntw);
    		mAdapter.notifyDataSetChanged();
    		
    	}else{
    		Toast.makeText(mContext, "TTS not ready", Toast.LENGTH_SHORT).show();
    	}
    	
    }
    
    
    
    
    
    /************************************
    *
    * When TTS speaker has finished Event
    * 
    * @param antw
    */
    public void onUtteranceCompleted(String utteranceId) {
			//Toast.makeText(mContext, mAntw, Toast.LENGTH_SHORT).show();
	}

	/************************************
    *
    * TTS Init finished
    * 
    * @param antw
    */
	public void onInit(int status) {
		// TODO Auto-generated method stub
		mTts.setLanguage(Locale.GERMANY);
		mHash = new HashMap<String, String>();
		mHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "EOA");
		mTTSReady = true;
	}
	
	
	/************************************
    *
    * Close TTS Engine
    * 
    */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mTts.shutdown();
	}
	
	
    
}