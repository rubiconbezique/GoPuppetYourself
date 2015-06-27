package com.nakedape.gopuppetyourself;

/**
 * Created by Nathan on 6/25/2015.
 */
public class KeyFrame {
    public final static int START = 1000;
    public final static int MOVEMENT = 1001;
    public final static int CLOSE_MOUTH = 1002;
    public final static int OPEN_MOUTH_NARROW = 1003;
    public final static int OPEN_MOUTH_MED = 1004;
    public final static int OPEN_MOUTH_WIDE = 1005;
    public final static int END = 9000;

    private final static int UNSET = -1;

    public int puppetId;
    public int eventType;
    public long time;
    public int x = UNSET, y = UNSET;
    public KeyFrame(){

    }
    public KeyFrame(long time, int eventType){
        this.time = time;
        this.eventType = eventType;
    }
    public KeyFrame(long time, int puppetId, int eventType){
        this.time = time;
        this.puppetId = puppetId;
        this.eventType = eventType;
    }
    public KeyFrame(long time, int puppetId, int eventType, int x, int y){
        this.time = time;
        this.puppetId = puppetId;
        this.eventType = eventType;
        this.x = x;
        this.y = y;
    }
}