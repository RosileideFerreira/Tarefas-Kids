package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ChildEntity::class,
        TaskEntity::class,
        CompletionEntity::class,
        RedemptionEntity::class,
        ParentAccountEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class TarefasDatabase : RoomDatabase() {

    abstract fun tarefasDao(): TarefasDao

    companion object {
        @Volatile
        private var INSTANCE: TarefasDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN targetWeek INTEGER DEFAULT NULL")
            }
        }

        fun getInstance(context: Context): TarefasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TarefasDatabase::class.java,
                    "tarefas_kids.db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
