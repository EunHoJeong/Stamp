package com.polared.stamp;

import java.io.Serializable;
import java.util.ArrayList;

public class CreateStamp implements Serializable {
    private boolean hasView;
    private String stamp_id;
    private String stamp_type;
    private String stamp_status;
    private String stamp_name;
    private String stamp_total_count;
    private ArrayList<StampItems> stamp_item;

    public CreateStamp(String stamp_id, String stamp_type, String stamp_status, String stamp_name, String stamp_total_count, ArrayList<StampItems> stamp_item) {
        this.stamp_id = stamp_id;
        this.stamp_type = stamp_type;
        this.stamp_status = stamp_status;
        this.stamp_name = stamp_name;
        this.stamp_total_count = stamp_total_count;
        this.stamp_item = stamp_item;
    }

    public boolean hasView() {
        return hasView;
    }

    public void setHasView(boolean hasView) {
        this.hasView = hasView;
    }

    public String getStamp_id() {
        return stamp_id;
    }

    public String getStamp_type() {
        return stamp_type;
    }

    public String getStamp_status() {
        return stamp_status;
    }

    public void setStamp_status(String stamp_status) {
        this.stamp_status = stamp_status;
    }

    public String getStamp_name() {
        return stamp_name;
    }

    public String getStamp_total_count() {
        return stamp_total_count;
    }

    public ArrayList<StampItems> getStamp_item() {
        return stamp_item;
    }

}
