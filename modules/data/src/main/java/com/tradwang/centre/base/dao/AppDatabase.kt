package com.tradwang.centre.base.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tradwang.centre.entry.ArticleBean
import com.tradwang.common.base.App

/**
 * ProjectName   CommonStudy
 * PackageName  com.tradwang.centre.base.dao
 * @author  tradwang
 * @since   18-8-9   下午2:49.
 * @email  wangxiaojun@bianla.cn
 * @version  0.1
 * @describe  TODO
 */
@Database(entities = [ArticleBean::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        val appDatabase: AppDatabase by lazy { Room.databaseBuilder(App.getInstance(), AppDatabase::class.java, "article.db").build() }
    }

    abstract fun articleDao(): ArticleDao
}