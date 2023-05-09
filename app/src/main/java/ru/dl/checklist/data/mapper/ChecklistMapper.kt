package ru.dl.checklist.data.mapper

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiSuccessModelMapper
import ru.dl.checklist.data.model.ChecklistDto
import ru.dl.checklist.data.model.MarkDto
import ru.dl.checklist.data.model.ZoneDto
import ru.dl.checklist.domain.model.Address
import ru.dl.checklist.domain.model.AuditDate
import ru.dl.checklist.domain.model.Checker
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.Point
import ru.dl.checklist.domain.model.Senior
import ru.dl.checklist.domain.model.ShortName
import ru.dl.checklist.domain.model.Title
import ru.dl.checklist.domain.model.ZoneDomain

object ChecklistMapper : ApiSuccessModelMapper<ChecklistDto, ChecklistDomain> {
    override fun map(apiSuccessResponse: ApiResponse.Success<ChecklistDto>): ChecklistDomain {
        val dto = apiSuccessResponse.data
        val address = Address(dto.address ?: "")
        val auditDate = AuditDate(dto.auditDate ?: "")
        val checker = Checker(dto.checker ?: "")
        val senior = Senior(dto.senior ?: "")
        val shortName = ShortName(dto.shortName ?: "")
        val zones = dto.zones?.mapNotNull { zoneDto ->
            zoneDto?.let { mapZoneDtoToDomain(zoneDto) }
        } ?: emptyList()
        return ChecklistDomain(address, auditDate, checker, senior, shortName, zones)
    }

    private fun mapZoneDtoToDomain(dto: ZoneDto?): ZoneDomain? {
        return dto?.let {
            val marks = it.marks?.mapNotNull { markDto ->
                mapMarkDtoToDomain(markDto)
            } ?: emptyList()
            ZoneDomain(it.id ?: 0, marks, it.zone ?: "")
        }
    }

    private fun mapMarkDtoToDomain(dto: MarkDto?): MarkDomain? {
        return dto?.let {
            MarkDomain(Point(it.points ?: 0), Title(it.title ?: ""))
        }
    }
}
