package com.taskflow.app.domain.model;

public enum Priorytet {
    NISKI(1, "Niski"),
    SREDNI(2, "Sredni"),
    WYSOKI(3, "Wysoki");

    private final int wartosc;
    private final String etykieta;

    Priorytet(int wartosc, String etykieta) {
        this.wartosc = wartosc;
        this.etykieta = etykieta;
    }

    public int pobierzWartosc() {
        return wartosc;
    }

    public String pobierzEtykiete() {
        return etykieta;
    }

    public static Priorytet zWartosci(int wartosc) {
        for (Priorytet p : values()) {
            if (p.wartosc == wartosc) return p;
        }
        return NISKI;
    }
}
