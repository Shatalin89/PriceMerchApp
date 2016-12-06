package ru.yandex.shatalin.pricemerchapp.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.CheckResult;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.StrictMode;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fragment.MerchDetails;
import fragment.MerchView;
import ru.yandex.shatalin.pricemerchapp.R;


public class MainActivity extends Activity implements MerchView.onClickListView, MerchDetails.onClickOkButton, MerchDetails.onClickImageView {

    public String URLM;
    private static final String URL_MERCH = "/merch/";


    public MerchView FragMerchView;
    public MerchDetails FragMerchDetails;

    public FragmentTransaction transaction;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(ClassLoader.getSystemResourceAsStream());
        URLM = "http://192.168.1.10:8008";
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        bundle = new Bundle();
        startFragement();
    }

    public void onClickCancelMerchView(View view) {
        startFragement();
    }

    void startFragement(){
        FragMerchView = new MerchView();
        bundle.putString("URL", URLM);
        FragMerchView.setArguments(bundle);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frgmCont, FragMerchView);
        transaction.commit();
    }

    @Override
    public void clickListView(String URL_ID){
        FragMerchDetails = new MerchDetails();
        bundle.putString("URL_ID", URLM+URL_ID);
        bundle.putString("MODE_STATUS", "VIEW");
        FragMerchDetails.setArguments(bundle);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frgmCont, FragMerchDetails);
        transaction.commit();
    }

    public void addClickMerchView(View view) {
        FragMerchDetails = new MerchDetails();
        bundle.putString("URL_ID", URLM+URL_MERCH);
        bundle.putString("MODE_STATUS", "ADD");
        FragMerchDetails.setArguments(bundle);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frgmCont, FragMerchDetails);
        transaction.commit();
    }

    @Override
    public void clickOkButton() {
        startFragement();
    }

    @Override
    public Bitmap clickImageView(Intent imageReturnedIntent){
        try {
            Uri imageUri = imageReturnedIntent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Log.i("filePathColumn: ", filePathColumn[0]);
            Cursor cursor  = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            Log.i("cursor: ", cursor.toString());
            cursor.moveToFirst();
            Log.i("cursor: ", cursor.toString());
            int culumnindex = cursor.getColumnIndex(filePathColumn[0]);
            Log.i("culumnindex: ", String.valueOf(culumnindex));
            String filePath = cursor.getString(culumnindex);
            Log.i("filePath: ", filePath);
            cursor.close();
            Log.i("clickImageView: ", imageUri.getPath());
            doFileUpload(filePath);
            final InputStream imageStream;
            imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            return selectedImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void doFileUpload(String path){
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String urlString = URLM+"/merch/photo/";   // server ip
        try
        {
            //------------------ CLIENT REQUEST
            FileInputStream fileInputStream = new FileInputStream(new File(path) );
            // open a URL connection to the Servlet
            URL url = new URL(urlString);
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+"    ");
            dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + path + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(lineEnd);

            // close streams
            Log.e("Debug","File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        }
        catch (MalformedURLException ex)
        {
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
        catch (IOException ioe)
        {
            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }

        //------------------ read the SERVER RESPONSE
        try {
            inStream = new DataInputStream ( conn.getInputStream() );
            String str;
            while (( str = inStream.readLine()) != null)
            {
                Log.e("Debug","Server Response "+str);
            }
            inStream.close();
        }
        catch (IOException ioex){
            Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.server1:
                URLM = "http://192.168.1.140:8008";
                startFragement();
                return true;
            case R.id.server2:
                URLM = "http://192.168.1.10:8008";
                startFragement();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.server1).setCheckable(true);
        Log.i("URLM start:", URLM);
        return true;
    }
}


