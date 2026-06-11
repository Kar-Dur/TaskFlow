package com.taskflow.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.taskflow.app.data.local.BazaDanych;
import com.taskflow.app.data.local.DaoZadan;
import com.taskflow.app.data.local.WpisZadania;
import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ImplRepozytoriumZadan implements RepozytoriumZadan {

    private final DaoZadan daoZadan;
    private final ExecutorService wykonawca;

    public ImplRepozytoriumZadan(Application application) {
        BazaDanych baza = BazaDanych.pobierzInstancje(application);
        this.daoZadan = baza.daoZadan();
        this.wykonawca = Executors.newSingleThreadExecutor();
    }

    @Override
    public void wstaw(Zadanie zadanie) {
        wykonawca.execute(() -> daoZadan.wstaw(naWpis(zadanie)));
    }

    @Override
    public void zaktualizuj(Zadanie zadanie) {
        wykonawca.execute(() -> daoZadan.zaktualizuj(naWpis(zadanie)));
    }

    @Override
    public void usun(Zadanie zadanie) {
        wykonawca.execute(() -> daoZadan.usun(naWpis(zadanie)));
    }

    @Override
    public LiveData<List<Zadanie>> wszystkieZadania() {
        return Transformations.map(daoZadan.wszystkie(), wpisy ->
                wpisy.stream().map(this::naDomain).collect(Collectors.toList()));
    }

    @Override
    public LiveData<List<Zadanie>> aktywneZadania() {
        return Transformations.map(daoZadan.aktywne(), wpisy ->
                wpisy.stream().map(this::naDomain).collect(Collectors.toList()));
    }

    private WpisZadania naWpis(Zadanie zadanie) {
        WpisZadania wpis = new WpisZadania();
        wpis.setId(zadanie.getId());
        wpis.setTytul(zadanie.getTytul());
        wpis.setOpis(zadanie.getOpis());
        wpis.setPriorytet(zadanie.getPriorytet().pobierzWartosc());
        wpis.setTermin(zadanie.getTermin() != null ? zadanie.getTermin().getTime() : null);
        wpis.setUkonczone(zadanie.isUkonczone());
        wpis.setDataDodania(zadanie.getDataDodania() != null ? zadanie.getDataDodania().getTime() : System.currentTimeMillis());
        return wpis;
    }

    private Zadanie naDomain(WpisZadania wpis) {
        return new Zadanie(
                wpis.getId(),
                wpis.getTytul(),
                wpis.getOpis(),
                Priorytet.zWartosci(wpis.getPriorytet()),
                wpis.getTermin() != null ? new Date(wpis.getTermin()) : null,
                wpis.isUkonczone(),
                new Date(wpis.getDataDodania())
        );
    }
}
