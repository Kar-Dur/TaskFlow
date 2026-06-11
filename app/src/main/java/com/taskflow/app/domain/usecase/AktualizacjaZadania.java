package com.taskflow.app.domain.usecase;

import com.taskflow.app.data.repository.RepozytoriumZadan;
import com.taskflow.app.domain.model.Zadanie;

public class AktualizacjaZadania {

    private final RepozytoriumZadan repozytorium;

    public AktualizacjaZadania(RepozytoriumZadan repozytorium) {
        this.repozytorium = repozytorium;
    }

    public boolean wykonaj(Zadanie zadanie) {
        if (zadanie == null || zadanie.getTytul() == null || zadanie.getTytul().trim().isEmpty()) {
            return false;
        }
        repozytorium.zaktualizuj(zadanie);
        return true;
    }
}
