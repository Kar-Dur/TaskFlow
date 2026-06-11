package com.taskflow.app.domain.usecase;

import androidx.lifecycle.LiveData;

import com.taskflow.app.data.repository.RepozytoriumZadan;
import com.taskflow.app.domain.model.Zadanie;

import java.util.List;

public class PobieranieZadan {

    private final RepozytoriumZadan repozytorium;

    public PobieranieZadan(RepozytoriumZadan repozytorium) {
        this.repozytorium = repozytorium;
    }

    public LiveData<List<Zadanie>> wykonaj() {
        return repozytorium.wszystkieZadania();
    }

    public LiveData<List<Zadanie>> wykonajAktywne() {
        return repozytorium.aktywneZadania();
    }
}
