package com.polared.stamp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ProduceStamp extends AppCompatActivity{
    private TextView tvType;
    private EditText edtStampName, edtCount;
    private ImageButton ibQuestion;
    private Button btnCreateStamp;

    private LinearLayout llAddView;
    private ArrayList<StampItems> itemList = new ArrayList<>();

    private HashMap<Integer, String> itemNameMap = new HashMap<>();
    private HashMap<Integer, String> itemCountMap = new HashMap<>();
    private HashMap<Integer, String> itemImageMap = new HashMap<>();

    private int maxTag = 0;
    private int imageViewId = 0;



    private String[] items = new String[]{"Type1", "Type2"};

    private ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Uri uri = result.getData().getData();

                        ImageView img = llAddView.findViewById(imageViewId);
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .centerCrop()
                                .into(img);

                        itemImageMap.put(imageViewId, uri.toString());

                    }
                }
            });

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        setContentView(R.layout.activity_produce_stamp);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.stamp_produce_actionbar);

        findViewByIdFunc();

        eventHandlerFunc();

        createView(0);

    }

    private void createView(int tag) {
        View addView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_items, null, false);

        llAddView.addView(addView);

        llAddView.setTag(tag);

        maxTag = tag;

        itemNameMap.put(tag, "");
        itemCountMap.put(tag, "");
        itemImageMap.put(tag, "");

        setEventItemName(addView, tag);

        setEventItemCount(addView, tag);

        setEventImageChoice(addView, tag);

        setEventController(addView, tag);

    }

    private void setEventController(View addView, int tag) {
        ImageButton ibController = addView.findViewById(R.id.ibController);

        ibController.setOnClickListener(v1 -> {

            if(tag == maxTag){
                ibController.setBackgroundResource(R.drawable.subtract);
                createView(tag+1);
            }else{
                itemNameMap.remove(tag);
                itemCountMap.remove(tag);
                itemImageMap.remove(tag);
                llAddView.removeView(addView);
            }

        });

    }

    private void setEventImageChoice(View addView, int tag) {

        ImageView imgChoice = addView.findViewById(R.id.imgChoice);
        imgChoice.setId(tag);
        imgChoice.setTag(tag);

        imgChoice.setOnClickListener(v1 -> {

            if(checkPermission()) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                imageViewId = tag;
                mStartForResult.launch(intent);
            }

        });
    }

    private void setEventItemCount(View addView, int tag) {
        EditText edtItemCount = addView.findViewById(R.id.edtItemCount);
        edtItemCount.setTag(tag);

        edtItemCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText)addView.findViewById(R.id.edtItemCount)).getText().toString();
                itemCountMap.put(tag, text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setEventItemName(View addView, int tag) {
        EditText edtItemName = addView.findViewById(R.id.edtItemName);
        edtItemName.setTag(tag);

        edtItemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = ((EditText)addView.findViewById(R.id.edtItemName)).getText().toString();
                itemNameMap.put(tag, text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void eventHandlerFunc() {

        ibQuestion.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Type1 : 아무 스탬프 아이템 사용 시 스탬프 종료 \nType2 : 모든 스탬프 아이템 사용 시 스탬프 종료");
            builder.setPositiveButton("확인", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        tvType.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tvType.setText(items[which]);
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        btnCreateStamp.setOnClickListener(v -> {
            String message = checkData();

            if(message != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(message);
                builder.setPositiveButton("확인", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }else{

                requestJsonData();
                finish();
            }

        });



    }

    private void requestJsonData() {

        insertItemList();

        String stamp_id = String.valueOf((getIntent().getIntExtra("stamp_id", 0)+1));
        String stamp_type = tvType.getText().toString();
        String stamp_status = "available";
        String stamp_name = edtStampName.getText().toString();
        String stamp_total_count = edtCount.getText().toString();

        itemList.get(0).setItem_status("available");

        CreateStamp createStamp = new CreateStamp(stamp_id, stamp_type, stamp_status, stamp_name, stamp_total_count, itemList);

        Intent intent = new Intent();
        intent.putExtra("CreateStamp", createStamp);
        setResult(Activity.RESULT_OK, intent);



    }

    private void insertItemList() {
        int id = 1001;

        int size = Integer.parseInt(edtCount.getText().toString());

        for(int i = 0; i < size; i++){

            String item_id = String.valueOf(id);
            String item_name = edtStampName.getText().toString()+(i+1);
            String item_position = String.valueOf((i+1));
            String item_image = "-1";
            String item_status = "-1";

            itemList.add(new StampItems(item_id, item_name, item_position, item_image, item_status));
            id++;
        }

        Iterator<Integer> iterator = itemNameMap.keySet().iterator();

        while(iterator.hasNext()){
            int key = iterator.next();
            String itemName = itemNameMap.get(key);
            String itemCount = itemCountMap.get(key);
            String itemImage = itemImageMap.get(key);
            int position = Integer.parseInt(itemCount);

            itemList.get(position-1).setItem_name(itemName);
            itemList.get(position-1).setItem_position(itemCount);
            itemList.get(position-1).setItem_image(itemImage);

        }
    }

    private boolean checkPermission(){
        boolean hasPermission = false;
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permissionState == PackageManager.PERMISSION_GRANTED){

            hasPermission = true;
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("권한설정을 해주세요.");
            builder.setMessage("OK를 누르면 권한설정창으로 이동합니다.");

            builder.setPositiveButton("CANCEL", null);

            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return hasPermission;
    }

    private String checkData() {
        String message = null;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editText = null;

        boolean isCheckCreateStamp = false;
        boolean hasLastCount = false;

        check:while(!isCheckCreateStamp){
            if(edtStampName.getText().toString().length() < 1){
                message = "스탬프 이름을 입력해주세요.";
                editText = edtStampName;

                break;
            }

            String totalCount =  edtCount.getText().toString();

            if(totalCount.length() < 1){
                message = "총 Count를 입력해주세요.";
                editText = edtCount;

                break;
            }

            if(tvType.getText().toString().length() < 1){
                message = "유형을 정해주세요.";
                break;
            }

            Iterator<Integer> iterator = itemNameMap.keySet().iterator();
            while(iterator.hasNext()){
                int key = iterator.next();
                if (itemNameMap.get(key).length() < 1){
                    message = "혜택을 입력해주세요.";

                    break check;

                }

                String count = itemCountMap.get(key);

                if(count.length() < 1){
                    message = "혜택 위치를 지정해주세요.";
                    break check;
                }

                if(Integer.parseInt(count) > Integer.parseInt(totalCount)){
                    message = "총 Count 보다 높을순 없습니다.";
                    break check;
                }

                if(Integer.parseInt(count) == Integer.parseInt(totalCount)){
                    hasLastCount = true;
                }

                if(itemImageMap.get(key).length() < 1){
                    message = "이미지를 지정해주세요.";
                    break check;
                }

            }

            if(!hasLastCount){
                message = edtCount.getText().toString()+"번째 위치에 혜택을 정해주셔야합니다.";
                break;
            }

            editText = null;
            isCheckCreateStamp = true;
        }

        if(editText != null){
            editText.requestFocus();
//            editText.length()
            imm.showSoftInput(editText, 0);
        }


        return message;
    }


    private void findViewByIdFunc() {
        edtStampName = findViewById(R.id.edtStampName);
        edtCount = findViewById(R.id.edtCount);

        tvType = findViewById(R.id.tvType);

        btnCreateStamp = findViewById(R.id.btnCreateStamp);

        ibQuestion = findViewById(R.id.ibQuestion);

        llAddView = findViewById(R.id.llAddView);
    }

}