package ru.dl.checklist.data.mapper

import ru.dl.checklist.data.model.entity.ChecklistEntity
import ru.dl.checklist.data.model.entity.HouseCheckEntity
import ru.dl.checklist.data.model.entity.HouseChecklistEntity
import ru.dl.checklist.data.model.entity.MarkEntity
import ru.dl.checklist.data.model.entity.ZoneEntity
import ru.dl.checklist.data.model.remote.ChecklistDto
import ru.dl.checklist.data.model.remote.HouseCheckDto
import ru.dl.checklist.data.model.remote.HouseChecklistDto
import ru.dl.checklist.data.model.remote.MarkDto
import ru.dl.checklist.data.model.remote.ZoneDto

object DtoToEntityMapper {
    fun ChecklistDto.toEntity(): ChecklistEntity {
        return ChecklistEntity(
            uuid = uuid ?: "",
            address = address ?: "",
            auditDate = auditDate ?: "",
            checker = checker ?: "",
            senior = senior ?: "",
            shortName = shortName ?: "",
            title = title ?: "Название не задано"
        )
    }

    fun ZoneDto.toEntity(checklistId: Long): ZoneEntity {
        return ZoneEntity(
            checklistId = checklistId,
            zone = zone ?: ""
        )
    }

    fun MarkDto.toEntity(zoneId: Long): MarkEntity {
        return MarkEntity(
            zoneId = zoneId,
            points = points ?: 0,
            title = title ?: "",
            answer = 0f,
            comment = "",
            pkd = "",
            flag = flag ?: 0
        )
    }

    fun HouseChecklistDto.toEntity(): HouseChecklistEntity {
        return HouseChecklistEntity(
            uuid = uuid ?: "",
            title = title ?: "",
            timeStart = timeStart ?: "",
            timeEnd = timeEnd ?: ""
        )
    }

    fun HouseCheckDto.toEntity(checklistId: Long): HouseCheckEntity {
        return HouseCheckEntity(
            uuid = uuid ?: "",
            checklistId = checklistId,
            name = name ?: "",
            isPhotoRequired = photoRequired ?: false,
            isStart = start ?: false,
            isEnd = end ?: false,
            nextCheckUUID = next,
            answer = null
        )
    }
}