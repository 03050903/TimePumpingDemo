package com.jusfoun.timepumpingdemo;

import java.io.Serializable;
import java.util.List;

/**
 * Description
 */
public class TimeModel implements Serializable {
    private List<TimeItemModel> data;

    public List<TimeItemModel> getData() {
        return data;
    }

    public void setData(List<TimeItemModel> data) {
        this.data = data;
    }
}
