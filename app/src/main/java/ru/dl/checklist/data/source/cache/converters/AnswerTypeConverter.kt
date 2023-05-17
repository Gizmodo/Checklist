package ru.dl.checklist.data.source.cache.converters

import androidx.room.TypeConverter
import ru.dl.checklist.domain.model.Answer

class AnswerTypeConverter {
    @TypeConverter
    fun fromAnswer(answer: Answer?): Int? {
        return answer?.value?.let { if (it) 1 else 0 }
    }

    @TypeConverter
    fun toAnswer(value: Int?): Answer {
        return when (value) {
            1 -> Answer.YES
            0 -> Answer.NO
            else -> Answer.UNDEFINED
        }
    }
}