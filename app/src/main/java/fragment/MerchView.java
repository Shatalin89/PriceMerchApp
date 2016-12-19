package fragment;



import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import request.ImageManager;
import request.LoadJSONTask;
import ru.yandex.shatalin.pricemerchapp.R;

public class MerchView extends Fragment implements  AdapterView.OnItemClickListener, LoadJSONTask.Listener  {


    public static final String MERCH_URL = "/merch/";
    public String URL;
    public ArrayList<HashMap<String, Object>> mAndroidMapList = new ArrayList<HashMap<String, Object>>();
    public static final String KEY_ID = "id";
    private static final String KEY_NAME = "name_merch";
    private static final String KEY_COUNT = "merch_count";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_NAME_IMAGE = "name";
    public ListView MerchListView;
    public ListAdapter adapter;
    Bundle bundle;
    //интерфейс для вывода детальной информации о товаре
    public interface onClickListView {
        void clickListView(String URL_ID);
    }

    public onClickListView onClickListView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_merch_view, null);
        MerchListView = (ListView) v.findViewById(R.id.merchView);
        MerchListView.setOnItemClickListener(this);
        bundle = getArguments();
        URL = bundle.getString("URL");
        Loadvoid();
        return v;
    }

    @Override
    public void onLoaded(JSONArray response) {

        try {
            HashMap<String, Object> map;
            Log.i("onLoaded letring: ", response.toString());

            int[] IDm = new int[20]; // массив айдишников
            String[] name_merch = new String[20];//массив названий
            int[] merch_count = new int[20];
            Bitmap[] image = new Bitmap[20];//массив картинок
            String[] URIImage = new String[20];
            int[] idImage = new int[20];

            for (int i=0; i<response.length(); i++){
                map = new HashMap<String, Object>();
                JSONObject merch = response.getJSONObject(i);

                IDm[i] = merch.getInt(KEY_ID);
                name_merch[i] = merch.getString(KEY_NAME);
                merch_count[i] = merch.getInt(KEY_COUNT);
                JSONArray photos = merch.getJSONArray("photos");
                Log.i("photso"+i+":", photos.toString());
                Log.i( "onLoaded: ", String.valueOf(photos.length()));
                URIImage[i]="http://192.168.1.140:8008/media/images/IMG_20161127_062646_fH7pL0p.jpg";
                for (int j=0; j<photos.length(); j++){
                    JSONObject images = photos.getJSONObject(j);

                    String type = images.getString("phototype");
                    Log.i("photso type ", type);

                        URIImage[i] = images.getString(KEY_IMAGE);
                        Log.i("photso uri ", URIImage[i]);

                }
//                Log.i("onLoaded: ", String.valueOf(URIImage[i].length()));
               /* if (URIImage[i].isEmpty()){

                }*/

                image[i]=ImageManager.downloadImage(URIImage[i]);

                map.put(KEY_ID, IDm[i]);
                map.put(KEY_NAME, name_merch[i]);
                map.put(KEY_COUNT, merch_count[i]);
                map.put(KEY_IMAGE, image[i]);
                mAndroidMapList.add(map);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadListView();
    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "Error !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(getActivity(), mAndroidMapList.get(i).get(KEY_URI),Toast.LENGTH_SHORT).show();
        onClickListView.clickListView(MERCH_URL+mAndroidMapList.get(i).get(KEY_ID)+"/");
    }


    private void loadListView() {
        adapter = new SimpleAdapter(getActivity(), mAndroidMapList, R.layout.listview,
                new String[] { KEY_ID, KEY_NAME, KEY_COUNT , KEY_IMAGE},
                new int[] { R.id.merchid, R.id.merchname, R.id.merchcount, R.id.imageView});
        MerchListView.setAdapter(adapter);
    }


    private void Loadvoid(){
        mAndroidMapList.clear();
        new LoadJSONTask(this).execute(URL+MERCH_URL);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onClickListView = (onClickListView) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public Bitmap getbmpfromURL(String surl){
        try {
            URL url = new URL(surl);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();
            Bitmap mIcon = BitmapFactory.decodeStream(in);
            return  mIcon;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}