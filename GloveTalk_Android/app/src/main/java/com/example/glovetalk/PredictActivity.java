package com.example.glovetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class PredictActivity extends AppCompatActivity {

    Utilities utilities=MainActivity.utilities;

    private TextToSpeech tts=null;
    private boolean tts_available=false;

    private PredictThread predictThread;

    private ToastHandler toastHandler=new ToastHandler();

    private int FAILED=0;
    private int CN_ERROR=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);

        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result=tts.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                        System.out.println("Lang not supported!");
                    }else {
                        tts_available=true;
                    }
                }else {
                    System.out.println("Error tts");
                }
            }
        });

        Button bttn_start=(Button)findViewById(R.id.bttn_start);
        Button bttn_stop=(Button)findViewById(R.id.bttn_stop);

        bttn_start.setEnabled(true);
        bttn_stop.setEnabled(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }

    public void speak(String text){
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    public void start(View view){
        Button bttn_start=(Button)findViewById(R.id.bttn_start);
        Button bttn_stop=(Button)findViewById(R.id.bttn_stop);

        if(!utilities.predict_running.getFlag() && tts_available){
            bttn_start.setEnabled(false);
            bttn_stop.setEnabled(true);

            utilities.run_predict.setFlag(true);
            predictThread=new PredictThread();
            predictThread.start();
        }
        if(!tts_available)
            makeToast("TTS not available!");
    }

    public void stop(View view){
        Button bttn_start=(Button)findViewById(R.id.bttn_start);
        Button bttn_stop=(Button)findViewById(R.id.bttn_stop);

        utilities.run_predict.setFlag(false);

        bttn_start.setEnabled(true);
        bttn_stop.setEnabled(false);
    }

    public void makeToast(String toast_string){
        Toast.makeText(this, toast_string, Toast.LENGTH_LONG).show();
    }

    public class PredictThread extends Thread{
        Message message;
        PredictThread(){
            utilities.predict_running.setFlag(true);
        }

        @Override
        public void run() {
            super.run();
            int status;

            String data;
            String prediction;

            if(utilities.connectServer()){
                utilities.send("PR");
                status=Integer.valueOf(utilities.recv());
                if(status==1){
                    while (utilities.run_predict.getFlag()){
                        data=utilities.blt_recv();
                        utilities.send(data);
                        prediction=utilities.recv();
                        if(prediction.equals(utilities.CONTINUE_TOKEN)) {
                            continue;
                        }
                        else{
                            if(prediction.contains(utilities.NAME_TOKEN)){
                                prediction = prediction.replace(utilities.NAME_TOKEN,utilities.user_name);
                            }
                            speak(prediction);
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    utilities.send(utilities.END_TOKEN);

                }else {
                    message=toastHandler.obtainMessage();;
                    message.what=FAILED;
                    toastHandler.sendMessage(message);
                }
                utilities.predict_running.setFlag(false);
            }else {
                message=toastHandler.obtainMessage();;
                message.what=CN_ERROR;
                toastHandler.sendMessage(message);
            }
            utilities.disconnectServer();
            utilities.predict_running.setFlag(false);
        }
    }
    public class ToastHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            String toast_string;

            if(msg.what==FAILED)
                toast_string="Failed!";
            else{
                toast_string="Connection error!";
            }
            makeToast(toast_string);
        }
    }
}