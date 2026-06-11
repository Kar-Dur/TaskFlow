package com.taskflow.app.domain.model;

import java.util.Date;

public class Zadanie {

    private long id;
    private String tytul;
    private String opis;
    private Priorytet priorytet;
    private Date termin;
    private boolean ukonczone;
    private Date dataDodania;

    public Zadanie() {
        this.dataDodania = new Date();
        this.ukonczone = false;
        this.priorytet = Priorytet.SREDNI;
    }

    public Zadanie(long id, String tytul, String opis, Priorytet priorytet, Date termin, boolean ukonczone, Date dataDodania) {
        this.id = id;
        this.tytul = tytul;
        this.opis = opis;
        this.priorytet = priorytet;
        this.termin = termin;
        this.ukonczone = ukonczone;
        this.dataDodania = dataDodania;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTytul() { return tytul; }
    public void setTytul(String tytul) { this.tytul = tytul; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public Priorytet getPriorytet() { return priorytet; }
    public void setPriorytet(Priorytet priorytet) { this.priorytet = priorytet; }

    public Date getTermin() { return termin; }
    public void setTermin(Date termin) { this.termin = termin; }

    public boolean isUkonczone() { return ukonczone; }
    public void setUkonczone(boolean ukonczone) { this.ukonczone = ukonczone; }

    public Date getDataDodania() { return dataDodania; }
    public void setDataDodania(Date dataDodania) { this.dataDodania = dataDodania; }
}
