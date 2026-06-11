package com.taskflow.app.domain.usecase;

import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FiltrowanieZadan {

    public List<Zadanie> poPriorytecie(List<Zadanie> zadania, Priorytet priorytet) {
        if (zadania == null) return new ArrayList<>();
        if (priorytet == null) return new ArrayList<>(zadania);
        return zadania.stream()
                .filter(z -> z.getPriorytet() == priorytet)
                .collect(Collectors.toList());
    }

    public List<Zadanie> poStatusie(List<Zadanie> zadania, boolean ukonczone) {
        if (zadania == null) return new ArrayList<>();
        return zadania.stream()
                .filter(z -> z.isUkonczone() == ukonczone)
                .collect(Collectors.toList());
    }

    public List<Zadanie> poSłowie(List<Zadanie> zadania, String slowo) {
        if (zadania == null) return new ArrayList<>();
        if (slowo == null || slowo.trim().isEmpty()) return new ArrayList<>(zadania);
        String szukane = slowo.toLowerCase().trim();
        return zadania.stream()
                .filter(z -> {
                    boolean wTytule = z.getTytul() != null && z.getTytul().toLowerCase().contains(szukane);
                    boolean wOpisie = z.getOpis() != null && z.getOpis().toLowerCase().contains(szukane);
                    return wTytule || wOpisie;
                })
                .collect(Collectors.toList());
    }

    public List<Zadanie> filtrujPrzeterminowane(List<Zadanie> zadania) {
        if (zadania == null) return new ArrayList<>();
        long teraz = System.currentTimeMillis();
        return zadania.stream()
                .filter(z -> !z.isUkonczone() && z.getTermin() != null && z.getTermin().getTime() < teraz)
                .collect(Collectors.toList());
    }

    public List<Zadanie> wszystkie(List<Zadanie> zadania, Priorytet priorytet, Boolean ukonczone, String slowo) {
        List<Zadanie> wynik = new ArrayList<>(zadania);
        if (priorytet != null) wynik = poPriorytecie(wynik, priorytet);
        if (ukonczone != null) wynik = poStatusie(wynik, ukonczone);
        if (slowo != null && !slowo.isEmpty()) wynik = poSłowie(wynik, slowo);
        return wynik;
    }
}
