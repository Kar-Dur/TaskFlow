package com.taskflow.app.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.taskflow.app.data.repository.ImplRepozytoriumZadan;
import com.taskflow.app.data.repository.RepozytoriumZadan;
import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;
import com.taskflow.app.domain.usecase.AktualizacjaZadania;
import com.taskflow.app.domain.usecase.DodawanieZadania;
import com.taskflow.app.domain.usecase.FiltrowanieZadan;
import com.taskflow.app.domain.usecase.PobieranieZadan;
import com.taskflow.app.domain.usecase.SortowanieZadan;
import com.taskflow.app.domain.usecase.UsuwanieZadania;

import java.util.ArrayList;
import java.util.List;

public class ModelWidoku extends AndroidViewModel {

    private final DodawanieZadania dodawanie;
    private final UsuwanieZadania usuwanie;
    private final AktualizacjaZadania aktualizacja;
    private final SortowanieZadan sortowanie;
    private final FiltrowanieZadan filtrowanie;

    private final MutableLiveData<SortowanieZadan.TrybSortowania> trybSortowania = new MutableLiveData<>(SortowanieZadan.TrybSortowania.WEDLUG_WYNIKU);
    private final MutableLiveData<Priorytet> filtrPriorytet = new MutableLiveData<>(null);
    private final MutableLiveData<String> szukanie = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> pokazUkonczone = new MutableLiveData<>(true);

    private final LiveData<List<Zadanie>> wszystkieZadania;
    private final MutableLiveData<List<Zadanie>> przetworzone = new MutableLiveData<>(new ArrayList<>());

    public ModelWidoku(@NonNull Application application) {
        super(application);
        RepozytoriumZadan repozytorium = new ImplRepozytoriumZadan(application);
        dodawanie = new DodawanieZadania(repozytorium);
        usuwanie = new UsuwanieZadania(repozytorium);
        aktualizacja = new AktualizacjaZadania(repozytorium);
        sortowanie = new SortowanieZadan();
        filtrowanie = new FiltrowanieZadan();
        wszystkieZadania = new PobieranieZadan(repozytorium).wykonaj();
        wszystkieZadania.observeForever(this::odswiez);
    }

    public ModelWidoku(@NonNull Application application, RepozytoriumZadan repozytorium) {
        super(application);
        dodawanie = new DodawanieZadania(repozytorium);
        usuwanie = new UsuwanieZadania(repozytorium);
        aktualizacja = new AktualizacjaZadania(repozytorium);
        sortowanie = new SortowanieZadan();
        filtrowanie = new FiltrowanieZadan();
        wszystkieZadania = new PobieranieZadan(repozytorium).wykonaj();
        wszystkieZadania.observeForever(this::odswiez);
    }

    public void dodajZadanie(Zadanie zadanie) {
        dodawanie.wykonaj(zadanie);
    }

    public void usunZadanie(Zadanie zadanie) {
        usuwanie.wykonaj(zadanie);
    }

    public void przelaczUkonczone(Zadanie zadanie) {
        zadanie.setUkonczone(!zadanie.isUkonczone());
        aktualizacja.wykonaj(zadanie);
    }

    public void ustawTrybSortowania(SortowanieZadan.TrybSortowania tryb) {
        trybSortowania.setValue(tryb);
        if (wszystkieZadania.getValue() != null) odswiez(wszystkieZadania.getValue());
    }

    public void ustawFiltrPriorytet(Priorytet priorytet) {
        filtrPriorytet.setValue(priorytet);
        if (wszystkieZadania.getValue() != null) odswiez(wszystkieZadania.getValue());
    }

    public void ustawSzukanie(String fraza) {
        szukanie.setValue(fraza);
        if (wszystkieZadania.getValue() != null) odswiez(wszystkieZadania.getValue());
    }

    public void ustawPokazUkonczone(boolean pokazuj) {
        pokazUkonczone.setValue(pokazuj);
        if (wszystkieZadania.getValue() != null) odswiez(wszystkieZadania.getValue());
    }

    private void odswiez(List<Zadanie> zadania) {
        if (zadania == null) {
            przetworzone.setValue(new ArrayList<>());
            return;
        }
        Boolean pokazComp = pokazUkonczone.getValue();
        List<Zadanie> przefiltrowane = filtrowanie.wszystkie(
                zadania,
                filtrPriorytet.getValue(),
                pokazComp != null && !pokazComp ? false : null,
                szukanie.getValue()
        );
        SortowanieZadan.TrybSortowania tryb = trybSortowania.getValue() != null
                ? trybSortowania.getValue()
                : SortowanieZadan.TrybSortowania.WEDLUG_WYNIKU;
        List<Zadanie> posortowane = sortowanie.wykonaj(przefiltrowane, tryb);
        przetworzone.setValue(posortowane);
    }

    public LiveData<List<Zadanie>> pobierzZadania() {
        return przetworzone;
    }

}
