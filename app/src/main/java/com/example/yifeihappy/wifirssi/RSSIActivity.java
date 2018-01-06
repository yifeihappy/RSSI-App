package com.example.yifeihappy.wifirssi;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RSSIActivity extends AppCompatActivity {
    private WifiInfo wifiInfo = null;       //获得的Wifi信息
    private WifiManager wifiManager = null; //Wifi管理器
    private Handler handler;
    private TextView Time_text;
    private TextView RSSI_text;
    private long start_time;
    private int level;

    String filename = "RSSI.txt";
    FileOutputStream fout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssi);
        start_time = System.currentTimeMillis();
        Time_text = (TextView)findViewById(R.id.timestamp_text);
        RSSI_text = (TextView)findViewById(R.id.RSSI_text);
        handler = new MyHandler();

        try {
            fout = openFileOutput(filename, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 获得WifiManager
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        // 使用定时器,每隔5秒获得一次信号强度值
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                wifiInfo = wifiManager.getConnectionInfo();
                //获得信号强度值
                level = wifiInfo.getRssi();
                long cur_time = System.currentTimeMillis();
                try {
                    fout.write((""+cur_time+","+level+"\r\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("T",""+(cur_time-start_time));
                bundle.putString("level",""+level);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        },1000,500);
    }

    class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String level = (String)bundle.get("level");
            String T = (String)bundle.get("T");
            Time_text.setText(T);
            RSSI_text.setText(level);


        }
    }
}
