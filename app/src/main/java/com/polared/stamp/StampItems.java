package com.polared.stamp;

import java.io.Serializable;

public class StampItems implements Serializable {
    private String item_id;
    private String item_name;
    private String item_position;
    private String item_image;
    private String item_status;

    public StampItems(){
        this.item_status = "available";
    }

    public StampItems(String item_id, String item_name, String item_position, String item_image, String item_status) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_position = item_position;
        this.item_image = item_image;
        this.item_status = item_status;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_position() {
        return item_position;
    }

    public void setItem_position(String item_position) {
        this.item_position = item_position;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getItem_status() {
        return item_status;
    }

    public void setItem_status(String item_status) {
        this.item_status = item_status;
    }
}
