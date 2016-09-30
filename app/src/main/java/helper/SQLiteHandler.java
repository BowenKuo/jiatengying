package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper{
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    /* All Static Value for Setting SQLite Database */
    //Database Version
    private static final short DATABASE_VERSION = 3;
    //Database Name
    private static final String DATABASE_NAME = "Session";
    //Login Table Name
    private static final String TABLE_USER = "user";
    //Login TABLE Column names
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_CREATED_AT = "created_at";
    //mCar Table Name
    private  static  final String TABLE_MCAR = "mcar";
    //mCar Table Column names
    private static final String CAR_BRAND = "brand";
    private static final String CAR_TYPE = "type";
    private static final String CAR_IMAGE_URL = "image_url";

    public SQLiteHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //Create TABLE
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //SQL syntax
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_UID + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_PHOTO + " BLOB,"
                + KEY_BIRTHDAY + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_MCAR_TABLE = "CREATE TABLE " + TABLE_MCAR + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + CAR_BRAND + " TEXT,"
                + CAR_TYPE + " TEXT,"
                + CAR_IMAGE_URL + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_MCAR_TABLE);

        Log.d(TAG, "Database tables created");
    }

    //Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int nerVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        //re-create
        onCreate(sqLiteDatabase);
    }

    //Store User details in the database
    public void addUser(String name, String email, String uid, String type, String phone, String photo, String birthday, String create_at){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //push into database
        values.put(KEY_NAME,name);
        values.put(KEY_EMAIL,email);
        values.put(KEY_UID,uid);
        values.put(KEY_TYPE,type);
        values.put(KEY_PHONE,phone);
        values.put(KEY_PHOTO,photo);
        values.put(KEY_BIRTHDAY,birthday);
        values.put(KEY_CREATED_AT,create_at);
        //Insert Row
        long id = db.insert(TABLE_USER,null,values);
        db.close();

        //Write into log
        Log.d(TAG, "New User has been added");
    }


    //Getting user data from database
    public HashMap<String,String> getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        String query = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        //Runs the provided SQL and returns a Cursor over the result set.
        Cursor cursor = db.rawQuery(query,null);

        //Move cursor to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0){
            user.put("_id",cursor.getString(0));
            user.put("name",cursor.getString(1));
            user.put("email",cursor.getString(2));
            user.put("mid",cursor.getString(3));
            user.put("type",cursor.getString(4));
            user.put("phone",cursor.getString(5));
//            user.put("photo",cursor.getBlob(6));
            user.put("birthday",cursor.getString(7));
            user.put("created_at",cursor.getString(8));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"Fetching user from Sqlite: " + user.toString());

        //return value
        return user;
    }

    public byte[] getPhoto(){
        String query = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        byte[] photo = null;
        if (cursor.moveToFirst())
            photo = cursor.getBlob(cursor.getColumnIndex("photo"));
        db.close();
        return photo;
    }

    //Re crate database Delete all tables and create them again
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void updateUserPhoto(ContentValues cv, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Update user photo
        db.update(TABLE_USER, cv, KEY_ID+"="+id, null);
        db.close();

        Log.d(TAG, "Update user photo in SQLite");
    }

    // -------------------- MCAR --------------------
    //Store User details in the database
    public void addMcar(String brand, String type, String image_url){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //push into database
        values.put(CAR_BRAND, brand);
        values.put(CAR_TYPE, type);
        values.put(CAR_IMAGE_URL, image_url);
        //Insert Row
        long id = db.insert(TABLE_MCAR, null, values);
        db.close();

        //Write into log
        Log.d(TAG, "New car has been added");
    }

    public HashMap<String,String> getMcarDetail(){
        HashMap<String,String> mcar = new HashMap<>();
        String query = "SELECT * FROM " + TABLE_MCAR;

        SQLiteDatabase db = this.getReadableDatabase();
        //Runs the provided SQL and returns a Cursor over the result set.
        Cursor cursor = db.rawQuery(query,null);

        //Move cursor to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0){
            mcar.put("_id",cursor.getString(0));
            mcar.put("brand",cursor.getString(1));
            mcar.put("type",cursor.getString(2));
            mcar.put("image_url",cursor.getString(3));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"Fetching mcar from Sqlite: " + mcar.toString());

        //return value
        return mcar;
    }
}
