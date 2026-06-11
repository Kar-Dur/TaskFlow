package com.taskflow.app.domain.usecase;

import com.taskflow.app.data.repository.RepozytoriumZadan;
import com.taskflow.app.domain.model.Zadanie;

public class DodawanieZadania {

    private final RepozytoriumZadan repozytorium;

    public DodawanieZadania(RepozytoriumZadan repozytorium) {
        this.repozytorium = repozytorium;
    }

    public boolean wykonaj(Zadanie zadanie) {
        if (!czyPoprawne(zadanie)) return false;
        repozytorium.wstaw(zadanie);
        return true;
    }

    public boolean czyPoprawne(Zadanie zadanie) {
        return zadanie != null
                && zadanie.getTytul() != null
                && !zadanie.getTytul().trim().isEmpty();
    }
}
