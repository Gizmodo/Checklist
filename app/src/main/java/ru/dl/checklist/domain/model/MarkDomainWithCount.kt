package ru.dl.checklist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarkDomainWithCount(
    val id: Long,
    val points: Int,
    val title: String,
    val answer: Int,
    val comment: String,
    val pkd: String,
    val count: Int
) : Parcelable
