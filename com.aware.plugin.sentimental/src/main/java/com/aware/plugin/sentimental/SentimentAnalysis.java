package com.aware.plugin.sentimental;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import com.aware.Aware;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SentimentAnalysis {
	//confirm that context is needed
    private Context context;
    private JSONObject Dictionary;

    public SentimentAnalysis(Context c) {
        context = c;
        //read sentiment dictionary from assets
        try {
		    Dictionary = new JSONObject(loadJSONFromAsset(c));
        } catch (JSONException ex) {
            ex.printStackTrace();

        }
    }
	
	//function to read json file to string
	public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is;
            int id = context.getResources().getIdentifier("sentiment", "raw", context.getPackageName());
            is = context.getResources().openRawResource(id);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
    /**
     * Read the dictionary
     *
     * @return Dictionary as String
     */
    public String getDictionaryAsString() {
        
        return Dictionary.toString();
    }

	public double getScoreFromWord(String inpWord){
        double result;
        JSONArray CatArr=Dictionary.getJSONArray("Categories")
        
		return 1;
	}

    public SentimentAnalysis getInstance(){
		return this;
	}
}
