package com.polared.stamp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


public class StampAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String NONE = "-1";
    public static final String AVAILABLE ="available";
    public static final String USED = "used";
    private static final String DELETE = "delete";

    private int num = 0;

    private ArrayList<CreateStamp> stampList;
    private StatusCallBack statusCallBack;


    public StampAdapter(ArrayList<CreateStamp> stampList, StatusCallBack statusCallBack) {
        this.stampList = stampList;
        this.statusCallBack = statusCallBack;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Test", "onCreateViewHolder = " +num);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_stamp, parent, false);

        return new Stamp(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("Test", "onBindViewHolder = " + position);
        if(holder instanceof Stamp){
            Stamp stamp = (Stamp) holder;

                stamp.createView();
                stamp.setStampItem();
                stamp.tvStampName.setText(stampList.get(holder.getAdapterPosition()).getStamp_name());

        }
    }

    @Override
    public int getItemCount() {
        return stampList.size();
    }

    public class Stamp extends RecyclerView.ViewHolder{
        private LinearLayout llStampBackground;
        private TextView tvStampName;

        public Stamp(@NonNull View itemView) {
            super(itemView);
            Log.d("Test", "Stamp = "+ num);
            num++;

            llStampBackground = itemView.findViewById(R.id.llStampBackground);

            tvStampName = itemView.findViewById(R.id.tvStampName);

        }

        public void setStampItem(){

            int position = getAdapterPosition();

            for(int i = 1; i <= stampList.get(position).getStamp_item().size(); i++){

                ImageButton stamp = itemView.findViewById(i);

                String status = stampList.get(position).getStamp_item().get(i-1).getItem_status();
                String image = stampList.get(position).getStamp_item().get(i-1).getItem_image();

                if(!image.equals(NONE)){

                    Glide.with(itemView.getContext())
                            .load(Uri.parse(image))
                            .apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop())
                            .into(stamp);

                    stamp.setTag(i);

                    if(status.equals(AVAILABLE)){
                        stamp.setBackgroundResource(R.drawable.stamp_red);
                        stamp.setImageAlpha(180);
                    }else if(status.equals(DELETE)){
                        stamp.setBackgroundResource(R.drawable.check);
                        stamp.setImageAlpha(100);
                    }

                }else{
                    if(status.equals(AVAILABLE)){
                        stamp.setBackgroundResource(R.drawable.stamp_red);

                    }else if(status.equals(DELETE)){
                        stamp.setBackgroundResource(R.drawable.stamp_blue);
                    }

                }



                if(stampList.get(position).getStamp_status().equals(USED)){
                    llStampBackground.setAlpha(0.6f);
                    deleteStampEventHandler();
                }else{
                    llStampBackground.setAlpha(1.0f);
                }

            }


        }


        public void createView() {
            llStampBackground.removeAllViews();
            int stampPosition = getAdapterPosition();

            View layoutView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.add_stamp_layout, null, false);
            LinearLayout llLayout = layoutView.findViewById(R.id.llStampLayout);

            int total_count = Integer.parseInt(stampList.get(stampPosition).getStamp_total_count());



            for(int i = 1; i <= total_count; i++){

                final int itemPosition = i;

                View stampItemView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.add_stamp_item, null, false);

                ImageButton item = stampItemView.findViewById(R.id.ibItem);

                item.setId(i);

                item.setOnClickListener(v -> {
                    itemEventHandler(itemPosition-1);
                });


                llLayout.addView(stampItemView);

                if(i%5 == 0){

                    llStampBackground.addView(layoutView);
                    layoutView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.add_stamp_layout, null, false);
                    llLayout = layoutView.findViewById(R.id.llStampLayout);

                }

            }

            if(total_count%5 != 0){
                llStampBackground.addView(layoutView);
            }




        }// end of createView

        public void itemEventHandler(int itemPosition){


            int position = getAdapterPosition();
            if(stampList.get(position).getStamp_status().equals(USED)){
                return;
            }

            if(stampList.get(position).getStamp_item().get(itemPosition).getItem_status().equals(NONE)
                    || stampList.get(position).getStamp_item().get(itemPosition).getItem_status().equals(DELETE)){
                return ;
            }



            boolean hasStampItem = false;
            boolean hasAvailableStampItem = false;

            if(!stampList.get(position).getStamp_item().get(itemPosition).getItem_image().equals(NONE)){
                hasStampItem = true;

                if(stampList.get(position).getStamp_item().get(itemPosition).getItem_status().equals(USED)){
                    hasAvailableStampItem = true;
                }
            }




            setAlertDialog(hasStampItem, hasAvailableStampItem, itemPosition);

        }

        private void setAlertDialog(boolean hasStampItem, boolean hasAvailableStampItem, int itemPosition) {

            String itemName = stampList.get(getAdapterPosition()).getStamp_item().get(itemPosition).getItem_name();
            String message = itemName;

            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

            //스탬프 아이템 사용가능하면
            if(hasAvailableStampItem){
                message += " 스탬프 아이템을 사용하시겠습니까?";
            }else{
                message += " 스탬프를 적립하시겠습니까?";
            }

            final int position = itemPosition;

            builder.setMessage(message);
            builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ImageButton stamp = itemView.findViewById(position+1);

                    String status = null;
                    int stampPosition = getAdapterPosition();

                    if (hasAvailableStampItem){
                        stamp.setBackgroundResource(R.drawable.check);
                        stamp.setImageAlpha(100);
                        stampList.get(stampPosition).getStamp_item().get(itemPosition).setItem_status(DELETE);

                        if (stampList.get(stampPosition).getStamp_type().equals("Type1")
                            || !checkUnusedStamp()){
                            deleteAvailableItem();
                            usedStamp(itemPosition);
                            return ;

                        }else{
                            status = DELETE;
                        }

                    }else if(hasStampItem){
                        stamp.setImageAlpha(255);
                        stampList.get(stampPosition).getStamp_item().get(itemPosition).setItem_status(USED);
                        status = USED;
                    }else{
                        stamp.setBackgroundResource(R.drawable.stamp_blue);
                        stampList.get(stampPosition).getStamp_item().get(itemPosition).setItem_status(DELETE);
                        status = DELETE;
                    }

                    setStampStatus(itemPosition, status);



                    if(stampList.get(stampPosition).getStamp_item().size() > itemPosition+1 && !hasAvailableStampItem){
                        setNextStampStatus(itemPosition+1);
                    }else if(!checkUnusedStamp()){
                        deleteAvailableItem();
                        usedStamp(itemPosition);
                    }

                }
            });
            builder.setPositiveButton("아니요", null);
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        private void deleteAvailableItem() {
            int stampPosition = getAdapterPosition();
            for(int i = 0; i < stampList.get(stampPosition).getStamp_item().size(); i++){
                if(stampList.get(stampPosition).getStamp_item().get(i).getItem_status().equals(AVAILABLE)
                && stampList.get(stampPosition).getStamp_item().get(i).getItem_image().equals(NONE)){

                    statusCallBack.itemUpdate(stampPosition, i, DELETE);

                    itemView.findViewById(i+1).setBackgroundResource(R.drawable.stamp_gray);

                }
            }
        }

        private void setNextStampStatus(int itemPosition) {
            ImageButton stamp = itemView.findViewById(itemPosition+1);
            stamp.setBackgroundResource(R.drawable.stamp_red);

            stampList.get(getAdapterPosition()).getStamp_item().get(itemPosition).setItem_status(AVAILABLE);
            setStampStatus(itemPosition, AVAILABLE);

            if (!stampList.get(getAdapterPosition()).getStamp_item().get(itemPosition).getItem_image().equals(NONE)){
                stamp.setImageAlpha(180);
            }


        }

        private void setStampStatus(int itemPosition, String status){
            int stampPosition = getAdapterPosition();

            statusCallBack.itemUpdate(stampPosition, itemPosition, status);
        }


        public void deleteStampEventHandler() {

            for(int i = 1; i <= stampList.get(getAdapterPosition()).getStamp_item().size(); i++){
                itemView.findViewById(i).setClickable(false);
            }

            llStampBackground.setOnClickListener(v -> {
                if(!stampList.get(getAdapterPosition()).getStamp_status().equals(USED)){
                    return ;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setMessage(stampList.get(getAdapterPosition()).getStamp_name()+" 스탬프를 삭제 하시겠습니까?");
                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        statusCallBack.stampDelete(getAdapterPosition());

                    }
                });
                builder.setPositiveButton("아니요", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            });

        }

        private void usedStamp(int itemPosition){
            int stampPosition = getAdapterPosition();

            llStampBackground.setAlpha(0.6f);
            deleteStampEventHandler();

            statusCallBack.stampUpdate(stampPosition, itemPosition, USED);
        }

        private boolean checkUnusedStamp() {

            for(int i = 0; i < stampList.get(getAdapterPosition()).getStamp_item().size(); i++){
                if(!stampList.get(getAdapterPosition()).getStamp_item().get(i).getItem_image().equals(NONE)
                && !stampList.get(getAdapterPosition()).getStamp_item().get(i).getItem_status().equals(DELETE)) {
                    return true;
                }
            }

            return false;
        }

    }

}
