package com.example.kevin.myapplication;

/**
 * Created by kevin on 18-2-10.
 */
import android.widget.Toast;

import com.jcraft.jsch.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SSHCONNECTION {
    private  String user="kevin";
    private  String password="shikaijie123456";
    private  String host="localhost";
    private  String port="22";
    private  JSch jsch=null;
    private  Session session=null;
    private  Channel channel=null;
    private ArrayList<String> msg=null;
    private InputStream inputStream=null;
    private OutputStream outputStream=null;

    void init(String user,String password,String host,String port){
        this.user=user;
        this.password=password;
        this.host=host;
        this.port=port;
        this.msg=new ArrayList<>() ;
        jsch=new JSch();
    }

    /**
     * init a new session throught exist JSch object
     * @return
     */
    boolean newSession() {
        try {
            session = jsch.getSession(user,host,Integer.valueOf(port));
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking","no");
            // username and password will be given via UserInfo interface.
            session.setUserInfo(new MyUserInfo());
            session.connect();
        } catch (Exception e) {
            return false;
        }
        return  true;
    }

    /**
     * disconnect a session
     * @return
     */
     private boolean disconnectSession() {
        try {
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
        return  true;
    }

    /**
     * test whether the connection is effective
     * @return
     */
    boolean testLegal() {
        if(newSession()) {
            disconnectSession();
            return true;
        }
        return false;
    }

    /**
     * init an interactive shell
     * @return
     */
    public void shellInit(){
        try {
            newSession();
            channel=session.openChannel("shell");
            channel.connect();
            inputStream=channel.getInputStream();
            outputStream=channel.getOutputStream();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * end a shell
     */
    public void shellEnd() {
        try {
            inputStream.close();
            outputStream.close();
            channel.disconnect();
            session.disconnect();
            msg.clear();
        }catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     *execute given command in a simulation shell
     * @param command
     */
    public  ArrayList<String> shellCommand(String command) {
        try {
            outputStream.write((command+"\r\n").getBytes());
            outputStream.write("exit\r\n".getBytes());
            outputStream.flush();
            byte[] tmp=new byte[1024];
            while(true) {
                while (inputStream.available() > 0) {
                    int i = inputStream.read(tmp,0,1024);
                    if (i < 0)
                        break;
                    msg.add(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (inputStream.available() > 0) continue;
                    msg.add("exit-status:"+channel.getExitStatus());
                    break;
                }
            }
        }catch (Exception e) {
            System.out.println(e);
        }
        return msg;
    }

    /**
     * control a channel connection with a specified command
     * non-interactive mode
     * @param command
     * @return
     */
    public  ArrayList<String> connect(String command) {
        try {
            newSession();
            msg.clear();
            dealOrder(session,command);
            disconnectSession();

        } catch (Exception e ){
            System.out.println(e);
        }
        return msg;
    }

    /**
     * connect a new channel,send command and receive message
     * @param session
     * @param command
     */
    private  void dealOrder(Session session, String command) {
        try {
            channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            InputStream in=channel.getInputStream();
            channel.connect();

            byte[] tmp=new byte[1024];
            while(true) {
                while(in.available()>0) {
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    msg.add(new String(tmp,0,i));
                    System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()) {
                    if(in.available()>0) continue;
                    msg.add("exit-status: "+channel.getExitStatus());
                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception e) {
                    System.out.println(e);
                }
            }
            channel.disconnect();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
     * implement of UserInfo
     */
    private static class MyUserInfo implements UserInfo{
        @Override
        public String getPassphrase() {
            System.out.println("getPassphrase");
            return null;
        }
        @Override
        public String getPassword() {
            System.out.println("getPassword");
            return null;
        }
        @Override
        public boolean promptPassword(String s) {
            System.out.println("promptPassword:"+s);
            return false;
        }
        @Override
        public boolean promptPassphrase(String s) {
            System.out.println("promptPassphrase:"+s);
            return false;
        }
        @Override
        public boolean promptYesNo(String s) {
            System.out.println("promptYesNo:"+s);
            return true;//notice here!
        }
        @Override
        public void showMessage(String s) {
            System.out.println("showMessage:"+s);
        }
    }
}
