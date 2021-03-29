package com.example.glovetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

public class ResetActivity extends AppCompatActivity {

    Utilities utilities=MainActivity.utilities;

    ToastHandler toastHandler=new ToastHandler();

    int FAILED=0;
    int SUCCESS=1;
    int CN_ERROR=3;

    ResetThread resetThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
    }

    public void reset(View view){
        if(!utilities.reset_running.getFlag()){
            resetThread=new ResetThread();
            resetThread.start();
        }
    }

    public void makeToast(String toast_string){
        Toast.makeText(this, toast_string, Toast.LENGTH_LONG).show();
    }

    public class ResetThread extends Thread{
        Message message;
        ResetThread(){
            utilities.reset_running.setFlag(true);
        }

        @Override
        public void run() {
            super.run();

            int status;
            if(utilities.connectServer()){
                utilities.send("RS");
                status=Integer.valueOf(utilities.recv());
                if(status==1){
                    message=toastHandler.obtainMessage();;
                    message.what=SUCCESS;
                    toastHandler.sendMessage(message);
                }else{
                    message=toastHandler.obtainMessage();;
                    message.what=FAILED;
                    toastHandler.sendMessage(message);
                }
            }else {
                message=toastHandler.obtainMessage();;
                message.what=CN_ERROR;
                toastHandler.sendMessage(message);
            }

            utilities.disconnectServer();

            utilities.reset_running.setFlag(false);
        }
    }

    public class ToastHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            String toast_string;

            if(msg.what==SUCCESS)
                toast_string="Success!";
            else if(msg.what==FAILED)
                toast_string="Failed!";
            else{
                toast_string="Connection error!";
            }
            makeToast(toast_string);
        }
    }

}