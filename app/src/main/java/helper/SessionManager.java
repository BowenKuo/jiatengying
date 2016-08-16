package helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    //for the logcat
    private static java.lang.String TAG = SessionManager.class.getSimpleName();
    //Shared Preference
    SharedPreferences pref;
    Editor editor;
    Context _context;

    //shared pref mode
    final int PRIVATE_MODE = 0;
    //Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";


    public SessionManager (Context context){
        this._context = context;
        pref = this._context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void setLogin (boolean isLoggedIn){
        //putBoolean(String key, Boolean value)
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        //commit change
        editor.commit();
        //Store action to log
        Log.d(TAG,"User log in session is modified");
    }

    public boolean isLoggedIn(){
        //getBoolean(String key, boolean default_value)
        return pref.getBoolean(KEY_IS_LOGGEDIN,false);
    }
}
