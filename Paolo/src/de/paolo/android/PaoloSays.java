package de.paolo.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class PaoloSays extends AsyncTask<String, Void, String> {

	private PaoloActivity activity;

	public PaoloSays(PaoloActivity activity)
	{
		this.activity = activity;
	}
	
	
	/********************************
	 * 
	 * Wird vor dem Task ausgeführt
	 * 
	 * 
	 ********************************/
	@Override
	protected void onPreExecute()
	{
		//progressDialog.show();??
	}
	
	/*********************************
	 * 
	 * MACH PAOLO MACH MACH
	 *
	 *********************************/
	@Override
	protected String doInBackground(String... args) 
	{
		int responseCode = 0;
		String antw = "";
		try 
		{
			HttpClient client = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://paperbits.informatik.hs-augsburg.de/paolo/src/androidtalk.php");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        	nameValuePairs.add(new BasicNameValuePair("botname", args[0]));
	        	nameValuePairs.add(new BasicNameValuePair("eingabe", args[1]));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			int executeCount = 0;
			HttpResponse response;
			do
			{
				// Execute HTTP Post Request
				executeCount++;
				response = client.execute(httppost);
				responseCode = response.getStatusLine().getStatusCode();						
				// If you want to see the response code, you can Log it
				// out here by calling:
				// Log.d("Paolo script", "statusCode: " + responseCode)
			} while (executeCount < 5 && responseCode != 200);

			//get antwort
			
			HttpEntity entity = response.getEntity();
	        
	        BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent()));
	    	StringBuilder sb1 = new StringBuilder();
	    	String line = null;
	    	while ((line = buf.readLine()) != null) {
	    		sb1.append(line+"\n");
	    	}
	    	
	        antw = sb1.toString();
	        
		}
		catch (Exception e) {
			responseCode = 408;
			e.printStackTrace();
			//TODO Nachricht, wenn Server nicht erreichbar
		}
		return antw;
	}
	
	/********************************************
	 * 
	 * Gib den Wert zurück an die Talk Funktion
	 * 
	 ********************************************/
	@Override
	protected void onPostExecute(String result) {
		
		activity.talk(result);
	}
}