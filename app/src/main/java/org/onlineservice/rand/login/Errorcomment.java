package org.onlineservice.rand.login;

/**
 * Created by leoGod on 2016/10/21.
 */

public class Errorcomment {
    private String mname;
    private String info;
    private String time;
    public Errorcomment(String mname,String info,String time) {
        this.mname = mname;
        this.info = info;
        this.time = time;
    }
    public String getmid(){
        return mname;
    }
    public void setmid(String mname){
        this.mname = mname;
    }
    public String getinfo(){
        return info;
    }
    public void setinfo(String info){
        this.info = info;
    }
    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time = time;
    }
}
