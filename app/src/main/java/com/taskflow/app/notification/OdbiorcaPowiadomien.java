package com.taskflow.app.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.taskflow.app.R;
import com.taskflow.app.ui.main.GlownaAktywnosc;

public class OdbiorcaPowiadomien extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String tytul = intent.getStringExtra(PomocnikPowiadomien.KLUCZ_TYTUL);
        int idZadania = intent.getIntExtra(PomocnikPowiadomien.KLUCZ_ID, 0);

        Intent glownyIntent = new Intent(context, GlownaAktywnosc.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, glownyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder budowniczy = new NotificationCompat.Builder(context, PomocnikPowiadomien.ID_KANALU)
                .setSmallIcon(R.drawable.ic_powiadomienie)
                .setContentTitle("Zbliża sie termin!")
                .setContentText("Zadanie \"" + tytul + "\" jest na jutro")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(idZadania, budowniczy.build());
    }
}
