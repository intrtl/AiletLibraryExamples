package com.intrtl.intentexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_RESULT_START_IR_REPORT = 1;
    private static final int ACTIVITY_RESULT_START_IR_VISIT = 2;
    private static final int ACTIVITY_RESULT_START_IR_SUMMARYREPORT = 3;
    private static final String IR_PACKAGE_NAME = "com.intelligenceretail.www.pilot";
    private static final String user = "vsevolod.didkovskiy";
    private static final String password = "4421Vfrc0215";
    private static final String user_id = null;
    private static final String visit_id = "1";
    private static final String visit_id2 = "2";
    private static final String store_id = "123456789";
    private static final String store_id2 = "5555";
    private BroadcastReceiver shareShelfBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareShelfBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    try {
                        addlog("BROADCAST_SHARESHELF:" + extras.getString("json"));
                        JSONObject json = new JSONObject(extras.getString("json"));
                        Toast.makeText(getBaseContext(), "BROADCAST_SHARESHELF " + json.getString("status"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        this.registerReceiver(
                shareShelfBroadcast,
                new IntentFilter("IR_BROADCAST_SHARESHELF"));

        findViewById(R.id.btVisit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getPackageManager().getLaunchIntentForPackage(IR_PACKAGE_NAME);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_RUN);
                    intent.setFlags(0);
                    intent.putExtra("method", "visit");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("id", user_id);
                    intent.putExtra("visit_id", visit_id);
                    intent.putExtra("store_id", store_id);
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_VISIT);
                }
            }
        });

        findViewById(R.id.btVisit2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getPackageManager().getLaunchIntentForPackage(IR_PACKAGE_NAME);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_RUN);
                    intent.setFlags(0);
                    intent.putExtra("method", "sync");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("id", user_id);
                    intent.putExtra("visit_id", visit_id);
                    intent.putExtra("store_id", store_id);
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_VISIT);
                }
            }
        });

        findViewById(R.id.btReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getPackageManager().getLaunchIntentForPackage(IR_PACKAGE_NAME);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_RUN);
                    intent.setFlags(0);
                    intent.putExtra("method", "report");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("id", user_id);
                    intent.putExtra("visit_id", visit_id);
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_REPORT);
                }
            }
        });

        findViewById(R.id.btReport2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getPackageManager().getLaunchIntentForPackage(IR_PACKAGE_NAME);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_RUN);
                    intent.setFlags(0);
                    intent.putExtra("method", "report");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("id", user_id);
                    intent.putExtra("visit_id", visit_id2);
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_REPORT);
                }
            }
        });

        findViewById(R.id.btSummaryReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getPackageManager().getLaunchIntentForPackage(IR_PACKAGE_NAME);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_RUN);
                    intent.setFlags(0);
                    intent.putExtra("method", "summaryReport");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("id", user_id);
                    intent.putExtra("visit_id", visit_id);
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_SUMMARYREPORT);
                }
            }
        });

        findViewById(R.id.btSummaryReport2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = getPackageManager().getLaunchIntentForPackage(IR_PACKAGE_NAME);
                if (intent != null) {
                    intent.setAction(Intent.ACTION_RUN);
                    intent.setFlags(0);
                    intent.putExtra("method", "summaryReport");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("id", user_id);
                    intent.putExtra("visit_id", visit_id2);
                    startActivityForResult(intent, ACTIVITY_RESULT_START_IR_SUMMARYREPORT);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String mode = "";
            switch (requestCode) {

                case (ACTIVITY_RESULT_START_IR_REPORT):
                    mode = "reports";
                    break;

                case (ACTIVITY_RESULT_START_IR_VISIT):
                    mode = "visit";
                    break;

                case (ACTIVITY_RESULT_START_IR_SUMMARYREPORT):
                    mode = "summaryReport";
                    break;
            }

            if (data.getExtras() != null) {
                try {
                    addlog(data.getExtras().getString("json"));
                    JSONObject json = new JSONObject(data.getExtras().getString("json"));
                    Toast.makeText(getBaseContext(), mode + " " + json.getString("status"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (data.getExtras() != null)
                Toast.makeText(getBaseContext(), "ERROR_ACTIVITY_RESULT " + data.getExtras().getString("error"), Toast.LENGTH_LONG).show();
        }
    }

    private void addlog(String text){
        EditText logEditText = findViewById(R.id.logEditText);
        logEditText.setText(text);

    }
}
