package com.sysu.bigmans.uchat;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

import common.src.ChatManager;
import common.src.UMessage;
import minet.src.ClientSocketsManager;
import minet.src.Minet;
import mirot.src.Mirot;
import mirot.src.ServerSocketsManager;

/**
 * Created by liyujie on 15/12/19.
 */
public class UApplication extends Application {
    ClientSocketsManager csm = ClientSocketsManager.getInstance();
    ServerSocketsManager ssm = ServerSocketsManager.getInstance();
    Mirot mr;

    public void startServer() {
        Handler hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ChatManager.SEND_MSG_TO_UI_THREAD) {
                    UMessage umsg = (UMessage)msg.obj;
                    Log.i("UAPPLICATION", "Received a msg as a Server: " + umsg.getContent());

                    Intent intent = new Intent();

                    if (umsg.getChatType().toLowerCase().equals("group-chat")) {
                        intent.setAction("com.sysu.bigmans.uchat.GROUP_MSG");
                    } else {
                        intent.setAction("com.sysu.bigmans.uchat.RECEIVE_MSG");
                    }

                    intent.putExtra("content", umsg.getContent());
                    intent.putExtra("sender", umsg.getSenderAddress());
                    intent.putExtra("file", umsg.getFile());
                    intent.putExtra("content-type", umsg.getContentType());
                    sendBroadcast(intent);
                } else if (msg.what == ChatManager.SEND_CONTACT_MSG_TO_UI_THREAD) {
                    Log.i("UAPPLICATION", "New contact: " + msg.obj.toString());
                    Intent intent = new Intent();
                    intent.setAction("com.sysu.bigmans.uchat.ADD_CONTACT_BROADCAST");
                    intent.putExtra("address", msg.obj.toString());
                    UApplication.this.sendBroadcast(intent);
                    Log.i("UAPPLICATION", "Broadcast sended!");

                }
            }
        };
        mr = new Mirot(this, hd);
        mr.start();
        Log.i("CHAT_ACTIVITY", "Started server");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startServer();
    }

    public void createClient(String ip_address) {
        Handler hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ChatManager.SEND_MSG_TO_UI_THREAD) {
                    UMessage umsg = (UMessage)msg.obj;
                    Log.i("UAPPLICATION", "Received a msg as a Client: " + umsg.getContent());
                    Intent intent = new Intent();
                    intent.setAction("com.sysu.bigmans.uchat.RECEIVE_MSG");
                    intent.putExtra("content", umsg.getContent());
                    intent.putExtra("file", umsg.getFile());
                    intent.putExtra("content-type", umsg.getContentType());
                    intent.putExtra("sender", umsg.getSenderAddress());
                    sendBroadcast(intent);
                }
            }
        };

        Minet mn = new Minet(this, ip_address, hd);
        mn.start();
    }

    public void createGroupClient(String ip_address) {
        Handler hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == ChatManager.SEND_MSG_TO_UI_THREAD) {
                    UMessage umsg = (UMessage)msg.obj;
                    Log.i("UAPPLICATION", "Received a msg as a Client: " + umsg.getContent());
                    Intent intent = new Intent();
                    intent.setAction("com.sysu.bigmans.uchat.GROUP_MSG");
                    intent.putExtra("content", umsg.getContent());
                    intent.putExtra("file", umsg.getFile());
                    intent.putExtra("content-type", umsg.getContentType());
                    intent.putExtra("sender", umsg.getSenderAddress());
                    sendBroadcast(intent);
                }
            }
        };

        Minet mn = new Minet(this, ip_address, hd);
        mn.start();
    }
}
