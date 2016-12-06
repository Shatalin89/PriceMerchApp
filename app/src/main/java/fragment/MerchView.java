package fragment;



import android.app.Activity;
import android.app.Fragment;
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



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import request.LoadJSONTask;
import ru.yandex.shatalin.pricemerchapp.R;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MerchView extends Fragment implements  AdapterView.OnItemClickListener, LoadJSONTask.Listener  {


    public static final String MERCH_URL = "/merch/";
    public String URL;

    public List<HashMap<String, String>> mAndroidMapList = new ArrayList<>();
    public static final String KEY_ID = "id";
    private static final String KEY_NAME = "name_merch";
    private static final String KEY_COUNT = "merch_count";
   // private static final String KEY_URI ="resource_uri";
    public ListView MerchListView;
    public ListAdapter adapter;
    Bundle bundle;
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
        URL=bundle.getString("URL");
        Loadvoid();
        return v;
    }



    @Override
    public void onLoaded(JSONArray response) {

        try {
           // JSONArray object = response.getJSONArray(1);
            Log.i("onLoaded letring: ", String.valueOf(response.length()));
            for (int i = 0; i < response.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                JSONObject merch = response.getJSONObject(i);


                String IDm = merch.getString(KEY_ID);
                map.put(KEY_ID, IDm);
                String name_merch = merch.getString(KEY_NAME);
                map.put(KEY_NAME, name_merch);
                String merch_count = merch.getString(KEY_COUNT);
                map.put(KEY_COUNT, merch_count);
              //  String merch_uri = merch.getString(KEY_URI);
             //   map.put(KEY_URI, merch_uri);

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
        onClickListView.clickListView(mAndroidMapList.get(i).get(KEY_ID));
    }


    private void loadListView() {

        adapter = new SimpleAdapter(getActivity(), mAndroidMapList, R.layout.listview,
                new String[] { KEY_ID, KEY_NAME, KEY_COUNT },
                new int[] { R.id.merchid, R.id.merchname, R.id.merchcount});
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

}