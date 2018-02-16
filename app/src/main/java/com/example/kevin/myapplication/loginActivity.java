package com.example.kevin.myapplication;
import java.lang.Thread;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class loginActivity extends AppCompatActivity {

    SSHCONNECTION sshconnection=new SSHCONNECTION();
    static volatile int flag=0;//test if the information is right
    static volatile int MODE=0;//choose different mode
    public class TestConnectionThread extends Thread{
        @Override
        public void run(){
            if(!sshconnection.testLegal()){
                flag=1;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssh_page);
        Button button=findViewById(R.id.connectBtn);
        Button shellBtn=findViewById(R.id.shellConnectBtn);
        final EditText edit_ip=findViewById(R.id.edit_ip);
        final EditText edit_port=findViewById(R.id.edit_port);
        final EditText edit_username=findViewById(R.id.edit_username);
        final EditText edit_password=findViewById(R.id.edit_password);


        //do connection test in a child thread.If fail,change flag's value to 1
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String ip=edit_ip.getText().toString();
                String port=edit_port.getText().toString();
                String username=edit_username.getText().toString();
                String password=edit_password.getText().toString();
                MODE=0;
                startActivity(ip,port,username,password);
            }
        });
        shellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip=edit_ip.getText().toString();
                String port=edit_port.getText().toString();
                String username=edit_username.getText().toString();
                String password=edit_password.getText().toString();
                MODE=1;
                startActivity(ip,port,username,password);
            }
        });

    }
    private void  startActivity(String ip,String port,String username,String password){
        sshconnection.init(username,password,ip,port);
        try{
            TestConnectionThread thread=new TestConnectionThread();
            thread.start();
            thread.join();
            //judge whether we should start the new activity
            if(flag==0) {
                Intent intent=null;
                if(MODE==0){
                    intent=new Intent(loginActivity.this,exacActivity.class);
                }else{
                    intent=new Intent(loginActivity.this,shellActivity.class);
                }
                intent.putExtra("ip",ip);
                intent.putExtra("username",username);
                intent.putExtra("port",port);
                intent.putExtra("password",password);
                startActivity(intent);
            }
            else{
                Toast.makeText(loginActivity.this,"连接失败，请重试",Toast.LENGTH_LONG).show();
                //change flag to original value
                flag=0;
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

}
