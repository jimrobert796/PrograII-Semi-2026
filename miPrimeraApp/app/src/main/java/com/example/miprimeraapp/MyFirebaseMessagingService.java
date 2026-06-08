package com.example.miprimeraapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyAndroidFCMservice";
    public static final String DISPLAY_MESSAGE_ACTION = "enviarMsg";
    private static final String ADMIN_CHANNEL_ID = "canal_mensajes";

    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        crearNotificacionPush(remoteMessage);
        sendNewMsgBroadcast(remoteMessage);
    }
    private void crearNotificacionPush(RemoteMessage remoteMessage) {

        // ✅ Activity destino al tocar la notificación
        Intent intent = new Intent(this, chats.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("msg",  remoteMessage.getData().get("msg"));
        intent.putExtra("to",   remoteMessage.getData().get("para"));
        intent.putExtra("from", remoteMessage.getData().get("de"));
        intent.putExtra("user", remoteMessage.getData().get("nombre")); // ✅ "nombre" según tu payload

        // ✅ FLAG_IMMUTABLE obligatorio en Android 12+
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String titulo = "Mensaje de " + remoteMessage.getData().get("nombre");
        String cuerpo = remoteMessage.getData().get("msg");

        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo != null ? titulo : "Nuevo mensaje")
                .setContentText(cuerpo != null ? cuerpo : "")
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(0, mNotificationBuilder.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = "miCanal";
        String adminChannelDescription = "Esta es mi cana de comunicacion";
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
    private void sendNewMsgBroadcast(RemoteMessage remoteMessage) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra("msg",  remoteMessage.getData().get("msg"));
        intent.putExtra("to",   remoteMessage.getData().get("para"));
        intent.putExtra("from", remoteMessage.getData().get("de"));
        intent.putExtra("user", remoteMessage.getData().get("nombre"));

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}