package dev.carloszuil.herojourney.data.local.entities;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

public class QuestStateConverter {

    @TypeConverter
    public static QuestState fromString(String value){
        if(value==null) return null;
        return QuestState.valueOf(value);
    }

    @TypeConverter
    public static String questStateToString(QuestState state){
        return (state == null ? null : state.name());
    }
}
