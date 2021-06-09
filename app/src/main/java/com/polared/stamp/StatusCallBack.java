package com.polared.stamp;

public interface StatusCallBack {
    public void stampUpdate(int stampPosition, int itemPosition, String status);
    public void stampDelete(int stampPosition);
    public void itemUpdate(int stampPosition, int itemPosition, String status);
}
