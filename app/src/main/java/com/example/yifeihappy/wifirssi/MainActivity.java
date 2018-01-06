package com.example.yifeihappy.wifirssi;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Button RSSIButton;
    private Button SendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RSSIButton = (Button)findViewById(R.id.RSSI_butt);
        SendButton = (Button)findViewById(R.id.Send_butt);

        RSSIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rssiIntent = new Intent(MainActivity.this, RSSIActivity.class);
                startActivity(rssiIntent);
            }
        });
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(MainActivity.this, SendActivity.class);
                startActivity(sendIntent);
            }
        });

    }



}
