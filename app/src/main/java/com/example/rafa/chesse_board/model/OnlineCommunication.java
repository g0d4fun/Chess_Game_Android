package com.example.rafa.chesse_board.model;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rafa.chesse_board.R;
import com.example.rafa.chesse_board.ui.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
class OnlineCommunication {

    private static final int PORT = 8899;
    private static final int PORTaux = 9988; // to test with emulators

    MainActivity mainActivity;
    Context context;
    Model model;

    ProgressDialog pd = null;

    GameOnlineMode mode;
    ServerSocket serverSocket;
    Socket socketGame;
    Handler procMsg = null;

    BufferedReader input;
    PrintWriter output;

    boolean toRead;

    public OnlineCommunication(GameOnlineMode mode,
                               MainActivity mainActivity, Model model) {
        this.mode = mode;

        procMsg = new Handler();

        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        this.model = model;
    }

    public void server() {
        String ip = getLocalIpAddress();
        pd = new ProgressDialog(mainActivity);
        pd.setMessage("Game Server" + "\n(IP: " + ip + ")");
        pd.setTitle("Create Game");
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mainActivity.finish();
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {

                    }
                    serverSocket = null;
                }
            }
        });
        pd.show();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(PORT);
                    socketGame = serverSocket.accept();
                    serverSocket.close();
                    serverSocket = null;
                    commThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    socketGame = null;
                }
                procMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        if (socketGame == null)
                            mainActivity.finish();
                    }
                });
            }
        });
        t.start();
    }

    void clientDlg() {
        final EditText edtIP = new EditText(context);
        edtIP.setText("10.0.2.2"); // emulator's default ip
        AlertDialog ad = new AlertDialog.Builder(mainActivity).setTitle("RPS Client")
                .setMessage("Server IP").setView(edtIP)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client(edtIP.getText().toString(), PORTaux); // to test with emulators: PORTaux);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mainActivity.finish();
                    }
                }).create();
        ad.show();
    }

    void client(final String strIP, final int Port) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("RPS", "Connecting to the server  " + strIP);
                    socketGame = new Socket(strIP, Port);
                } catch (Exception e) {
                    socketGame = null;
                }
                if (socketGame == null) {
                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.finish();
                        }
                    });
                    return;
                }
                commThread.start();
            }
        });
        t.start();
    }

    Thread commThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(
                        socketGame.getInputStream()));
                output = new PrintWriter(socketGame.getOutputStream());
                while (!Thread.currentThread().isInterrupted()) {
                    String read = input.readLine();
                    String read2 = input.readLine();
                    final int move = Integer.parseInt(read);
                    final int move2 = Integer.parseInt(read);
                    Log.d("RPS", "Received: " + move);
                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            // PLAY!!!
                            // moveOtherPlayer(move);
                            model.makeMove(move,move2);
                        }
                    });
                }
            } catch (Exception e) {
                procMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.finish();
                        Toast.makeText(context, "Game Finished", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    });

    public void onPause() {
        try {
            commThread.interrupt();
            if (socketGame != null)
                socketGame.close();
            if (output != null)
                output.close();
            if (input != null)
                input.close();
        } catch (Exception e) {
        }
        input = null;
        output = null;
        socketGame = null;
    }

    public void onResume() {
        if (mode.equals(GameOnlineMode.SERVER))
            server();
        else  // CLIENT
            clientDlg();
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {

                NetworkInterface netInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                        return inetAddress.getHostAddress();
                }
            }
        } catch (SocketException e) {
            Log.i("Raven", "Could not get Local IP Address");
            e.printStackTrace();
        }
        return null;
    }

    public boolean isClient() {
        if (mode.equals(GameOnlineMode.CLIENT))
            return true;
        return false;
    }

    public boolean isServer() {
        if (mode.equals(GameOnlineMode.SERVER))
            return true;
        return false;
    }

    public void makeMove(int sourceTile,int destinationTile){
        output.println(sourceTile);
        output.flush();
        output.println(destinationTile);
        output.flush();
    }
}