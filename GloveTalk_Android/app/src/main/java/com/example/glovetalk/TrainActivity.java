package com.example.glovetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TrainActivity extends AppCompatActivity {

    Utilities utilities=MainActivity.utilities;
    UIHandler uiHandler;

    String TOAST_KEY="toast";
    int TOAST=0;
    int UPDATEUI=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        uiHandler=new UIHandler();
    }

    public void train(View view){

        String word;

        EditText textWord=(EditText)findViewById(R.id.text_word);
        Button bttnTrain=(Button)findViewById(R.id.bttn_train_train);

        word=textWord.getText().toString();

        if(!utilities.train_running.getFlag() && word.length()>0){
            textWord.setEnabled(false);
            bttnTrain.setEnabled(false);

            TrainThread trainThread=new TrainThread(word);
            trainThread.start();
        }else {
            if(utilities.train_running.getFlag())
                makeToast("Training in progress!");
        }
    }

    public void makeToast(String toast_string){
        Toast.makeText(this, toast_string, Toast.LENGTH_LONG).show();
    }

    public void updateUI(){
        EditText textWord=(EditText)findViewById(R.id.text_word);
        Button bttnTrain=(Button)findViewById(R.id.bttn_train_train);

        textWord.setEnabled(true);
        bttnTrain.setEnabled(true);
    }

    public class TrainThread extends Thread{
        String word;
        Message message;

        TrainThread(String word){
            this.word=word;
            utilities.train_running.setFlag(true);
        }

        @Override
        public void run() {
            super.run();

            int status;
            int size;

            String data;

            if(utilities.connectServer()){
                utilities.send("TR");
                status=Integer.valueOf(utilities.recv());
                if(status==1){
                    utilities.send(this.word);
                    size=Integer.valueOf(utilities.recv());
                    for(int i=0;i<size;i++){
                        data=utilities.blt_recv();
                        utilities.send(data);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    utilities.send(utilities.END_TOKEN);

                    message=uiHandler.obtainMessage();;
                    Bundle bundle=new Bundle();
                    bundle.putString(TOAST_KEY,"Train completed!");
                    message.setData(bundle);
                    message.what=TOAST;
                    uiHandler.sendMessage(message);

                }else{

                    message=uiHandler.obtainMessage();;
                    Bundle bundle=new Bundle();
                    bundle.putString(TOAST_KEY,"Train failed!");
                    message.setData(bundle);
                    message.what=TOAST;
                    uiHandler.sendMessage(message);
                }


            }else {
                message=uiHandler.obtainMessage();;
                Bundle bundle=new Bundle();
                bundle.putString(TOAST_KEY,"Connection error!");
                message.setData(bundle);
                message.what=TOAST;
                uiHandler.sendMessage(message);
            }

            utilities.disconnectServer();

            utilities.train_running.setFlag(false);
            message=uiHandler.obtainMessage();
            message.what=UPDATEUI;
            uiHandler.sendMessage(message);
        }
    }

    public class UIHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==UPDATEUI){
                updateUI();
            }else if(msg.what==TOAST){

                Bundle bundle;
                String toast_string;

                bundle=msg.getData();
                toast_string=bundle.getString(TOAST_KEY);

                makeToast(toast_string);

            }
        }
    }

}