package com.example.rafa.chesse_board.model;

import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by henri on 12/2/2017.
 */
class Communication {

    boolean isClient;
    boolean isConnected;
    ServerSocket serverSocket;
    Socket clientSocket;

    ObjectOutputStream oos;
    ObjectInputStream ois;

    boolean toRead;
    Model input;

    public Communication(boolean isClient) {
        this.isClient = isClient;
        this.isConnected = false;
        this.toRead = false;
    }

    public void client(final String strIP, final int port){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("Raven","Connecting to the server " + strIP);
                    clientSocket = new Socket(strIP, port);

                } catch (IOException e) {
                    clientSocket = null;
                }
                if(clientSocket != null){
                    isConnected = true;
                    try {
                        oos = new ObjectOutputStream(clientSocket.getOutputStream());
                        ois = new ObjectInputStream(clientSocket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    while (isConnected){
                        try {
                            Model input = (Model) ois.readObject();
                            toRead = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static String getLocalIpAddress(){
        try{
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                en.hasMoreElements();){

                NetworkInterface netInterface = en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = netInterface.getInetAddresses();
                    enumIpAddr.hasMoreElements();){

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                        return inetAddress.getHostAddress();
                }
            }
        } catch (SocketException e) {
            Log.i("Raven","Could not get Local IP Address");
            e.printStackTrace();
        }
        return null;
    }
}
