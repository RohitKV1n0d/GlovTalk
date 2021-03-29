package com.example.glovetalk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import javax.xml.parsers.FactoryConfigurationError;


public class Utilities {

    public String END_TOKEN="<END>";
    public String CONTINUE_TOKEN="<CONTINUE>";
    public String NAME_TOKEN = "<name>";

    public String IP=null;
    public int PORT= 0;
    public String user_name = null;

    public UUID blt_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public String blt_address = "00:18:E5:04:4F:C8";

    public Flag server_connected=new Flag(false);
    public Flag blt_connected=new Flag(false);

    public Flag run_predict=new Flag(true);

    public Flag predict_running=new Flag(false);
    public Flag train_running=new Flag(false);
    public Flag reset_running=new Flag(false);

    private Server server=null;
    private BltThread blt_thread=null;

    private Flag run_blt=new Flag(true);

    private Turn turn_none=new Turn(-1);
    private Turn turn_blt=new Turn(0);
    private Turn turn_read_blt=new Turn(1);

    private Turn turn=turn_none;

    private String message=null;
    private String blt_message=null;
    private String blt_data=null;




    public void send(String data){
        message=data;
        server.send();
    }

    public String recv(){
        server.recv();
        return message;
    }

    public String blt_recv(){
        turn.setTurnVal(turn_blt.getTurnVal());
        while (turn.getTurnVal()!=turn_read_blt.getTurnVal());
        turn.setTurnVal(turn_none.getTurnVal());
        return blt_message;
    }

    public boolean connectServer(){

        blt_thread=new BltThread();
        blt_thread.start();
        server=new Server();

        if(server_connected.getFlag() && blt_connected.getFlag())
            return true;
        else
            return false;
    }

    public void disconnectServer(){
        server.disconnect();
        run_blt.setFlag(false);
    }




    private class Turn{
        int turn;
        Turn(int turnVal){
            setTurnVal(turnVal);
        }
        public void setTurnVal(int turnVal){
            synchronized (this){
                this.turn=turnVal;
            }
        }
        public int getTurnVal(){
            synchronized (this){
                return this.turn;
            }
        }
    }

    public class Flag{
        boolean flag;
        Flag(boolean flagStat){
            setFlag(flagStat);
        }
        public void setFlag(boolean flagStat){
            synchronized (this){
                this.flag=flagStat;
            }
        }

        public boolean getFlag(){
            synchronized (this){
                return this.flag;
            }
        }
    }


    private class Server{

        private Socket socket=null;

        private PrintWriter dout;
        private BufferedReader din;

        Server(){
            server_connected.setFlag(false);
            try {
                socket=new Socket(IP,PORT);
                dout=new PrintWriter(socket.getOutputStream());
                din=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                server_connected.setFlag(true);
            } catch (IOException e) {
                e.printStackTrace();
                server_connected.setFlag(false);
            }
        }

        public void send(){
            dout.write(message);
            dout.flush();
        }
        public void recv(){
            try {
                message=din.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void disconnect(){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket=null;
            server_connected.setFlag(false);
        }

    }

    private class BltThread extends Thread{
        private BluetoothSocket btSocket;
        private BluetoothAdapter btAdapter;
        private BluetoothDevice hc05;

        private BufferedReader br;

        BltThread(){
            blt_connected.setFlag(false);
            try {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                hc05 = btAdapter.getRemoteDevice(blt_address);

                btSocket = hc05.createRfcommSocketToServiceRecord(blt_UUID);
                btSocket.connect();
                br = new BufferedReader(new InputStreamReader( btSocket.getInputStream()));

                blt_connected.setFlag(true);
            } catch (IOException e) {
                e.printStackTrace();
                blt_connected.setFlag(false);
            }

            run_blt.setFlag(true);
        }

        @Override
        public void run() {
            super.run();
            while (run_blt.getFlag() && blt_connected.getFlag()){
                if(turn.getTurnVal()==turn_blt.getTurnVal()){
                    try {

                        blt_message = br.readLine();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    turn.setTurnVal(turn_read_blt.getTurnVal());
                }
            }
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            blt_connected.setFlag(false);

        }
    }
}
