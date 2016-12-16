package ru.yandex.shatalin.pricemerchapp.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fragment.MerchDetails;
import fragment.MerchView;
import ru.yandex.shatalin.pricemerchapp.R;


public class MainActivity extends Activity implements MerchView.onClickListView, MerchDetails.onClickOkButton, MerchDetails.onClickImageView {

    public String URLM;
    private static final String GET_MERCH = "/merch/";
    private static final String PHOTO = "/merch/photo/";


    public MerchView FragMerchView;
    public MerchDetails FragMerchDetails;

    public FragmentTransaction transaction;
    private Bundle bundle;

    //Создание главной активити
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(ClassLoader.getSystemResourceAsStream());
        //задали адрес хоста
        URLM = "http://192.168.1.10:8008";
        //
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        bundle = new Bundle();
        //стартуем фрагмент со списком товаров
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

    //процедура вывода фрагмента с детальным описание товара
    @Override
    public void clickListView(String URL_ID) {
        FragMerchDetails = new MerchDetails();
        bundle.putString("URL_ID", URLM + URL_ID);
        bundle.putString("MODE_STATUS", "VIEW");
        FragMerchDetails.setArguments(bundle);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frgmCont, FragMerchDetails);
        transaction.commit();
        Log.i("clickListView: ", "нажаите произведено");
    }
    //процедура добавление нового элемента
    public void addClickMerchView(View view) {
        FragMerchDetails = new MerchDetails();
        bundle.putString("URL_ID", URLM+GET_MERCH);
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
    //процедура добавление картинки из галереи в имейдж вью
    @Override
    public Bitmap clickImageView(Intent imageReturnedIntent) {
        try {
            Uri imageUri = imageReturnedIntent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int culumnindex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(culumnindex);
            cursor.close();
            multipartRequest(URLM + PHOTO, "idmerch=1&phototype=m&name=", filePath, "image");
            final InputStream imageStream;
            imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            return selectedImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //<пример аплоада>
    public String multipartRequest(String urlTo, String post, String filepath, String filefield) throws ParseException, IOException {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary =  "*****"+Long.toString(System.currentTimeMillis())+"*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;
        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] +"\"" + lineEnd);
            outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while(bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(lineEnd);
            // Upload POST Data
            String data = post+q[idx];
            String[] posts = data.split("&");
            int max = posts.length;
            for(int i=0; i<max;i++) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                String[] kv = posts[i].split("=");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + kv[0] + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain"+lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(kv[1]);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            inputStream = connection.getInputStream();
            result = this.convertStreamToString(inputStream);
            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            return result;
        } catch(Exception e) {
            Log.e("MultipartRequest","Multipart Form Upload Error");
            e.printStackTrace();
            return "error";
        }
    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    //</пример аплоада>

    //меню итем выбираем сервак
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
//создание меню итем
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.server1).setCheckable(true);
        return true;
    }
}


