package ru.dl.checklist.data.mapper

import ru.dl.checklist.data.model.remote.ObjectDto
import ru.dl.checklist.data.model.remote.TemplateDto
import ru.dl.checklist.domain.model.ObjectDomain
import ru.dl.checklist.domain.model.TemplateDomain

object DtoToDomainMapper {
    fun TemplateDto.toDomain() = TemplateDomain(
        uuid = uuid ?: "",
        name = name ?: ""
    )

    fun ObjectDto.toDomain() = ObjectDomain(
        uuid = uuid ?: "",
        name = name ?: "",
        shortname = shortname ?: ""
    )
}