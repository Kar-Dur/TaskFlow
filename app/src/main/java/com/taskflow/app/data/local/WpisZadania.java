package com.taskflow.app.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "zadania")
public class WpisZadania {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String tytul;
    private String opis;
    private int priorytet;
    private Long termin;
    private boolean ukonczone;
    private long dataDodania;

    public WpisZadania() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTytul() { return tytul; }
    public void setTytul(String tytul) { this.tytul = tytul; }

    public String getOpis() { return opis; }
    public void setOpis(String opis) { this.opis = opis; }

    public int getPriorytet() { return priorytet; }
    public void setPriorytet(int priorytet) { this.priorytet = priorytet; }

    public Long getTermin() { return termin; }
    public void setTermin(Long termin) { this.termin = termin; }

    public boolean isUkonczone() { return ukonczone; }
    public void setUkonczone(boolean ukonczone) { this.ukonczone = ukonczone; }

    public long getDataDodania() { return dataDodania; }
    public void setDataDodania(long dataDodania) { this.dataDodania = dataDodania; }
}
