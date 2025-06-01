package dev.carloszuil.herojourney.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dev.carloszuil.herojourney.data.local.entities.Quest;
import dev.carloszuil.herojourney.data.local.entities.QuestState;

@Dao
public interface QuestDao {

    @Query("SELECT * FROM quest")
    LiveData<List<Quest>> getAllQuests();

    @Insert
    void insert(Quest quest);

    @Update
    void update(Quest quest);

    @Delete
    void delete(Quest quest);

    @Query("SELECT * FROM quest WHERE estado = :state")
    LiveData<List<Quest>> getQuestsByState(QuestState state);
}
