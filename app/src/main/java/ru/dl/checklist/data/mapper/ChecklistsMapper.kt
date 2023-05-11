package ru.dl.checklist.data.mapper

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiSuccessModelMapper
import ru.dl.checklist.data.model.entity.ZoneEntity
import ru.dl.checklist.data.model.remote.ChecklistDto
import ru.dl.checklist.data.model.remote.ChecklistsDto
import ru.dl.checklist.data.model.remote.MarkDto
import ru.dl.checklist.data.model.remote.ZoneDto
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.ChecklistsDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.model.ZoneDomain2
import ru.dl.checklist.domain.model.valueclasses.valueclasses

object ChecklistsMapper : ApiSuccessModelMapper<ChecklistsDto, ChecklistsDomain> {
    override fun map(apiSuccessResponse: ApiResponse.Success<ChecklistsDto>): ChecklistsDomain {
        val dto = apiSuccessResponse.data
        val checklists = dto.checklists?.mapNotNull { checklistsDto ->
            mapChecklistDtoToDomain(checklistsDto)
        } ?: emptyList()
        return ChecklistsDomain(checklists)
    }

    private fun mapChecklistDtoToDomain(dto: ChecklistDto?): ChecklistDomain? {
        return dto?.let {
            val uuid = it.uuid ?: ""
            val address = valueclasses.Address(it.address ?: "")
            val auditDate = valueclasses.AuditDate(it.auditDate ?: "")
            val checker = valueclasses.Checker(it.checker ?: "")
            val senior = valueclasses.Senior(it.senior ?: "")
            val shortName = valueclasses.ShortName(it.shortName ?: "")
            val zones = it.zones?.mapNotNull { zoneDto ->
                zoneDto?.let { mapZoneDtoToDomain(zoneDto) }
            } ?: emptyList()
            ChecklistDomain(uuid, address, auditDate, checker, senior, shortName, zones)
        }
    }

    fun mapZoneDtoToDomain(dto: ZoneDto?): ZoneDomain? {
        return dto?.let {
            val marks = it.marks?.mapNotNull { markDto ->
                mapMarkDtoToDomain(markDto)
            } ?: emptyList()
            ZoneDomain(marks = marks, zone = it.zone ?: "")
        }
    }
fun mapZoneEntityToDomain(entity:ZoneEntity?):ZoneDomain2?{
    return entity?.let {
        ZoneDomain2(entity.id,entity.zone)
    }
}
    private fun mapMarkDtoToDomain(dto: MarkDto?): MarkDomain? {
        return dto?.let {
            MarkDomain(it.points ?: 0, it.title ?: "")
        }
    }
}