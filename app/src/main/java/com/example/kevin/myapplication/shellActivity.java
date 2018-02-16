package com.example.kevin.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class shellActivity extends AppCompatActivity {

    SSHCONNECTION sshconnection=new SSHCONNECTION();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell);
        Intent intent=getIntent();
        final String ip=intent.getStringExtra("ip");
        final String port=intent.getStringExtra("port");
        final String username=intent.getStringExtra("username");
        final String password=intent.getStringExtra("password");

        final EditText editCommand=findViewById(R.id.shellCommand);
        Button send=findViewById(R.id.shellSendBtn);
        final TextView result=findViewById(R.id.shellresult);
        result.setMovementMethod(ScrollingMovementMethod.getInstance());
        Toast.makeText(shellActivity.this,"aaaaaaaaaaa",Toast.LENGTH_SHORT).show();
//        try{
//             Thread thread= new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    sshconnection.init(username,password,ip,port);
//                    sshconnection.shellInit();
//                }
//            });
//             thread.start();
//             thread.join();
//        }catch (Exception e){
//            System.out.println(e);
//        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String command=editCommand.getText().toString();
                try{
                    new Thread(new Runnable() {
                        @Override
                        public synchronized void  run() {
                            sshconnection.init(username,password,ip,port);
                            sshconnection.shellInit();
                            final ArrayList<String> msg=sshconnection.shellCommand(command);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(shellActivity.this,"连接远程客户端成功kkk!",Toast.LENGTH_LONG).show();
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
    @Override
    protected void onDestroy(){
        super.onDestroy();
        sshconnection.shellEnd();
    }
}
