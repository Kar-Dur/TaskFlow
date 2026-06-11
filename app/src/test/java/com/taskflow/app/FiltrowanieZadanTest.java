package com.taskflow.app;

import com.taskflow.app.domain.model.Priorytet;
import com.taskflow.app.domain.model.Zadanie;
import com.taskflow.app.domain.usecase.FiltrowanieZadan;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class FiltrowanieZadanTest {

    private FiltrowanieZadan filtrowanie;
    private List<Zadanie> przykladowe;

    @Before
    public void setUp() {
        filtrowanie = new FiltrowanieZadan();

        Zadanie z1 = new Zadanie();
        z1.setId(1);
        z1.setTytul("Kupic mleko");
        z1.setOpis("Kupic 2 litry");
        z1.setPriorytet(Priorytet.NISKI);
        z1.setUkonczone(false);
        z1.setDataDodania(new Date());

        Zadanie z2 = new Zadanie();
        z2.setId(2);
        z2.setTytul("Zrobic raport");
        z2.setOpis("Raport kwartalny");
        z2.setPriorytet(Priorytet.WYSOKI);
        z2.setUkonczone(false);
        z2.setDataDodania(new Date());

        Zadanie z3 = new Zadanie();
        z3.setId(3);
        z3.setTytul("Oddac projekt");
        z3.setOpis("Projekt z Java");
        z3.setPriorytet(Priorytet.WYSOKI);
        z3.setUkonczone(true);
        z3.setDataDodania(new Date());

        Zadanie z4 = new Zadanie();
        z4.setId(4);
        z4.setTytul("Spotkanie z zespolem");
        z4.setOpis(null);
        z4.setPriorytet(Priorytet.SREDNI);
        z4.setUkonczone(false);
        z4.setDataDodania(new Date());

        przykladowe = Arrays.asList(z1, z2, z3, z4);
    }

    @Test
    public void poPriorytecie_wysoki_zwracaTylkoWysokie() {
        List<Zadanie> wynik = filtrowanie.poPriorytecie(przykladowe, Priorytet.WYSOKI);
        assertEquals(2, wynik.size());
        wynik.forEach(z -> assertEquals(Priorytet.WYSOKI, z.getPriorytet()));
    }

    @Test
    public void poPriorytecie_niski_zwracaJedno() {
        List<Zadanie> wynik = filtrowanie.poPriorytecie(przykladowe, Priorytet.NISKI);
        assertEquals(1, wynik.size());
        assertEquals("Kupic mleko", wynik.get(0).getTytul());
    }

    @Test
    public void poPriorytecie_null_zwracaWszystkie() {
        List<Zadanie> wynik = filtrowanie.poPriorytecie(przykladowe, null);
        assertEquals(4, wynik.size());
    }

    @Test
    public void poStatusie_ukonczone_zwracaTylkoUkonczone() {
        List<Zadanie> wynik = filtrowanie.poStatusie(przykladowe, true);
        assertEquals(1, wynik.size());
        assertEquals("Oddac projekt", wynik.get(0).getTytul());
    }

    @Test
    public void poStatusie_aktywne_zwracaTylkoAktywne() {
        List<Zadanie> wynik = filtrowanie.poStatusie(przykladowe, false);
        assertEquals(3, wynik.size());
        wynik.forEach(z -> assertFalse(z.isUkonczone()));
    }

    @Test
    public void poSlowie_znajdujePoCzesciTytulu() {
        List<Zadanie> wynik = filtrowanie.poSłowie(przykladowe, "raport");
        assertEquals(1, wynik.size());
        assertEquals("Zrobic raport", wynik.get(0).getTytul());
    }

    @Test
    public void poSlowie_znajdujePoCzesciOpisu() {
        List<Zadanie> wynik = filtrowanie.poSłowie(przykladowe, "java");
        assertEquals(1, wynik.size());
        assertEquals("Oddac projekt", wynik.get(0).getTytul());
    }

    @Test
    public void poSlowie_niezalezneOdWielkosci() {
        List<Zadanie> wynik = filtrowanie.poSłowie(przykladowe, "MLEKO");
        assertEquals(1, wynik.size());
        assertEquals("Kupic mleko", wynik.get(0).getTytul());
    }

    @Test
    public void poSlowie_puste_zwracaWszystkie() {
        List<Zadanie> wynik = filtrowanie.poSłowie(przykladowe, "");
        assertEquals(4, wynik.size());
    }

    @Test
    public void poSlowie_brakWynikow_zwracaPusta() {
        List<Zadanie> wynik = filtrowanie.poSłowie(przykladowe, "xyzxyz");
        assertTrue(wynik.isEmpty());
    }

    @Test
    public void poSlowie_null_zwracaWszystkie() {
        List<Zadanie> wynik = filtrowanie.poSłowie(przykladowe, null);
        assertEquals(4, wynik.size());
    }

    @Test
    public void filtrujPrzeterminowane_zwracaTylkoAktywnePrzeterminowane() {
        Zadanie przeterminowane = new Zadanie();
        przeterminowane.setTytul("Przeterminowane");
        przeterminowane.setPriorytet(Priorytet.WYSOKI);
        przeterminowane.setUkonczone(false);
        przeterminowane.setDataDodania(new Date());
        Calendar przeszlosc = Calendar.getInstance();
        przeszlosc.add(Calendar.DAY_OF_YEAR, -3);
        przeterminowane.setTermin(przeszlosc.getTime());

        Zadanie ukonczonePrzeterminowane = new Zadanie();
        ukonczonePrzeterminowane.setTytul("Ukonczone przeterminowane");
        ukonczonePrzeterminowane.setPriorytet(Priorytet.WYSOKI);
        ukonczonePrzeterminowane.setUkonczone(true);
        ukonczonePrzeterminowane.setDataDodania(new Date());
        ukonczonePrzeterminowane.setTermin(przeszlosc.getTime());

        List<Zadanie> wynik = filtrowanie.filtrujPrzeterminowane(Arrays.asList(przeterminowane, ukonczonePrzeterminowane));

        assertEquals(1, wynik.size());
        assertEquals("Przeterminowane", wynik.get(0).getTytul());
    }

    @Test
    public void wszystkie_polaczoneFiltry() {
        List<Zadanie> wynik = filtrowanie.wszystkie(przykladowe, Priorytet.WYSOKI, false, "raport");
        assertEquals(1, wynik.size());
        assertEquals("Zrobic raport", wynik.get(0).getTytul());
    }

    @Test
    public void wszystkie_bezFiltrow_zwracaWszystkie() {
        List<Zadanie> wynik = filtrowanie.wszystkie(przykladowe, null, null, null);
        assertEquals(4, wynik.size());
    }

    @Test
    public void poPriorytecie_nullLista_zwracaPusta() {
        List<Zadanie> wynik = filtrowanie.poPriorytecie(null, Priorytet.WYSOKI);
        assertNotNull(wynik);
        assertTrue(wynik.isEmpty());
    }

    @Test
    public void poSlowie_zadanieZNullOpisem_bezWyjatku() {
        List<Zadanie> wynik = filtrowanie.poSłowie(przykladowe, "spotkanie");
        assertEquals(1, wynik.size());
    }
}
