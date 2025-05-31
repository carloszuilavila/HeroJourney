package dev.carloszuil.herojourney.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.data.local.entities.Habit;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.TareaViewHolder> {

    private final List<Habit> habits;
    private final Runnable onTareaCambiada;  // callback

    public HabitAdapter(List<Habit> habits, Runnable onTareaCambiada) {
        this.habits = habits;
        this.onTareaCambiada = onTareaCambiada;
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        public final CheckBox checkBox;
        public final TextView texto;

        public TareaViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.habitCheckbox);
            texto   = view.findViewById(R.id.habitText);
        }
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Habit tarea = habits.get(position);

        // Texto y estado inicial
        holder.texto.setText(tarea.getName());
        holder.checkBox.setChecked(tarea.isFinished());

        // Evitar que el listener anterior se dispare al reciclar la vista
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(tarea.isFinished());

        // Nuevo listener
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tarea.setFinished(isChecked);
                onTareaCambiada.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }
}
