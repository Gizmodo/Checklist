package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.model.AuthPayload
import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class AuthenticateUserUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    fun run(authPayload: AuthPayload) =
        repository.sendAuth(auth = authPayload)
}