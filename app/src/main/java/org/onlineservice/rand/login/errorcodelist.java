package org.onlineservice.rand.login;

/**
 * Created by leoGod on 2016/10/19.
 */

public class errorcodelist {
    private String id;
    private String info;
    private String time;
    public errorcodelist(String id,String info,String time) {
        this.id = id;
        this.info = info;
        this.time = time;
    }
    public String getid(){
        return id;
    }
    public void setid(String id){
        this.id= id;
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
