package com.taskflow.app;

import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;
import com.taskflow.app.domain.usecase.SortowanieZadan;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class SortowanieZadanTest {

    private SortowanieZadan sortowanie;

    @Before
    public void setUp() {
        sortowanie = new SortowanieZadan();
    }

    @Test
    public void wykonaj_nullLista_zwracaPusta() {
        List<Zadanie> wynik = sortowanie.wykonaj(null, SortowanieZadan.TrybSortowania.WEDLUG_WYNIKU);
        assertNotNull(wynik);
        assertTrue(wynik.isEmpty());
    }

    @Test
    public void czyPrzeterminowane_przeszlyTermin_zwracaTrue() {
        Zadanie zadanie = new Zadanie();
        Calendar przeszlosc = Calendar.getInstance();
        przeszlosc.add(Calendar.DAY_OF_YEAR, -1);
        zadanie.setTermin(przeszlosc.getTime());
        zadanie.setDataDodania(new Date());

        assertTrue(sortowanie.czyPrzeterminowane(zadanie));
    }

    @Test
    public void czyPrzeterminowane_przyszlyTermin_zwracaFalse() {
        Zadanie zadanie = new Zadanie();
        Calendar przyszlosc = Calendar.getInstance();
        przyszlosc.add(Calendar.DAY_OF_YEAR, 5);
        zadanie.setTermin(przyszlosc.getTime());
        zadanie.setDataDodania(new Date());

        assertFalse(sortowanie.czyPrzeterminowane(zadanie));
    }

    @Test
    public void czyPrzeterminowane_bezTerminu_zwracaFalse() {
        Zadanie zadanie = new Zadanie();
        zadanie.setDataDodania(new Date());

        assertFalse(sortowanie.czyPrzeterminowane(zadanie));
    }
}
