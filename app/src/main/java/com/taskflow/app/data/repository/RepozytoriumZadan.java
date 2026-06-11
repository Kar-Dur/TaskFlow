package com.taskflow.app.data.repository;

import androidx.lifecycle.LiveData;

import com.taskflow.app.domain.model.Zadanie;

import java.util.List;

public interface RepozytoriumZadan {
    void wstaw(Zadanie zadanie);
    void zaktualizuj(Zadanie zadanie);
    void usun(Zadanie zadanie);
    LiveData<List<Zadanie>> wszystkieZadania();
    LiveData<List<Zadanie>> aktywneZadania();
}
