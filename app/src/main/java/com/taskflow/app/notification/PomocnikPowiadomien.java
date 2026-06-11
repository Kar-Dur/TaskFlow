package com.taskflow.app.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.taskflow.app.domain.model.Zadanie;

public class PomocnikPowiadomien {

    public static final String ID_KANALU = "taskflow_kanal";
    public static final String NAZWA_KANALU = "TaskFlow Powiadomienia";
    public static final String KLUCZ_TYTUL = "tytul_zadania";
    public static final String KLUCZ_ID = "id_zadania";

    public static void stworzKanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel kanal = new NotificationChannel(
                    ID_KANALU,
                    NAZWA_KANALU,
                    NotificationManager.IMPORTANCE_HIGH
            );
            kanal.setDescription("Powiadomienia o nadchodzacych zadaniach");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(kanal);
        }
    }

    public static void zaplanujPowiadomienie(Context context, Zadanie zadanie) {
        if (zadanie.getTermin() == null) return;

        long czasAlarm = zadanie.getTermin().getTime() - 24 * 60 * 60 * 1000L;
        if (czasAlarm <= System.currentTimeMillis()) return;

        Intent intent = new Intent(context, OdbiorcaPowiadomien.class);
        intent.putExtra(KLUCZ_TYTUL, zadanie.getTytul());
        intent.putExtra(KLUCZ_ID, (int) zadanie.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) zadanie.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, czasAlarm, pendingIntent);
        }
    }

    public static void anulujPowiadomienie(Context context, long idZadania) {
        Intent intent = new Intent(context, OdbiorcaPowiadomien.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) idZadania,
                intent,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) alarmManager.cancel(pendingIntent);
        }
    }
}
