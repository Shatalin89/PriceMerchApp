package request;


import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoadJSONTask extends AsyncTask<String, Void, JSONObject> {

    public LoadJSONTask(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onLoaded(JSONObject response);
        void onError();
    }

    private Listener mListener;

    @Override
    protected JSONObject doInBackground(String... strings) {

        String stringResponse = null;
        try {
            stringResponse = loadJSON(strings[0]);

            JSONObject response =  new JSONObject(stringResponse);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject response) {

        if (response != null) {
            mListener.onLoaded(response);
        } else {
            mListener.onError();
        }
    }

    private String loadJSON(String jsonURL) throws IOException {

        URL url = new URL(jsonURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();

        while ((line = in.readLine()) != null)
        {
            response.append(line);
        }

        in.close();
        return response.toString();
    }

}