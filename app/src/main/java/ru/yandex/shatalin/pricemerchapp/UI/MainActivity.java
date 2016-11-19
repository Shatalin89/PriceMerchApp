package ru.yandex.shatalin.pricemerchapp.UI;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.StrictMode;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import fragment.MerchDetails;
import fragment.MerchView;
import ru.yandex.shatalin.pricemerchapp.R;


public class MainActivity extends Activity implements MerchView.onClickListView, MerchDetails.onClickOkButton {

    public static final String URL = "http://192.168.1.140:8008";
    private static final String URL_MERCH = "/api/v1/merch/";

    public MerchView FragMerchView;
    public MerchDetails FragMerchDetails;

    public FragmentTransaction transaction;
    private Bundle bundle;
    int MENU_ID=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(ClassLoader.getSystemResourceAsStream());
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
        bundle.putString("URL", URL);
        FragMerchView.setArguments(bundle);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frgmCont, FragMerchView);
        transaction.commit();

    }

    @Override
    public void clickListView(String URL_ID){
        FragMerchDetails = new MerchDetails();
        bundle.putString("URL_ID", URL+URL_ID);
        bundle.putString("MODE_STATUS", "VIEW");
        FragMerchDetails.setArguments(bundle);
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frgmCont, FragMerchDetails);
        transaction.commit();

    }

    public void addClickMerchView(View view) {
        FragMerchDetails = new MerchDetails();
        bundle.putString("URL_ID", URL+URL_MERCH);
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

}