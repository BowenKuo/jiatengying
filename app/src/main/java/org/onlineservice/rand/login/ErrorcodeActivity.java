package org.onlineservice.rand.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by leoGod on 2016/10/20.
 */

public class ErrorcodeActivity extends AppCompatActivity {
    private String err_id;
    private String err_info;
    TextView txterrid;
    TextView txterrinfo;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errorcode);

        Bundle bundle = getIntent().getExtras();
        err_id      = bundle.getString("Errorcode");
        err_info    = bundle.getString("Errorcodeinfo");

        txterrid = (TextView) findViewById(R.id.ErrorCode);
        txterrinfo = (TextView) findViewById(R.id.ErrorInfo);
        txterrid.setText(err_id);
        txterrinfo.setText(err_info);

    }

}
