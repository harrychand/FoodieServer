package foodie.example.com.foodieserver.Service;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import foodie.example.com.foodieserver.Model.Order;
import foodie.example.com.foodieserver.OrderStatus;
import foodie.example.com.foodieserver.R;

public class ListenOrder extends Service implements ChildEventListener {
    FirebaseDatabase db;
    DatabaseReference orders;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        orders = db.getReference("Requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orders.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //Trigger here
        com.example.foodie.foodie.Model.Request request = dataSnapshot.getValue(com.example.foodie.foodie.Model.Request.class);
        if(request.getStatus().equals("0"))
            showNotification(dataSnapshot.getKey(),request);
    }

    private void showNotification(String key, com.example.foodie.foodie.Model.Request request) {
        Intent intent = new Intent(getBaseContext(), OrderStatus.class);
        PendingIntent contentIntent = PendingIntent.getActivities(getBaseContext(),0, new Intent[]{intent},0);
        //PendingIntent contentIntent = PendingIntent.getActivities(getBaseContext()),0, intent 0 );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("EDMTDEv")
                .setContentInfo("New Order")
                .setContentText("You have new order #"+key)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager)getBaseContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        //if you want to many notification show, you need to give unique ID for each Notification
        int randomInt = new Random().nextInt(999-1)+1;
        manager.notify(randomInt,builder.build());
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
