package ru.dl.checklist.domain.model.valueclasses

object ValueClasses {

    @JvmInline
    value class Address(val value: String)

    @JvmInline
    value class AuditDate(val value: String)

    @JvmInline
    value class Checker(val value: String)

    @JvmInline
    value class Senior(val value: String)

    @JvmInline
    value class ShortName(val value: String)


    @JvmInline
    value class Point(val value: Int)

    @JvmInline
    value class Title(val value: String)
}