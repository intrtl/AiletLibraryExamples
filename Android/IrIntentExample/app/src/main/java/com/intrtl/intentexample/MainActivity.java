package com.intrtl.intentexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_RESULT_START_IR_REPORT = 1;
    private static final int ACTIVITY_RESULT_START_IR_VISIT = 2;
    private static final int ACTIVITY_RESULT_START_IR_SUMMARYREPORT = 3;
    private static final String IR_PACKAGE_NAME = "com.intelligenceretail.www.pilot";
    private static final String user = "";
    private static final String password = "";
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
                String visit_id = extras.getString("VISIT_ID", "-empty-");
                String external_visit_id = extras.getString("EXTERNAL_VISIT_ID", "-visit_id not set-");
                Toast.makeText(getBaseContext(), "Visit " + external_visit_id + " finished!", Toast.LENGTH_LONG).show();
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
                    intent.putExtra("method", "visit");
                    intent.putExtra("login", user);
                    intent.putExtra("password", password);
                    intent.putExtra("id", user_id);
                    intent.putExtra("visit_id", visit_id2);
                    intent.putExtra("store_id", store_id2);
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
            switch (requestCode) {

                case (ACTIVITY_RESULT_START_IR_REPORT):
                    if (data.getExtras() != null) {
                        try {
                            JSONObject json = new JSONObject(data.getExtras().getString("json"));
                            Toast.makeText(getBaseContext(), "ACTIVITY_RESULT_START_IR_REPORT " + json, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case (ACTIVITY_RESULT_START_IR_VISIT):
                    if (data.getExtras() != null) {
                        Toast.makeText(getBaseContext(), "ACTIVITY_RESULT_START_IR_VISIT " + data.getExtras().getString("error"), Toast.LENGTH_LONG).show();
                    }
                    break;

                case (ACTIVITY_RESULT_START_IR_SUMMARYREPORT):
                    if (data.getExtras() != null) {
                        Toast.makeText(getBaseContext(), "ACTIVITY_RESULT_START_IR_SUMMARYREPORT " + data.getExtras().getString("error"), Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } else {
            if (data.getExtras() != null)
                Toast.makeText(getBaseContext(), "ERROR_ACTIVITY_RESULT " + data.getExtras().getString("error"), Toast.LENGTH_LONG).show();
        }
    }

}
