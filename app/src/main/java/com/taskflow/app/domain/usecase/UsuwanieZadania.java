package com.taskflow.app.domain.usecase;

import com.taskflow.app.data.repository.RepozytoriumZadan;
import com.taskflow.app.domain.model.Zadanie;

public class UsuwanieZadania {

    private final RepozytoriumZadan repozytorium;

    public UsuwanieZadania(RepozytoriumZadan repozytorium) {
        this.repozytorium = repozytorium;
    }

    public void wykonaj(Zadanie zadanie) {
        repozytorium.usun(zadanie);
    }

}
