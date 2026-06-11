package com.taskflow.app.domain.usecase;

import com.taskflow.app.domain.model.Zadanie;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SortowanieZadan {

    public enum TrybSortowania {
        WEDLUG_WYNIKU,
        WEDLUG_PRIORYTETU,
        WEDLUG_TERMINU,
        WEDLUG_DATY
    }

    public List<Zadanie> wykonaj(List<Zadanie> zadania, TrybSortowania tryb) {
        if (zadania == null) return new ArrayList<>();
        List<Zadanie> wynik = new ArrayList<>(zadania);

        switch (tryb) {
            case WEDLUG_WYNIKU:
                wynik.sort((a, b) -> Double.compare(obliczWynik(b), obliczWynik(a)));
                break;
            case WEDLUG_PRIORYTETU:
                wynik.sort((a, b) -> Integer.compare(b.getPriorytet().pobierzWartosc(), a.getPriorytet().pobierzWartosc()));
                break;
            case WEDLUG_TERMINU:
                wynik.sort((a, b) -> {
                    if (a.getTermin() == null && b.getTermin() == null) return 0;
                    if (a.getTermin() == null) return 1;
                    if (b.getTermin() == null) return -1;
                    return a.getTermin().compareTo(b.getTermin());
                });
                break;
            case WEDLUG_DATY:
                wynik.sort((a, b) -> b.getDataDodania().compareTo(a.getDataDodania()));
                break;
        }

        return wynik;
    }

    public double obliczWynik(Zadanie zadanie) {
        if (zadanie == null) return 0;
        int wartoscPriorytetu = zadanie.getPriorytet().pobierzWartosc();

        if (zadanie.getTermin() == null) {
            return wartoscPriorytetu * 1.0;
        }

        long teraz = System.currentTimeMillis();
        long czasTerminu = zadanie.getTermin().getTime();
        long dniOstalo = TimeUnit.MILLISECONDS.toDays(czasTerminu - teraz);

        double mnoznik = pobierzMnoznikPilnosci(dniOstalo);

        return wartoscPriorytetu * mnoznik;
    }

    public double pobierzMnoznikPilnosci(long dniOstalo) {
        if (dniOstalo <= 0) return 10.0;
        if (dniOstalo <= 1) return 7.0;
        if (dniOstalo <= 3) return 5.0;
        if (dniOstalo <= 7) return 3.0;
        return 1.0;
    }

    public boolean czyPrzeterminowane(Zadanie zadanie) {
        if (zadanie.getTermin() == null) return false;
        return zadanie.getTermin().before(new Date());
    }

}
