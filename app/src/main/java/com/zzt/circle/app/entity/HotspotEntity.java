package com.zzt.circle.app.entity;

/**
 * Created by zzt on 15-5-31.
 */
public class HotspotEntity {
    private int hotspotID;
    private int x;
    private int y;
    private int count;

    public HotspotEntity(int hotspotID, int x, int y, int count) {
        this.hotspotID = hotspotID;
        this.x = x;
        this.y = y;
        this.count = count;
    }

    public int getHotspotID() {
        return hotspotID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCount() {
        return count;
    }
}
