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
import android.widget.TextView;
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

    //private static final int PORT = 8899;
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
                    serverSocket = new ServerSocket(PORTaux);
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
        final EditText edtIP = new EditText(mainActivity);
        edtIP.setText(/*"10.0.2.2"*/"192.168.1.4"); // emulator's default ip
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
                        Log.i("chess_game","onCancelListener");
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
                    socketGame = new Socket(strIP, Port);
                } catch (Exception e) {
                    socketGame = null;
                    Log.i("chess_game","Client Socket Exception");
                    mainActivity.finish();
                }
                if (socketGame == null) {
                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("chess_game","Socket is null");
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

                output.println(model.getUsername());
                output.flush();
                Log.i("chess_game","Send Username: " + model.getUsername());
                final String opponentName = input.readLine();
                model.setOpponentName(opponentName);
                Log.i("chess_game","Received Opponent Name: " + model.getOpponentName());
                procMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        if(isServer()) {
                            ((TextView) (mainActivity.findViewById(R.id.Player2))).setText(opponentName);
                        }else if(isClient()){
                            ((TextView)(mainActivity.findViewById(R.id.Player2))).setText(model.getUsername());
                            ((TextView) (mainActivity.findViewById(R.id.Player1))).setText(opponentName);
                        }
                    }
                });
                while (!Thread.currentThread().isInterrupted()) {
                    String read = input.readLine();
                    String read2 = input.readLine();
                    final int source = Integer.parseInt(read);
                    final int destination = Integer.parseInt(read2);
                    Log.i("chess_game", "Received: " + source +
                            " " + destination);
                    procMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            // PLAY!!!
                            // moveOtherPlayer(move);
                            Log.i("chess_game","MakeMove");
                            model.makeMove(source,destination);
                            mainActivity.renderGame();
                        }
                    });
                }
            } catch (Exception e) {
                procMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("chess_game","commThread Exception");
                        mainActivity.finish();
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
        Log.i("chess_game","onPause");
    }

    public void onResume() {
        if (mode.equals(GameOnlineMode.SERVER))
            server();
        else  // CLIENT
            clientDlg();
        Log.i("chess_game","onResume");
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

    public void makeMove(final int sourceTile,final int destinationTile){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                output.println(sourceTile);
                output.flush();
                output.println(destinationTile);
                output.flush();
                Log.i("chess_game","Sent: " + sourceTile + " " + destinationTile + ".");
            }
        });
        t.start();
    }
}
