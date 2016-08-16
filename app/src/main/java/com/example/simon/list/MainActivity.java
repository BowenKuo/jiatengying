package com.example.simon.list;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button list;
    private Button confirm;
    private ListView listView;
    private ListAdapter adapter;
    private String checked;
    private ImageView pf;
    private Drawable dpf;
    private Bitmap bmp;


    String[] values = new String[]{
            "水箱",
            "電瓶",
            "引擎",
            "剎車",
            "方向燈",
            "大燈",
            "前霧燈",
            "後霧燈",
            "連續行車時間",
            "油耗",
    };

    ArrayList<ScaleImage> images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        list = (Button) findViewById(R.id.List);
        confirm = (Button) findViewById(R.id.confirm);

        list.setOnClickListener(this);
        confirm.setOnClickListener(this);

        adapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_multiple_choice ,values);

        listView.setAdapter(adapter);
        listView.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l)
            {
                changeStatus(i);
            }
        });

        View.OnTouchListener infoOnTouchListener = new View.OnTouchListener() {
            private float x, y;    // 原本圖片存在的X,Y軸位置
            private int mx, my; // 圖片被拖曳的X ,Y軸距離長度

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Log.e("View", v.toString());
                switch (event.getAction()) {          //判斷觸控的動作

                    case MotionEvent.ACTION_DOWN:// 按下圖片時
                        x = event.getX();                  //觸控的X軸位置
                        y = event.getY();                  //觸控的Y軸位置

                    case MotionEvent.ACTION_MOVE:// 移動圖片時

                        //getX()：是獲取當前控件(View)的座標

                        //getRawX()：是獲取相對顯示螢幕左上角的座標
                        mx = (int) (event.getRawX() - x);
                        my = (int) (event.getRawY() - y);
                        v.layout(mx, my, mx + v.getWidth(), my + v.getHeight());
                        break;
                }
                Log.e("address", String.valueOf(mx) + "~~" + String.valueOf(my)); // 記錄目前位置
                return true;
            }
        };

        ScaleImage alittlewaterbottle = (ScaleImage)findViewById(R.id.alittlewaterbottle);
        alittlewaterbottle.setOnTouchListener(infoOnTouchListener);
        ScaleImage badligjtbottle = (ScaleImage)findViewById(R.id.badligjtbottle);
        badligjtbottle.setOnTouchListener(infoOnTouchListener);
        ScaleImage normalengine = (ScaleImage)findViewById(R.id.normalengine);
        normalengine.setOnTouchListener(infoOnTouchListener);
        ScaleImage ic_launcher = (ScaleImage)findViewById(R.id.ic_launcher);
        ic_launcher.setOnTouchListener(infoOnTouchListener);
        ScaleImage dlightbad = (ScaleImage)findViewById(R.id.dlightbad);
        dlightbad.setOnTouchListener(infoOnTouchListener);
        ScaleImage frontlightbad = (ScaleImage)findViewById(R.id.frontlightbad);
        frontlightbad.setOnTouchListener(infoOnTouchListener);
        ScaleImage fflightgood = (ScaleImage)findViewById(R.id.fflightgood);
        fflightgood.setOnTouchListener(infoOnTouchListener);
        ScaleImage blightgood = (ScaleImage)findViewById(R.id.blightgood);
        blightgood.setOnTouchListener(infoOnTouchListener);
        ScaleImage toolongtime = (ScaleImage)findViewById(R.id.toolongtime);
        toolongtime.setOnTouchListener(infoOnTouchListener);
        ScaleImage alittleoil = (ScaleImage)findViewById(R.id.alittleoil);
        alittleoil.setOnTouchListener(infoOnTouchListener);



        images = new ArrayList<ScaleImage>();
        images.add(alittlewaterbottle);
        images.add(badligjtbottle);
        images.add(normalengine);
        images.add(ic_launcher);
        images.add(dlightbad);
        images.add(frontlightbad);
        images.add(fflightgood);
        images.add(blightgood);
        images.add(toolongtime);
        images.add(alittleoil);


    }

    private void getResult()
    {
        checked = "";

        for (int a=0; a<images.size(); ++a)
        {
            if (images.get(a).isChosed)
            {
                images.get(a).setVisibility(View.VISIBLE);
                checked = checked + a + "\n";
            }
            else
                images.get(a).setVisibility(View.GONE);
        }

        //Toast.makeText(this, checked, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.List:
                listView.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
                for (int a=0; a<images.size(); ++a)
                {
//                    listView.setItemChecked(a, false);
                    images.get(a).setVisibility(View.GONE);
//                    images.get(a).isChosed = false;
                }
                break;

            case R.id.confirm:
                getResult();
                listView.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                break;

        }
    }

    private void changeStatus(int i)
    {
        if (images.get(i).isChosed)
            images.get(i).isChosed = false;
        else
            images.get(i).isChosed = true;
    }

}
