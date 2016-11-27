package fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.style.IconMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import request.JSONRequest;
import request.LoadJSONTask;
import request.PostJSONTask;
import ru.yandex.shatalin.pricemerchapp.R;
import ru.yandex.shatalin.pricemerchapp.UI.MainActivity;

import static android.app.Activity.RESULT_OK;


public class MerchDetails extends Fragment implements LoadJSONTask.Listener, PostJSONTask.Listener {


    public interface onClickOkButton {
        void clickOkButton();
    }

    public interface onClickImageView {
        Bitmap clickImageView(Intent intent);
        void doFileUpload();
    }

    onClickOkButton onClickOkButton;
    onClickImageView onClickImageView;
    public String URL_ID;
    public EditText setNameMerch, setPriceMerch, setDescriptionMerch;
    public CheckBox setEnabledCheckBox, setDeletedCheckBox;
    public
    String id;
    String nameMerch;
    int priceMerch;
    boolean enabledMerch;
    boolean deleteMerch;
    int countMerch;
    String merchDescription;
    public String MODE_STATUS;
    public String setElementStatus;
    Bundle bundle;
    public JSONObject jsonObject;
    public JSONRequest addDataJsonRequest;
    Button okButton;
    public String typeRequest;
    private final String POST = "POST";
    private final String PUT = "PUT";
    private final String GET = "GET";
    public ImageView imageView;
    private final int Pick_image = 1;
    public Bitmap bitmapimg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_merch_details, null);

        setHasOptionsMenu(true);

        // Получение параметров от активити
        bundle = getArguments();
        URL_ID = bundle.getString("URL_ID");
        MODE_STATUS = bundle.getString("MODE_STATUS");
        addDataJsonRequest = new JSONRequest();
        imageView = (ImageView) v.findViewById(R.id.ViewMerchImage);
        //инициализация всех элементов интерфейса
        setNameMerch = (EditText) v.findViewById(R.id.editTextName);
        setPriceMerch = (EditText) v.findViewById(R.id.editTextSetPrice);
        setDescriptionMerch = (EditText) v.findViewById(R.id.editTextSetDesription);
        setEnabledCheckBox = (CheckBox) v.findViewById(R.id.checkBoxEnabled);
        setDeletedCheckBox = (CheckBox) v.findViewById(R.id.checkBoxDeleted);

        if (MODE_STATUS == "VIEW") {
            setElementStatus = "disable";
            new LoadJSONTask(this).execute(URL_ID);
        } else if (MODE_STATUS == "EDIT") {
            enableEdit();
        } else if (MODE_STATUS == "ADD") {
            setElementStatus = "enable";
            typeRequest = POST;
            setEditable(setNameMerch, setElementStatus, null);
            setEditable(setPriceMerch, setElementStatus, null);
            setEditable(setDescriptionMerch, setElementStatus, null);
        } else {
            Log.e("ERROR MODE_STATUS:", MODE_STATUS);
        }
        okButton = (Button) v.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addParametr(typeRequest);
                Log.e("Click Ok", "проверяй");
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             // onClickImageLoad();
                                             //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
                                             Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                             //Тип получаемых объектов - image:
                                             photoPickerIntent.setType("image/*");
                                             //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
                                             startActivityForResult(photoPickerIntent, Pick_image);
                                             Log.e("Click IMAGE", "проверяй");
                                         }
                                     }
        );
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_edit:
                enableEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addParametr(String typeRequest) {
        jsonObject = new JSONObject();
        try {
            jsonObject.put("name_merch", setNameMerch.getText());
            if (setDeletedCheckBox.isChecked()) {
                jsonObject.put("merch_del", true);
            } else {
                jsonObject.put("merch_del", false);
            }
            if (setEnabledCheckBox.isChecked()) {
                jsonObject.put("merch_enabled", true);
            } else {
                jsonObject.put("merch_enabled", false);
            }
            jsonObject.put("merch_count", "1");
            jsonObject.put("merch_price", setPriceMerch.getText());
            jsonObject.put("merch_description", setDescriptionMerch.getText());
            //jsonObject.put("image", bitmapimg);
            addDataJsonRequest.setJson(jsonObject);
            addDataJsonRequest.setURL_ID(URL_ID);
            addDataJsonRequest.setTypeRequest(typeRequest);
            Log.e("URL_ID", URL_ID);
            new PostJSONTask(this).execute(addDataJsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaded(JSONObject response) {

        try {
            id = response.getString("id");
            nameMerch = response.getString("name_merch");
            enabledMerch = response.getBoolean("merch_enabled");
            deleteMerch = response.getBoolean("merch_del");
            countMerch = response.getInt("merch_count");
            priceMerch = response.getInt("merch_price");
            merchDescription = response.getString("merch_description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setEditable(setNameMerch, setElementStatus, nameMerch);
        setEditable(setPriceMerch, setElementStatus, String.valueOf(priceMerch));
        setEditable(setDescriptionMerch, setElementStatus, merchDescription);
        setEnabledCheckBox.setChecked(enabledMerch);
        setDeletedCheckBox.setChecked(deleteMerch);
    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "Error !", Toast.LENGTH_SHORT).show();
    }

    void setEditable(EditText edit, String status, String name) {
        if (status == "disable") {
            edit.setText(name);
            edit.setFocusable(false);
            edit.setFocusableInTouchMode(false);
            edit.setClickable(false);
        } else if (status == "enable") {
            edit.setText(name);
            edit.setFocusable(true);
            edit.setFocusableInTouchMode(true);
            edit.setClickable(true);
        } else {
            edit.setEnabled(false);
            Log.e("Error EditeText", "Edit status:" + status);
        }
    }

    @Override
    public void onOk(String status) {
        Toast.makeText(getActivity(), "Товар добавлен! " + status, Toast.LENGTH_SHORT).show();
        onClickOkButton.clickOkButton();
    }

    @Override
    public void onCancel() {
        Toast.makeText(getActivity(), "Чтото пошло не так!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onClickOkButton = (onClickOkButton) activity;
            onClickImageView = (onClickImageView) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private void enableEdit() {
        setElementStatus = "enable";
        typeRequest = PUT;
        setEditable(setNameMerch, setElementStatus, setNameMerch.getText().toString());
        setEditable(setPriceMerch, setElementStatus, setPriceMerch.getText().toString());
        setEditable(setDescriptionMerch, setElementStatus, setDescriptionMerch.getText().toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case Pick_image:
                if (resultCode == RESULT_OK) {
                    //Получаем URI изображения, преобразуем его в Bitmap
                    //объект и отображаем в элементе ImageView нашего интерфейса:
                    bitmapimg = onClickImageView.clickImageView((imageReturnedIntent));
                    imageView.setImageBitmap(bitmapimg);
                }
        }
    }


}