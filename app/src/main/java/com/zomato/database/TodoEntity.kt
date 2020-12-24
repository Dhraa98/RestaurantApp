package com.zomato.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "todo_items")
class TodoEntity {

    @PrimaryKey(autoGenerate = true)
    var Id: Int = 0

    @ColumnInfo(name = "listingName")
    var listingName: String = ""

    @ColumnInfo(name = "minPrice")
    var minPrice: String = ""

    @ColumnInfo(name = "avgRating")
    var avgRating: String=""

    @ColumnInfo(name = "cuisin")
    var cuisin: String=""

    @ColumnInfo(name = "imagePath")
    var imagePath : String=""





}