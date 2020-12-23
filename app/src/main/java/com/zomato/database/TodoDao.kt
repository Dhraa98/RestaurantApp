package com.zomato.database

import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items")
    fun getAll(): List<TodoEntity>


    @Insert
    fun insertAll(vararg todo: TodoEntity)

    @Delete
    fun delete(todo: TodoEntity)


    @Query("SELECT * FROM todo_items WHERE Id LIKE :todoId")
    fun deleteById(todoId: Int): TodoEntity



    @Update
    fun updateTodo(vararg todos: TodoEntity)

    //(onConflict = OnConflictStrategy.REPLACE)
}