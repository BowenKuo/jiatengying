package org.onlineservice.rand.login;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by leoGod on 2016/8/17.
 */
public class Dateselect extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{
    //    the suffix arrays and array lists
    ArrayList<Integer> arrayList_st = new ArrayList<Integer>();
    int[] array_st = {1, 21, 31};
    ArrayList<Integer> arrayList_nd = new ArrayList<Integer>();
    int[] array_nd = {2, 22};
    ArrayList<Integer> arrayList_rd = new ArrayList<Integer>();
    int[] array_rd = {3, 23};

    String[] monthsArray = {"January", "February", "March", "April"
            , "May", "June", "July", "August", "September"
            , "October", "November", "December"};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        create suffix array lists
        for (int i = 0; i < array_st.length; i++) {
            arrayList_st.add(array_st[i]);
        }

        for (int i = 0; i < array_nd.length; i++) {
            arrayList_nd.add(array_nd[i]);
        }

        for (int i = 0; i < array_rd.length; i++) {
            arrayList_rd.add(array_rd[i]);
        }

//        get the calendar day, month, year
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

//        create the date picker
        DatePickerDialog myDatePicker =
                new DatePickerDialog(getActivity(), this, year, month, day);

        return myDatePicker;
    }

    //    gets the date set by the user
    @Override
    public void onDateSet(DatePicker view, int year,
                          int monthOfYear, int dayOfMonth) {
        String day;
      if(dayOfMonth<10){
          day="0"+dayOfMonth;
      }else {
          day=String.valueOf(dayOfMonth);
      }
        ((TextView) getActivity().findViewById(R.id.birthday)).setText(year+"-"+String.valueOf(monthOfYear)+"-"+day);
        ((TextView) getActivity().findViewById(R.id.birthday)).setTextColor(Color.GRAY);
    }

    //    gets the suffix for the day
    private String getSuffix(int dayOfMonth) {
        String suffix = null;
        if (arrayList_st.contains(dayOfMonth)) {
            suffix = "st";
        } else if (arrayList_nd.contains(dayOfMonth)) {
            suffix = "nd";
        } else if (arrayList_rd.contains(dayOfMonth)) {
            suffix = "rd";
        } else {
            suffix = "th";
        }
        return suffix;
    }



}
