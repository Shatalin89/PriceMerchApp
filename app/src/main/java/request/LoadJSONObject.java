package request;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import fragment.MerchDetails;


public class LoadJSONObject extends AsyncTask<String, Void, JSONObject> {

    public LoadJSONObject(LoadJSONObject.Listener listener) {
        mListener = listener;
    }



    public interface Listener {
        void onLoaded(JSONObject response);
        void onError();
    }

    private LoadJSONObject.Listener mListener;

    @Override
    protected JSONObject doInBackground(String... strings) {
        String stringResponse = null;
        try {
            stringResponse = loadJSON(strings[0]);
            Log.e("doInBackground: ", stringResponse);

            //   JSONObject res  = new JSONObject(stringResponse);
            //  Log.i("jsonobgect doInBack", res.toString());
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
        Log.i("response: ", response.toString());
        if (response != null) {
            mListener.onLoaded(response);
        } else {
            mListener.onError();
        }
    }

    private String loadJSON(String jsonURL) throws IOException {
        Log.i("loadJSON: ", jsonURL);
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

