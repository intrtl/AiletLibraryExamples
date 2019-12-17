package com.intrtl.intentexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_RESULT_START_IR_REPORT = 1;
    private static final int ACTIVITY_RESULT_START_IR_CAMERA = 2;
    private static final String IR_PACKAGE_NAME = "com.intelligenceretail.www.pilot";
    private static final String IR_ACTIVITY_NAME = "com.intelligenceretail.www.pilot.intent.IntentIR";
    private static final String user = "";
    private static final String password = "12345678";
    private static final String visit_id = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getPackageManager().getLaunchIntentForPackage(IR_PACKAGE_NAME);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(0);
                    intent.putExtra("name", "report");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("visit_id", visit_id);
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_REPORT);
                }
            }
        });


        findViewById(R.id.btSummaryReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getPackageManager().getLaunchIntentForPackage(IR_PACKAGE_NAME);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(0);
                    intent.putExtra("name", "summaryReport");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("visit_id", visit_id);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case ACTIVITY_RESULT_START_IR_REPORT:
                    if (data.getExtras() != null){
                        try {
                            JSONObject json = new JSONObject(data.getExtras().getString("json"));
                            Log.i("", "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

            }
        } else {
            //Error
        }
    }

}
