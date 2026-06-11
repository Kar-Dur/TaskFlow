package com.taskflow.app.ui.add;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.taskflow.app.R;
import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;
import com.taskflow.app.notification.PomocnikPowiadomien;
import com.taskflow.app.ui.main.ModelWidoku;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DodajZadanieAktywnosc extends AppCompatActivity {

    public static final String KLUCZ_ID = "zadanie_id";
    public static final String KLUCZ_TYTUL = "zadanie_tytul";
    public static final String KLUCZ_OPIS = "zadanie_opis";
    public static final String KLUCZ_PRIORYTET = "zadanie_priorytet";
    public static final String KLUCZ_TERMIN = "zadanie_termin";

    private ModelWidoku modelWidoku;
    private TextInputEditText poleTytul, poleOpis;
    private AutoCompleteTextView wybieraniePriorytetu;
    private TextView tekstTermin;
    private Date wybranyTermin = null;
    private boolean trybEdycji = false;
    private long idEdytowanego = -1;

    private final SimpleDateFormat formatDaty = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_zadanie);
        setSupportActionBar(findViewById(R.id.pasek_dodaj));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        modelWidoku = new ViewModelProvider(this).get(ModelWidoku.class);

        poleTytul = findViewById(R.id.pole_tytul);
        poleOpis = findViewById(R.id.pole_opis);
        wybieraniePriorytetu = findViewById(R.id.wybor_priorytetu);
        tekstTermin = findViewById(R.id.tekst_wybrany_termin);
        Button przyciskData = findViewById(R.id.przycisk_data);
        Button przyciskZapisz = findViewById(R.id.przycisk_zapisz);
        Button przyciskWyczysc = findViewById(R.id.przycisk_wyczysc);

        String[] priorytety = {"Niski", "Średni", "Wysoki"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorytety);
        wybieraniePriorytetu.setAdapter(adapter);
        wybieraniePriorytetu.setText("Średni", false);

        sprawdzTrybEdycji();

        przyciskData.setOnClickListener(v -> pokazKalendarz());
        przyciskWyczysc.setOnClickListener(v -> {
            wybranyTermin = null;
            tekstTermin.setText(R.string.brak_terminu);
        });
        przyciskZapisz.setOnClickListener(v -> zapiszZadanie());
    }

    private void sprawdzTrybEdycji() {
        long id = getIntent().getLongExtra(KLUCZ_ID, -1);
        if (id != -1) {
            trybEdycji = true;
            idEdytowanego = id;
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edytuj zadanie");
            poleTytul.setText(getIntent().getStringExtra(KLUCZ_TYTUL));
            poleOpis.setText(getIntent().getStringExtra(KLUCZ_OPIS));
            int wartoscPriorytetu = getIntent().getIntExtra(KLUCZ_PRIORYTET, 2);
            wybieraniePriorytetu.setText(Priorytet.zWartosci(wartoscPriorytetu).pobierzEtykiete(), false);
            long terminMillis = getIntent().getLongExtra(KLUCZ_TERMIN, -1);
            if (terminMillis != -1) {
                wybranyTermin = new Date(terminMillis);
                tekstTermin.setText(formatDaty.format(wybranyTermin));
            }
        }
    }

    private void pokazKalendarz() {
        Calendar cal = Calendar.getInstance();
        if (wybranyTermin != null) cal.setTime(wybranyTermin);
        new DatePickerDialog(this, (view, rok, miesiac, dzien) -> {
            Calendar wybrany = Calendar.getInstance();
            wybrany.set(rok, miesiac, dzien, 23, 59, 0);
            wybranyTermin = wybrany.getTime();
            tekstTermin.setText(formatDaty.format(wybranyTermin));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void zapiszZadanie() {
        String tytul = poleTytul.getText() != null ? poleTytul.getText().toString().trim() : "";
        if (tytul.isEmpty()) {
            poleTytul.setError("Tytul jest wymagany");
            return;
        }
        String opis = poleOpis.getText() != null ? poleOpis.getText().toString().trim() : "";
        Priorytet priorytet = pobierzPriorytet();

        Zadanie zadanie = new Zadanie();
        zadanie.setTytul(tytul);
        zadanie.setOpis(opis);
        zadanie.setPriorytet(priorytet);
        zadanie.setTermin(wybranyTermin);

        if (trybEdycji) {
            zadanie.setId(idEdytowanego);
        }

        modelWidoku.dodajZadanie(zadanie);

        if (!trybEdycji && wybranyTermin != null) {
            PomocnikPowiadomien.zaplanujPowiadomienie(this, zadanie);
        }

        Toast.makeText(this, trybEdycji ? "Zaktualizowano" : "Dodano zadanie", Toast.LENGTH_SHORT).show();
        finish();
    }

    private Priorytet pobierzPriorytet() {
        String tekst = wybieraniePriorytetu.getText().toString();
        switch (tekst) {
            case "Niski": return Priorytet.NISKI;
            case "Wysoki": return Priorytet.WYSOKI;
            default: return Priorytet.SREDNI;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
