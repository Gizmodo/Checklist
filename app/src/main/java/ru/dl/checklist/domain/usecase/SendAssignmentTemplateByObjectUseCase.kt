package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.model.AssignedTemplateObject
import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class SendAssignmentTemplateByObjectUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    fun run(payload: AssignedTemplateObject) =
        repository.sendAssignTemplateByObject(payload)
}