package ru.dl.checklist.app.utils

object HTTPConstants {
    const val NO_INTERNET_ERROR_MESSAGE = "Отсутствует интернет соединение"
    const val SERVER_ERROR = "Внутренняя ошибка сервера"
    const val JSON_EXCEPTION = "ННевалидный JSON"
    const val JSON_MALFORMED = "Пплохо сформированный JSON"
    const val SECURITY_EXCEPTION = "Нарушение безопасности"
    const val GENERIC_ERROR_MESSAGE = "Упс! Что-то пошло не так"
    const val GatewayTimeout = "Шлюз не отвечает"
    const val BadGateway = "Плохой, ошибочный шлюз"
    const val Unauthorized = "Не авторизован"
    const val Forbidden = "Доступ запрещён"
}