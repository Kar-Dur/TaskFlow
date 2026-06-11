package com.taskflow.app.ui.main;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taskflow.app.R;
import com.taskflow.app.domain.model.Zadanie;
import com.taskflow.app.domain.usecase.SortowanieZadan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterZadan extends RecyclerView.Adapter<AdapterZadan.HolderZadania> {

    private List<Zadanie> zadania = new ArrayList<>();
    private final NasłuchiwaczAkcji nasłuchiwacz;
    private final SortowanieZadan sortowanie = new SortowanieZadan();
    private final SimpleDateFormat formatDaty = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public interface NasłuchiwaczAkcji {
        void przelaczUkonczone(Zadanie zadanie);
        void usunZadanie(Zadanie zadanie);
        void edytujZadanie(Zadanie zadanie);
    }

    public AdapterZadan(NasłuchiwaczAkcji nasłuchiwacz) {
        this.nasłuchiwacz = nasłuchiwacz;
    }

    public void ustawZadania(List<Zadanie> noweZadania) {
        DiffUtil.DiffResult roznica = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return zadania.size(); }
            @Override public int getNewListSize() { return noweZadania.size(); }
            @Override public boolean areItemsTheSame(int staryPos, int nowyPos) {
                return zadania.get(staryPos).getId() == noweZadania.get(nowyPos).getId();
            }
            @Override public boolean areContentsTheSame(int staryPos, int nowyPos) {
                Zadanie s = zadania.get(staryPos);
                Zadanie n = noweZadania.get(nowyPos);
                return s.isUkonczone() == n.isUkonczone()
                        && s.getTytul().equals(n.getTytul())
                        && s.getPriorytet() == n.getPriorytet();
            }
        });
        zadania = new ArrayList<>(noweZadania);
        roznica.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public HolderZadania onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View widok = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zadanie, parent, false);
        return new HolderZadania(widok);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderZadania holder, int position) {
        holder.bind(zadania.get(position));
    }

    @Override
    public int getItemCount() {
        return zadania.size();
    }

    class HolderZadania extends RecyclerView.ViewHolder {

        private final CheckBox poleUkonczone;
        private final TextView tekstTytul;
        private final TextView tekstOpis;
        private final TextView tekstTermin;
        private final TextView tekstPriorytet;
        private final ImageButton przyciskUsun;
        private final View pasekPriorytet;

        HolderZadania(@NonNull View itemView) {
            super(itemView);
            poleUkonczone = itemView.findViewById(R.id.pole_ukonczone);
            tekstTytul = itemView.findViewById(R.id.tekst_tytul);
            tekstOpis = itemView.findViewById(R.id.tekst_opis);
            tekstTermin = itemView.findViewById(R.id.tekst_termin);
            tekstPriorytet = itemView.findViewById(R.id.tekst_priorytet);
            przyciskUsun = itemView.findViewById(R.id.przycisk_usun);
            pasekPriorytet = itemView.findViewById(R.id.pasek_priorytet);
        }

        void bind(Zadanie zadanie) {
            poleUkonczone.setOnCheckedChangeListener(null);
            poleUkonczone.setChecked(zadanie.isUkonczone());

            tekstTytul.setText(zadanie.getTytul());
            tekstTytul.setPaintFlags(zadanie.isUkonczone()
                    ? tekstTytul.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                    : tekstTytul.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

            itemView.setAlpha(zadanie.isUkonczone() ? 0.5f : 1.0f);

            if (zadanie.getOpis() != null && !zadanie.getOpis().isEmpty()) {
                tekstOpis.setVisibility(View.VISIBLE);
                tekstOpis.setText(zadanie.getOpis());
            } else {
                tekstOpis.setVisibility(View.GONE);
            }

            if (zadanie.getTermin() != null) {
                tekstTermin.setVisibility(View.VISIBLE);
                boolean przeterminowane = sortowanie.czyPrzeterminowane(zadanie) && !zadanie.isUkonczone();
                tekstTermin.setText(formatDaty.format(zadanie.getTermin()));
                tekstTermin.setTextColor(ContextCompat.getColor(itemView.getContext(),
                        przeterminowane ? R.color.kolor_wysoki : R.color.tekst_drugorz));
            } else {
                tekstTermin.setVisibility(View.GONE);
            }

            tekstPriorytet.setText(zadanie.getPriorytet().pobierzEtykiete());

            int kolor = kolorPriorytetu(zadanie);
            tekstPriorytet.setTextColor(kolor);
            pasekPriorytet.setBackgroundColor(kolor);

            poleUkonczone.setOnCheckedChangeListener((btn, zaznaczone) -> {
                if (zaznaczone) {
                    tekstTytul.setPaintFlags(tekstTytul.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    itemView.setAlpha(0.5f);
                } else {
                    tekstTytul.setPaintFlags(tekstTytul.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    itemView.setAlpha(1.0f);
                }
                nasłuchiwacz.przelaczUkonczone(zadanie);
            });

            przyciskUsun.setOnClickListener(v -> nasłuchiwacz.usunZadanie(zadanie));
            itemView.setOnClickListener(v -> nasłuchiwacz.edytujZadanie(zadanie));
        }

        private int kolorPriorytetu(Zadanie zadanie) {
            switch (zadanie.getPriorytet()) {
                case WYSOKI: return ContextCompat.getColor(itemView.getContext(), R.color.kolor_wysoki);
                case SREDNI: return ContextCompat.getColor(itemView.getContext(), R.color.kolor_sredni);
                default: return ContextCompat.getColor(itemView.getContext(), R.color.kolor_niski);
            }
        }
    }
}
