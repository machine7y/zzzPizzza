package com.example.zzzpizzza.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.zzzpizzza.data.local.db.dao.PizzaDao
import com.example.zzzpizzza.data.local.db.entity.PizzaEntity
import com.example.zzzpizzza.data.local.db.entity.VariantEntity

@Database(entities = [PizzaEntity::class, VariantEntity::class], version = 2)
abstract class Database : RoomDatabase() {

    abstract fun pizzaDao(): PizzaDao
}
