package com.taskflow.app;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.taskflow.app.ui.main.GlownaAktywnosc;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GlownaAktywnoscTest {

    @Rule
    public ActivityScenarioRule<GlownaAktywnosc> regula =
            new ActivityScenarioRule<>(GlownaAktywnosc.class);

    @Test
    public void glownaAktywnosc_widoczna() {
        onView(withId(R.id.lista_zadan)).check(matches(isDisplayed()));
    }

    @Test
    public void przyciskDodaj_widoczny() {
        onView(withId(R.id.przycisk_dodaj)).check(matches(isDisplayed()));
    }

    @Test
    public void przyciskDodaj_otwieraDodajZadanie() {
        onView(withId(R.id.przycisk_dodaj)).perform(click());
        onView(withId(R.id.pole_tytul)).check(matches(isDisplayed()));
    }

    @Test
    public void ekranDodawania_maPoleWybieraniaPriorytetu() {
        onView(withId(R.id.przycisk_dodaj)).perform(click());
        onView(withId(R.id.wybor_priorytetu)).check(matches(isDisplayed()));
    }

    @Test
    public void poleSzukaj_widoczne() {
        onView(withId(R.id.pole_szukaj)).check(matches(isDisplayed()));
    }

    @Test
    public void poleSzukaj_pisanie_bezAwarii() {
        onView(withId(R.id.pole_szukaj))
                .perform(typeText("test"), closeSoftKeyboard());
        onView(withId(R.id.pole_szukaj)).check(matches(isDisplayed()));
    }

    @Test
    public void chipyPriorytetu_widoczne() {
        onView(withId(R.id.grupa_priorytety)).check(matches(isDisplayed()));
    }

    @Test
    public void chipySortowania_widoczne() {
        onView(withId(R.id.grupa_sortowanie)).check(matches(isDisplayed()));
    }

    @Test
    public void zapiszPustyTytul_zostajNaEkranie() {
        onView(withId(R.id.przycisk_dodaj)).perform(click());
        onView(withId(R.id.przycisk_zapisz)).perform(click());
        onView(withId(R.id.pole_tytul)).check(matches(isDisplayed()));
    }

    @Test
    public void dodajZadanie_poprawneDane_wracaDoGlownej() {
        onView(withId(R.id.przycisk_dodaj)).perform(click());

        onView(withId(R.id.pole_tytul))
                .perform(replaceText("Zadanie testowe"), closeSoftKeyboard());

        onView(withId(R.id.przycisk_zapisz)).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.przycisk_dodaj)).check(matches(isDisplayed()));
    }
}
