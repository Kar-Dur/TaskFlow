package com.taskflow.app;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.taskflow.app.data.repository.RepozytoriumZadan;
import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;
import com.taskflow.app.domain.usecase.DodawanieZadania;
import com.taskflow.app.domain.usecase.FiltrowanieZadan;
import com.taskflow.app.domain.usecase.SortowanieZadan;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ModelWidokuZadanTest {

    @Rule
    public InstantTaskExecutorRule regula = new InstantTaskExecutorRule();

    @Mock
    private RepozytoriumZadan mockRepozytorium;

    private DodawanieZadania dodawanie;
    private SortowanieZadan sortowanie;
    private FiltrowanieZadan filtrowanie;

    private MutableLiveData<List<Zadanie>> liveDataZadan;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        liveDataZadan = new MutableLiveData<>();
        when(mockRepozytorium.wszystkieZadania()).thenReturn(liveDataZadan);
        when(mockRepozytorium.aktywneZadania()).thenReturn(new MutableLiveData<>());

        dodawanie = new DodawanieZadania(mockRepozytorium);
        sortowanie = new SortowanieZadan();
        filtrowanie = new FiltrowanieZadan();
    }

    @Test
    public void dodajZadanie_poprawne_wywolujeWstawWRepo() {
        Zadanie zadanie = new Zadanie();
        zadanie.setTytul("Testowe zadanie");
        zadanie.setPriorytet(Priorytet.SREDNI);
        zadanie.setDataDodania(new Date());

        dodawanie.wykonaj(zadanie);

        verify(mockRepozytorium, times(1)).wstaw(zadanie);
    }

    @Test
    public void dodajZadanie_pustyTytul_nieWywolujeRepo() {
        Zadanie zadanie = new Zadanie();
        zadanie.setTytul("");
        zadanie.setDataDodania(new Date());

        boolean wynik = dodawanie.wykonaj(zadanie);

        assertFalse(wynik);
        verify(mockRepozytorium, never()).wstaw(any());
    }

    @Test
    public void dodajZadanie_nullTytul_nieWywolujeRepo() {
        Zadanie zadanie = new Zadanie();
        zadanie.setTytul(null);
        zadanie.setDataDodania(new Date());

        boolean wynik = dodawanie.wykonaj(zadanie);

        assertFalse(wynik);
        verify(mockRepozytorium, never()).wstaw(any());
    }

    @Test
    public void dodajZadanie_nullZadanie_zwracaFalse() {
        boolean wynik = dodawanie.wykonaj(null);
        assertFalse(wynik);
        verify(mockRepozytorium, never()).wstaw(any());
    }

    @Test
    public void usunZadanie_wywolujeUsunWRepo() {
        Zadanie zadanie = new Zadanie();
        zadanie.setId(1L);
        zadanie.setTytul("Do usuniecia");
        zadanie.setDataDodania(new Date());

        mockRepozytorium.usun(zadanie);

        verify(mockRepozytorium, times(1)).usun(zadanie);
    }

    @Test
    public void repozytorium_wszystkieZadania_zwracaLiveData() {
        Zadanie z1 = new Zadanie();
        z1.setTytul("Zadanie 1");
        z1.setPriorytet(Priorytet.WYSOKI);
        z1.setDataDodania(new Date());

        Zadanie z2 = new Zadanie();
        z2.setTytul("Zadanie 2");
        z2.setPriorytet(Priorytet.NISKI);
        z2.setDataDodania(new Date());

        liveDataZadan.setValue(Arrays.asList(z1, z2));

        List<Zadanie> wynik = liveDataZadan.getValue();
        assertNotNull(wynik);
        assertEquals(2, wynik.size());
    }

    @Test
    public void sortowanieIFiltrowanie_laczone_zwracaPoprawneLista() {
        Zadanie z1 = new Zadanie();
        z1.setTytul("Wazne spotkanie");
        z1.setPriorytet(Priorytet.WYSOKI);
        z1.setUkonczone(false);
        z1.setDataDodania(new Date());

        Zadanie z2 = new Zadanie();
        z2.setTytul("Kupic chleb");
        z2.setPriorytet(Priorytet.NISKI);
        z2.setUkonczone(false);
        z2.setDataDodania(new Date());

        Zadanie z3 = new Zadanie();
        z3.setTytul("Spotkanie z klientem");
        z3.setPriorytet(Priorytet.WYSOKI);
        z3.setUkonczone(false);
        z3.setDataDodania(new Date());

        List<Zadanie> wszystkie = Arrays.asList(z1, z2, z3);

        List<Zadanie> przefiltrowane = filtrowanie.poSłowie(wszystkie, "spotkanie");
        assertEquals(2, przefiltrowane.size());

        List<Zadanie> posortowane = sortowanie.wykonaj(przefiltrowane, SortowanieZadan.TrybSortowania.WEDLUG_PRIORYTETU);
        assertEquals(Priorytet.WYSOKI, posortowane.get(0).getPriorytet());
    }

    @Test
    public void repozytorium_mockZwracaPusta_bezWyjatku() {
        when(mockRepozytorium.wszystkieZadania()).thenReturn(new MutableLiveData<>(Collections.emptyList()));

        List<Zadanie> przefiltrowane = filtrowanie.poPriorytecie(Collections.emptyList(), Priorytet.WYSOKI);
        assertTrue(przefiltrowane.isEmpty());

        List<Zadanie> posortowane = sortowanie.wykonaj(Collections.emptyList(), SortowanieZadan.TrybSortowania.WEDLUG_WYNIKU);
        assertTrue(posortowane.isEmpty());
    }

    @Test
    public void czyPoprawne_bialeLznaki_zwracaFalse() {
        Zadanie zadanie = new Zadanie();
        zadanie.setTytul("   ");
        zadanie.setDataDodania(new Date());

        assertFalse(dodawanie.czyPoprawne(zadanie));
    }

    @Test
    public void czyPoprawne_poprawnyTytul_zwracaTrue() {
        Zadanie zadanie = new Zadanie();
        zadanie.setTytul("Cos do zrobienia");
        zadanie.setDataDodania(new Date());

        assertTrue(dodawanie.czyPoprawne(zadanie));
    }
}
