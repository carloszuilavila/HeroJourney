package dev.carloszuil.herojourney.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dev.carloszuil.herojourney.data.local.AppDatabase;
import dev.carloszuil.herojourney.data.local.dao.QuestDao;
import dev.carloszuil.herojourney.data.local.entities.Quest;

public class QuestRepository {

    private final QuestDao questDao;
    private final LiveData<List<Quest>> allQuests;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public QuestRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        questDao = database.questDao();
        allQuests = questDao.getAllQuests();
    }

    public LiveData<List<Quest>> getAllQuests(){
        return allQuests;
    }

    public void insert(Quest quest){
        executor.execute(() -> questDao.insert(quest));
    }

    public void update(Quest quest){
        executor.execute(() -> questDao.update(quest));
    }

    public void delete(Quest quest){
        executor.execute(() -> questDao.delete(quest));
    }

}
