package com.taskflow.app;

import com.taskflow.app.data.repository.RepozytoriumZadan;
import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;
import com.taskflow.app.domain.usecase.DodawanieZadania;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DodawanieZadaniaTest {

    @Mock
    private RepozytoriumZadan mockRepozytorium;

    private DodawanieZadania dodawanie;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dodawanie = new DodawanieZadania(mockRepozytorium);
    }

    @Test
    public void wykonaj_poprawneZadanie_zwracaTrue() {
        Zadanie zadanie = zbudujZadanie("Zadzwonić do lekarza", Priorytet.WYSOKI, 3);
        assertTrue(dodawanie.wykonaj(zadanie));
        verify(mockRepozytorium).wstaw(zadanie);
    }

    @Test
    public void wykonaj_bialeLznakiTytul_zwracaFalse() {
        Zadanie zadanie = zbudujZadanie("  ", Priorytet.SREDNI, -1);
        assertFalse(dodawanie.wykonaj(zadanie));
        verifyNoInteractions(mockRepozytorium);
    }

    @Test
    public void wykonaj_niskiPriorytet_zapisane() {
        Zadanie zadanie = zbudujZadanie("Posortować dokumenty", Priorytet.NISKI, -1);
        assertTrue(dodawanie.wykonaj(zadanie));
    }

    @Test
    public void wykonaj_wysokiPriorytetzTerminem_zapisane() {
        Zadanie zadanie = zbudujZadanie("Deadline projektu", Priorytet.WYSOKI, 0);
        assertTrue(dodawanie.wykonaj(zadanie));
    }

    private Zadanie zbudujZadanie(String tytul, Priorytet priorytet, int dniOdTeraz) {
        Zadanie zadanie = new Zadanie();
        zadanie.setTytul(tytul);
        zadanie.setPriorytet(priorytet);
        zadanie.setDataDodania(new Date());
        if (dniOdTeraz >= 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, dniOdTeraz);
            zadanie.setTermin(cal.getTime());
        }
        return zadanie;
    }
}
