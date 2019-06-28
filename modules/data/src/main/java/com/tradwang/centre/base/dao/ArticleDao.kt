package com.tradwang.centre.base.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tradwang.centre.entry.ArticleBean

@Dao
interface ArticleDao {

    @Query("select * FROM article")
    fun queryAll(): LiveData<MutableList<ArticleBean>>

    @Query("select * FROM article where author= :author ")
    fun queryByAuthor(author: String): LiveData<MutableList<ArticleBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg articleBean: ArticleBean)
}