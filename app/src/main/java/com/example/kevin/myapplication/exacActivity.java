package com.example.kevin.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class exacActivity extends AppCompatActivity {

    SSHCONNECTION sshconnection=new SSHCONNECTION();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exac);
        Intent intent=getIntent();
        final String ip=intent.getStringExtra("ip");
        final String port=intent.getStringExtra("port");
        final String username=intent.getStringExtra("username");
        final String password=intent.getStringExtra("password");


        //Toast.makeText(getApplicationContext(),"ip:"+ip,Toast.LENGTH_SHORT);

        final EditText editCommand=findViewById(R.id.exac);
        Button send=findViewById(R.id.sendBtn);
        final TextView result=findViewById(R.id.result);

        Toast.makeText(exacActivity.this,"aaaaaaaaaaa",Toast.LENGTH_SHORT).show();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String command=editCommand.getText().toString();
                sshconnection.init(username,password,ip,port);
                try{
                    new Thread(new Runnable() {
                        @Override
                        public synchronized void  run() {
                            sshconnection.newSession();
                            final ArrayList<String>msg=sshconnection.connect(command);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(exacActivity.this,"连接远程客户端成功!",Toast.LENGTH_LONG).show();
                                    result.setText("");
                                    for(String i:msg){
                                        result.append(i);
                                    }
                                }
                            });

                        }
                    }).start();
                }catch (Exception e ){
                    e.printStackTrace();
                }
            }
        });

    }
}
