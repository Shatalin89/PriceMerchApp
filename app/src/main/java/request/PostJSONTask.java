package request;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class PostJSONTask extends AsyncTask<JSONRequest, Void, String> {

    public PostJSONTask(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onOk(String status);
        void onCancel();
    }

    Listener mListener;

    private void JSONPost(JSONRequest json){
        String urlResponse = json.getURL_ID();
        JSONObject jsonObject = json.getJson();
        try {
            URL url = new URL(urlResponse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput (true);
            conn.setDoOutput (true);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            Log.i("my json", jsonObject.toString());
            Log.i("URL_ID_async", urlResponse);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == conn.HTTP_OK) {

                BufferedReader in=new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected String doInBackground(JSONRequest... jsonRequests) {
        String statusRequest;

        JSONPost(jsonRequests[0]);
        statusRequest = "OK";
        return statusRequest;

        //return null;
    }

    @Override
    protected void onPostExecute(String statusRequest) {

        if (statusRequest == "OK") {
            mListener.onOk(statusRequest);
        } else {
            mListener.onCancel();
        }
    }

}