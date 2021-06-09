package com.polared.stamp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private ArrayList<CreateStamp> stampList = new ArrayList<>();

    private RecyclerView recyclerView;
    private StampAdapter adapter;


    private StatusCallBack statusCallBack;

    private int id = 0;

    private ActivityResultLauncher<Intent> mStartForResult2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("Test", "onActivityResult");
                    if(result.getResultCode() == Activity.RESULT_OK){
                        CreateStamp createStamp = (CreateStamp) result.getData().getSerializableExtra("CreateStamp");

                        stampList.add(createStamp);

                        adapter.notifyItemInserted(stampList.size()-1);

                        requestData();
                    }
                }
            });

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.stamp_list_actionbar);


        findViewByIdFunc();

        checkPreferences();

        setCallbackMethod();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StampAdapter(stampList, statusCallBack);
        recyclerView.setAdapter(adapter);






    }

    private void setCallbackMethod() {
        statusCallBack = new StatusCallBack() {
            @Override
            public void stampUpdate(int stampPosition, int itemPosition, String status) {
                stampList.get(stampPosition).setStamp_status(status);
                stampList.get(stampPosition).getStamp_item().get(itemPosition).setItem_status("delete");
                requestData();
            }

            @Override
            public void stampDelete(int stampPosition) {
                stampList.remove(stampPosition);
                adapter.notifyItemRemoved(stampPosition);
                requestData();
            }

            @Override
            public void itemUpdate(int stampPosition, int itemPosition, String status) {
                stampList.get(stampPosition).getStamp_item().get(itemPosition).setItem_status(status);
                requestData();
            }
        };
    }


    private void checkPreferences() {
        pref = getSharedPreferences("stamp", Activity.MODE_PRIVATE);
        editor = pref.edit();

        String data = pref.getString("Stamp", null);

        if(data == null){
            Intent intent = new Intent(this, ProduceStamp.class);
            mStartForResult2.launch(intent);
        }else{
            getStampData(data);
        }
    }



    private void findViewByIdFunc() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void getStampData(String data) {

        try {
            JSONArray jsonArray = new JSONArray(data);

            for(int i = 0; i < jsonArray.length(); i++){


                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String stamp_id = jsonObject.getString("stamp_id");
                String stamp_type = jsonObject.getString("stamp_type");
                String stamp_status = jsonObject.getString("stamp_status");
                String stamp_name = jsonObject.getString("stamp_name");
                String stamp_total_count = jsonObject.getString("stamp_total_count");

                ArrayList<StampItems> itemList = new ArrayList<>();
                JSONArray items = jsonObject.getJSONArray("stamp_item");

                    for(int j = 0; j < items.length(); j++){
                        JSONObject item = items.getJSONObject(j);

                        String item_id = item.getString("item_id");
                        String item_name = item.getString("item_name");
                        String item_position = item.getString("item_position");
                        String item_image = item.getString("item_image");
                        String item_status = item.getString("item_status");

                        itemList.add(new StampItems(item_id, item_name, item_position, item_image, item_status));
                    }

                stampList.add(new CreateStamp(stamp_id, stamp_type, stamp_status, stamp_name, stamp_total_count, itemList));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void requestData(){
        String stamp = null;

        if(stampList.size() != 0){
            Gson gson = new Gson();
            stamp = gson.toJson(stampList);

            pref = getSharedPreferences("stamp", Activity.MODE_PRIVATE);
            editor = pref.edit();


        }

        editor.putString("Stamp", stamp);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, ProduceStamp.class);

        if(stampList.size() != 0) {
            id = Integer.parseInt(stampList.get(stampList.size() - 1).getStamp_id());
            intent.putExtra("stamp_id", id);
        }

        mStartForResult2.launch(intent);
        return super.onOptionsItemSelected(item);
    }



}