package com.taskflow.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.taskflow.app.R;
import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;
import com.taskflow.app.domain.usecase.SortowanieZadan;
import com.taskflow.app.notification.PomocnikPowiadomien;
import com.taskflow.app.ui.add.DodajZadanieAktywnosc;

public class GlownaAktywnosc extends AppCompatActivity {

    private ModelWidoku modelWidoku;
    private AdapterZadan adapterZadan;
    private TextView tekstPusty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glowna);
        setSupportActionBar(findViewById(R.id.pasek_narzedziowy));

        PomocnikPowiadomien.stworzKanal(this);

        modelWidoku = new ViewModelProvider(this).get(ModelWidoku.class);

        inicjujListe();
        inicjujSzukanie();
        inicjujChipy();
        inicjujPrzycisk();
        obserwujModel();
    }

    private void inicjujListe() {
        RecyclerView lista = findViewById(R.id.lista_zadan);
        tekstPusty = findViewById(R.id.tekst_pusty);
        adapterZadan = new AdapterZadan(new AdapterZadan.NasłuchiwaczAkcji() {
            @Override
            public void przelaczUkonczone(Zadanie zadanie) {
                modelWidoku.przelaczUkonczone(zadanie);
            }

            @Override
            public void usunZadanie(Zadanie zadanie) {
                new AlertDialog.Builder(GlownaAktywnosc.this)
                        .setTitle("Usun zadanie")
                        .setMessage("Czy na pewno chcesz usunac \"" + zadanie.getTytul() + "\"?")
                        .setPositiveButton("Usun", (d, w) -> modelWidoku.usunZadanie(zadanie))
                        .setNegativeButton("Anuluj", null)
                        .show();
            }

            @Override
            public void edytujZadanie(Zadanie zadanie) {
                Intent intent = new Intent(GlownaAktywnosc.this, DodajZadanieAktywnosc.class);
                intent.putExtra(DodajZadanieAktywnosc.KLUCZ_ID, zadanie.getId());
                intent.putExtra(DodajZadanieAktywnosc.KLUCZ_TYTUL, zadanie.getTytul());
                intent.putExtra(DodajZadanieAktywnosc.KLUCZ_OPIS, zadanie.getOpis());
                intent.putExtra(DodajZadanieAktywnosc.KLUCZ_PRIORYTET, zadanie.getPriorytet().pobierzWartosc());
                intent.putExtra(DodajZadanieAktywnosc.KLUCZ_TERMIN, zadanie.getTermin() != null ? zadanie.getTermin().getTime() : -1L);
                startActivity(intent);
            }
        });
        lista.setAdapter(adapterZadan);
        lista.setLayoutManager(new LinearLayoutManager(this));
    }

    private void inicjujSzukanie() {
        EditText poleSzukaj = findViewById(R.id.pole_szukaj);
        poleSzukaj.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                modelWidoku.ustawSzukanie(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void inicjujChipy() {
        ChipGroup grupaPriorytety = findViewById(R.id.grupa_priorytety);
        grupaPriorytety.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                modelWidoku.ustawFiltrPriorytet(null);
                return;
            }
            int id = checkedIds.get(0);
            if (id == R.id.chip_niski) modelWidoku.ustawFiltrPriorytet(Priorytet.NISKI);
            else if (id == R.id.chip_sredni) modelWidoku.ustawFiltrPriorytet(Priorytet.SREDNI);
            else if (id == R.id.chip_wysoki) modelWidoku.ustawFiltrPriorytet(Priorytet.WYSOKI);
        });

        ChipGroup grupaSortowanie = findViewById(R.id.grupa_sortowanie);
        grupaSortowanie.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_priorytet_sort) modelWidoku.ustawTrybSortowania(SortowanieZadan.TrybSortowania.WEDLUG_PRIORYTETU);
            else if (id == R.id.chip_termin_sort) modelWidoku.ustawTrybSortowania(SortowanieZadan.TrybSortowania.WEDLUG_TERMINU);
            else if (id == R.id.chip_data_sort) modelWidoku.ustawTrybSortowania(SortowanieZadan.TrybSortowania.WEDLUG_DATY);
        });
    }

    private void inicjujPrzycisk() {
        FloatingActionButton przyciskDodaj = findViewById(R.id.przycisk_dodaj);
        przyciskDodaj.setOnClickListener(v -> startActivity(new Intent(this, DodajZadanieAktywnosc.class)));
    }

    private void obserwujModel() {
        modelWidoku.pobierzZadania().observe(this, zadania -> {
            adapterZadan.ustawZadania(zadania);
            tekstPusty.setVisibility(zadania.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_glowna, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.akcja_ukonczone) {
            item.setChecked(!item.isChecked());
            modelWidoku.ustawPokazUkonczone(item.isChecked());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
