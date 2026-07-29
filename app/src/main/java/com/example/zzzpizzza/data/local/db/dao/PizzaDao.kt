package com.example.zzzpizzza.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.zzzpizzza.data.local.db.entity.PIZZA_TABLE
import com.example.zzzpizzza.data.local.db.entity.PizzaEntity
import com.example.zzzpizzza.data.local.db.entity.VariantEntity
import com.example.zzzpizzza.data.local.db.entity.join.PizzaWithVariantList

@Dao
abstract class PizzaDao {

    @Transaction
    @Query("SELECT * FROM $PIZZA_TABLE")
    abstract suspend fun getPizzaWithVariantList(): List<PizzaWithVariantList>

    // Variants are removed by the ON DELETE CASCADE foreign key.
    @Query("DELETE FROM $PIZZA_TABLE")
    abstract suspend fun deletePizzaList()

    @Insert
    abstract suspend fun insertPizzaList(pizzaList: List<PizzaEntity>)

    @Insert
    abstract suspend fun insertVariantList(variantList: List<VariantEntity>)

    @Transaction
    open suspend fun replacePizzaWithVariantList(itemList: List<PizzaWithVariantList>) {
        deletePizzaList()
        insertPizzaList(itemList.map { it.pizza })
        insertVariantList(itemList.flatMap { it.variantList })
    }
}
