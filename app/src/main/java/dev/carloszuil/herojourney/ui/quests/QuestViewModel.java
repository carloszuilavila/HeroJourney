package dev.carloszuil.herojourney.ui.quests;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import dev.carloszuil.herojourney.data.local.entities.Quest;
import dev.carloszuil.herojourney.data.repository.QuestRepository;

public class QuestViewModel extends AndroidViewModel {

    private final QuestRepository repository;
    private final LiveData<List<Quest>> allQuests;

    public QuestViewModel(@NonNull Application application){
        super(application);
        repository = new QuestRepository(application);
        allQuests = repository.getAllQuests();
    }

    public LiveData<List<Quest>> getAllQuests(){
        return allQuests;
    }

    public void addQuest(Quest quest){
        repository.insert(quest);
    }

    public void updateQuest(Quest quest){
        repository.update(quest);
    }

    public void deleteQuest(Quest quest){
        repository.delete(quest);
    }

}
