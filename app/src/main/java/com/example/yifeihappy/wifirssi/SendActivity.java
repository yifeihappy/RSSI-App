package com.example.yifeihappy.wifirssi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendActivity extends AppCompatActivity {

    private EditText IPEdit;
    private Button SendButton;
    String filename = "RSSI.txt";
    private String HOST = null;
    private int PORT = 5000;
    private final int SEND_ERROR = 1;
    private final int SEND_SUCCESS = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private  Handler sendHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        sharedPreferences = getSharedPreferences("HOSTIP", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        HOST = sharedPreferences.getString("IP", "192.168.10.0");

        IPEdit = (EditText)findViewById(R.id.ip_edit);
        SendButton = (Button)findViewById(R.id.Send_butt);
        IPEdit.setText(HOST);

        sendHandler = new SendHandler();
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Hahahaha",Toast.LENGTH_SHORT).show();
                HOST = IPEdit.getText().toString();
                editor.putString("IP",HOST);
                editor.commit();
                String buffer = readData(filename);
                //Log.e("Test",buffer);
                new ClientThread(buffer).start();
                SendButton.setEnabled(false);
            }
        });


    }
    public String readData(String filename) {
        String res = "";
        FileInputStream fin = null;
        try {
            fin = openFileInput(filename);
            int length = fin.available();
            byte[] buffer = new  byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer,"UTF-8");
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("D","open failed");
            Toast.makeText(getApplicationContext(),"Open "+filename+"failed!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("D","length failed");
            Toast.makeText(getApplicationContext(),"Length "+filename+"failed!",Toast.LENGTH_SHORT).show();
        }
        return  res;
    }

    private class ClientThread extends Thread {
        String buffer;
        Socket socket;
        PrintStream output;
        ClientThread(String buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            socket = new Socket();
            Message msg = Message.obtain();
            msg.what = SEND_SUCCESS;
            try {
                socket.connect(new InetSocketAddress(HOST,PORT),5000);
                output = new PrintStream(socket.getOutputStream());
                output.println(buffer);
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("D"," e.printStackTrace");
                msg.what = SEND_ERROR;
            }
            Log.d("D","Before sendMessaga");
            sendHandler.sendMessage(msg);

        }
    }

    class SendHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_SUCCESS:
                    Log.d("D","SUCCESS");
                    Toast.makeText(SendActivity.this,"Send Success",Toast.LENGTH_SHORT).show();
                    break;
                case SEND_ERROR:
                    Log.d("D", "ERROR");
                    Toast.makeText(SendActivity.this,"Send failed",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
