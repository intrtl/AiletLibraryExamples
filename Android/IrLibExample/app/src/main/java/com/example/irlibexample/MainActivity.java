package com.example.irlibexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.intrtl.lib.IntRtl;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver brShareShelf;
    private BroadcastReceiver brSession;
    private BroadcastReceiver br;
    private BroadcastReceiver brDebug;
    private IntRtl ir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ir = new IntRtl(getBaseContext());

        //Configure broadcast reciver
        brShareShelf = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String visit_id = extras.getString("VISIT_ID", "-empty-");
                String external_visit_id = extras.getString("EXTERNAL_VISIT_ID", "-empty-");
                log("brShareShelf\n");
            }
        };

        brSession = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String stats = extras.getString("VISIT_STATS", "-empty-");
                log("brSession\n");
                log("stats: " + stats + "\n");
            }
        };

        brDebug = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String debugInfo = extras.getString("DEBUG_INFO", "-empty-");

                log("brDebug!!!\n");
                log("debugInfo: " + debugInfo + "\n");
            }
        };

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String stats = extras.getString("VISIT_STATS", "-empty-");
                log("br\n");
                log("stats: " + stats + "\n");
            }
        };

        this.registerReceiver(brShareShelf, new IntentFilter("notificationID_SHARESHELF"));
        this.registerReceiver(brSession, new IntentFilter("notificationID_SESSION"));
        this.registerReceiver(brDebug, new IntentFilter("notificationID_DEBUG"));
        this.registerReceiver(br, new IntentFilter("notificationID"));

        findViewById(R.id.btStart).setOnClickListener(v -> {
            //Init IrLib
            clear();

            ir.setDevEnv(true);

            Thread startThread = new Thread(() -> {
                IntRtl.Results res = ir.init(
                        "test",
                        "test",
                        "notificationID"
                );

                if (res == IntRtl.Results.RESULT_OK) {
                    //Start IrLib camera mode (visit)
                    res = ir.start(
                            "123456789",
                            "testVisit5",
                            "1");
                }
            });
            startThread.start();
        });
    }

    public static void strictAccess() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void log(String msg){
        TextView logTextView = findViewById(R.id.logTextView);
        logTextView.append(msg);
        logTextView.append("----------------------------------\n");

        ScrollView logScrollView = findViewById(R.id.logScrollView);
        logScrollView.post(() -> logScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void clear() {
        TextView logTextView = findViewById(R.id.logTextView);
        logTextView.setText("");
    }
}

