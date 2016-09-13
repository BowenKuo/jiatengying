package org.onlineservice.rand.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class CarInfo extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    //Variables
    private ImageView clearHistory, carPicture;
    private TextView carStatus;
    private ListView listView;

    //Private methods
    private void initialize(){
        carPicture = (ImageView) (getActivity()).findViewById(R.id.carPicture);
        clearHistory = (ImageView) getActivity().findViewById(R.id.clearHistory);
        carStatus = (TextView) getActivity().findViewById(R.id.carStatus);
        listView = (ListView) getActivity().findViewById(R.id.troubleCodesHistory);

        carPicture.setOnClickListener(setCarPictureListener());
        clearHistory.setOnClickListener(setClearHistoryListener());
    }

    // CarPictureOnclickListener
    private View.OnClickListener setCarPictureListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO  Change Car Avatar
            }
        };
    }

    //ClearHistoryOnclickListener
    private View.OnClickListener setClearHistoryListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTroubleCode();
            }
        };
    }

    //Refresh UI
    private void loadUI(){
        //TODO  Get Obd2 trouble codes
    }

    //Clear Trouble codes
    private void clearTroubleCode(){
        //TODO Clear all trouble code history
    }

    //Public methods

    //Override methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_carinfo, container, false);
        initialize();
        return inflater.inflate(R.layout.activity_carinfo, container, false);
    }

    @Override
    public void onRefresh() {
        loadUI();
    }
}
