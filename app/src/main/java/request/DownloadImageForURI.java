package request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadImageForURI extends AsyncTask<String, Void, Bitmap> {

    public  DownloadImageForURI (setArrayBitmap listener){
        mListener = listener;
    }
    public interface setArrayBitmap {
        void setBitmap(Bitmap bitmap);
        void setNoImage();
    }

    private setArrayBitmap mListener;

    private final static String TAG = "DownloadImageForURI";
    @Override
    protected Bitmap doInBackground(String... iUrl) {
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream buf_stream = null;
        try {
            Log.v(TAG, "Starting loading image by URL: " + iUrl[0]);
            conn = (HttpURLConnection) new URL(iUrl[0]).openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            buf_stream = new BufferedInputStream(conn.getInputStream(), 8192);
            bitmap = BitmapFactory.decodeStream(buf_stream);
            buf_stream.close();
            conn.disconnect();
            buf_stream = null;
            conn = null;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Url parsing was failed: " + iUrl[0]);
        } catch (IOException ex) {
            Log.d(TAG, iUrl[0] + " does not exists");
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "Out of memory!!!");
            return null;
        } finally {
            if ( buf_stream != null )
                try { buf_stream.close(); } catch (IOException ex) {}
            if ( conn != null )
                conn.disconnect();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            mListener.setBitmap(bitmap);
        } else {
            mListener.setNoImage();
        }
    }

}
