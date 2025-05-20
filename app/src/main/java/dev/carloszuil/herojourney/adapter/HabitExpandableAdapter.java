package dev.carloszuil.herojourney.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.model.Habit;

public class HabitExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Callback interfaces
    public interface OnHabitToggledListener {
        void onHabitToggled();
    }

    public interface OnSectionToggledListener {
        void onSectionToggled(String sectionTitle, boolean isExpanded);
    }

    public interface OnHabitClickedListener {
        void onHabitClicked(Habit habit);
    }

    private final OnHabitToggledListener onHabitToggled;
    private final OnSectionToggledListener onSectionToggled;
    private final OnHabitClickedListener onHabitClicked;

    private final List<HabitListItem> items = new ArrayList<>();
    private final Map<String, Boolean> sectionExpandedState = new HashMap<>();
    private List<Habit> allHabits = new ArrayList<>();

    public HabitExpandableAdapter(
            @NonNull OnHabitToggledListener onHabitToggled,
            @NonNull OnSectionToggledListener onSectionToggled,
            @NonNull OnHabitClickedListener onHabitClicked
    ) {
        this.onHabitToggled   = onHabitToggled;
        this.onSectionToggled = onSectionToggled;
        this.onHabitClicked   = onHabitClicked;
    }

    /**
     * Recibe la lista completa de hábitos y el estado inicial de cada sección.
     */
    public void submitHabits(@NonNull List<Habit> habits,
                             boolean pendientesExpanded,
                             boolean completadasExpanded) {
        this.allHabits = new ArrayList<>(habits);
        sectionExpandedState.put("📌 Pendientes", pendientesExpanded);
        sectionExpandedState.put("✅ Completadas", completadasExpanded);
        rebuildItems();
    }

    /**
     * Reconstruye la lista interna de items (cabeceras y hábitos) y notifica el adaptador.
     */
    private void rebuildItems() {
        List<Habit> pendientes  = new ArrayList<>();
        List<Habit> completadas = new ArrayList<>();

        for (Habit h : allHabits) {
            if (h.isCompletada()) completadas.add(h);
            else                 pendientes.add(h);
        }

        items.clear();

        boolean pendExp = sectionExpandedState.getOrDefault("📌 Pendientes", true);
        items.add(new HabitListItem.SectionHeader("📌 Pendientes", pendExp));
        if (pendExp) {
            for (Habit habitoPendientes : pendientes) {
                items.add(new HabitListItem.HabitItem(habitoPendientes));
            }
        }

        boolean compExp = sectionExpandedState.getOrDefault("✅ Completadas", false);
        items.add(new HabitListItem.SectionHeader("✅ Completadas", compExp));
        if (compExp) {
            for (Habit habitoCompletadas : completadas) {
                items.add(new HabitListItem.HabitItem(habitoCompletadas));
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        HabitListItem item = items.get(position);
        return (item instanceof HabitListItem.SectionHeader) ? 0 : 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View v = inf.inflate(R.layout.item_section_header, parent, false);
            return new SectionViewHolder(v);
        } else {
            View v = inf.inflate(R.layout.item_habit, parent, false);
            return new HabitViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HabitListItem item = items.get(position);
        if (holder instanceof SectionViewHolder) {
            ((SectionViewHolder) holder).bind((HabitListItem.SectionHeader) item);
        } else {
            ((HabitViewHolder) holder).bind(((HabitListItem.HabitItem) item).getHabit());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** ViewHolder para cabeceras de sección */
    public class SectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageSwitcher switcher;

        public SectionViewHolder(@NonNull View view) {
            super(view);
            title    = view.findViewById(R.id.sectionTitle);
            switcher = view.findViewById(R.id.expandSwitcher);

            switcher.setFactory(() -> {
                ImageView iv = new ImageView(view.getContext());
                iv.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                return iv;
            });
        }

        public void bind(@NonNull HabitListItem.SectionHeader header) {
            title.setText(header.getTitle());
            int iconRes = header.isExpanded()
                    ? R.drawable.ic_expand_less
                    : R.drawable.ic_expand_more;
            switcher.setImageResource(iconRes);

            itemView.setOnClickListener(v -> {
                boolean newState = !header.isExpanded();
                header.setExpanded(newState);
                sectionExpandedState.put(header.getTitle(), newState);
                rebuildItems();
                onSectionToggled.onSectionToggled(header.getTitle(), newState);
            });
        }
    }

    /** ViewHolder para cada ítem de hábito */
    public class HabitViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox check;
        private final TextView text;

        public HabitViewHolder(@NonNull View view) {
            super(view);
            check = view.findViewById(R.id.checkboxTarea);
            text  = view.findViewById(R.id.textoTarea);
        }

        public void bind(@NonNull Habit habit) {
            text.setText(habit.getNombre());

            // Evitar callback anterior al reciclar
            check.setOnCheckedChangeListener(null);
            check.setChecked(habit.isCompletada());

            check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                habit.setCompletada(isChecked);
                rebuildItems();
                onHabitToggled.onHabitToggled();
            });

            itemView.setOnClickListener(v -> onHabitClicked.onHabitClicked(habit));
        }
    }
}

