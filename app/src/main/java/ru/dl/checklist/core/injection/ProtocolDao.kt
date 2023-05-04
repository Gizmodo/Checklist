package ru.dl.checklist.core.injection

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

@Dao
interface ProtocolDao {
    @Query("select * from inventory")
    fun selectAll(): List<InvItem>
}

@Entity(tableName = "inventory")
data class InvItem(
    @ColumnInfo(name = "code") var code: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "plu") var plu: String,
    @ColumnInfo(name = "barcode") var barcode: String,
    @ColumnInfo(name = "quantity") var quantity: String,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null

    constructor() : this("", "", "", "", "")
}
