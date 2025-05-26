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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.data.local.entities.Habit;

public class HabitExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Consumer<Habit> onHabitToggled;
    private final BiConsumer<String, Boolean> onSectionToggled;
    private final Consumer<Habit> onHabitClicked;

    private final List<HabitListItem> items = new ArrayList<>();
    private final Map<String, Boolean> sectionExpandedState = new HashMap<>();
    private List<Habit> allHabits = new ArrayList<>();

    public HabitExpandableAdapter(
            Consumer<Habit> onHabitToggled,
            BiConsumer<String, Boolean> onSectionToggled,
            Consumer<Habit> onHabitClicked
    ) {
        this.onHabitToggled = onHabitToggled;
        this.onSectionToggled = onSectionToggled;
        this.onHabitClicked = onHabitClicked;
    }

    public void submitHabits(@NonNull List<Habit> habits,
                             boolean pendientesExpanded,
                             boolean completadasExpanded) {
        this.allHabits = new ArrayList<>(habits);
        sectionExpandedState.put("📌 Pendientes", pendientesExpanded);
        sectionExpandedState.put("✅ Completadas", completadasExpanded);
        rebuildItems();
    }

    private void rebuildItems() {
        List<Habit> pendientes = new ArrayList<>();
        List<Habit> completadas = new ArrayList<>();
        for (Habit h : allHabits) {
            if (h.isFinished()) completadas.add(h);
            else pendientes.add(h);
        }

        items.clear();
        boolean pendExp = sectionExpandedState.getOrDefault("📌 Pendientes", true);
        items.add(new HabitListItem.SectionHeader("📌 Pendientes", pendExp));
        if (pendExp) {
            for (Habit h : pendientes) items.add(new HabitListItem.HabitItem(h));
        }
        boolean compExp = sectionExpandedState.getOrDefault("✅ Completadas", false);
        items.add(new HabitListItem.SectionHeader("✅ Completadas", compExp));
        if (compExp) {
            for (Habit h : completadas) items.add(new HabitListItem.HabitItem(h));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof HabitListItem.SectionHeader) ? 0 : 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType == 0 ? R.layout.item_section_header : R.layout.item_habit,
                        parent, false);
        return viewType == 0 ? new SectionViewHolder(v) : new HabitViewHolder(v);
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

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageSwitcher switcher;

        SectionViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.sectionTitle);
            switcher = view.findViewById(R.id.expandSwitcher);
            switcher.setFactory(() -> {
                ImageView iv = new ImageView(view.getContext());
                iv.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT));
                iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                return iv;
            });
        }

        void bind(HabitListItem.SectionHeader header) {
            title.setText(header.getTitle());
            int icon = header.isExpanded() ? R.drawable.ic_expand_less : R.drawable.ic_expand_more;
            switcher.setImageResource(icon);
            itemView.setOnClickListener(v -> {
                boolean newState = !header.isExpanded();
                header.setExpanded(newState);
                sectionExpandedState.put(header.getTitle(), newState);
                rebuildItems();
                onSectionToggled.accept(header.getTitle(), newState);
            });
        }
    }

    class HabitViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox check;
        private final TextView text;

        HabitViewHolder(@NonNull View view) {
            super(view);
            check = view.findViewById(R.id.checkboxTarea);
            text = view.findViewById(R.id.textoTarea);
        }

        void bind(Habit habit) {
            text.setText(habit.getName());
            check.setOnCheckedChangeListener(null);
            check.setChecked(habit.isFinished());
            check.setOnCheckedChangeListener((btn, isChecked) -> {
                habit.setFinished(isChecked);
                rebuildItems();
                onHabitToggled.accept(habit); // notifica el hábito cambiado
            });
            itemView.setOnClickListener(v -> onHabitClicked.accept(habit));
        }
    }
}
