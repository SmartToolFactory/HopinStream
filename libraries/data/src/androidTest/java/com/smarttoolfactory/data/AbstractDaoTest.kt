package com.smarttoolfactory.data

import androidx.annotation.CallSuper
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.smarttoolfactory.data.db.HopinDatabase
import java.util.concurrent.Executors
import org.junit.After
import org.junit.Before

open class AbstractDaoTest(
    private val inMemoryDatabase: Boolean = true,
    private val allowMainThreadQueries: Boolean = false,
) {

    internal lateinit var database: HopinDatabase

    @CallSuper
    @Before
    open fun setUp() {

        // using an in-memory database because the information stored here disappears after test
        val builder = if (inMemoryDatabase) {
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(), HopinDatabase::class.java
            )
        } else {
            Room.databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                HopinDatabase::class.java,
                "test.db"
            )
                .fallbackToDestructiveMigration()
        }
            // 🔥🔥🔥 Without this Coroutines tests with @Transaction get stuck
            .setTransactionExecutor(Executors.newSingleThreadExecutor())

        // allowing main thread queries, just for testing
        if (allowMainThreadQueries) {
            builder.allowMainThreadQueries()
        }

        database = builder.build()
    }

    @CallSuper
    @After
    @Throws(Exception::class)
    open fun tearDown() {
        database.close()
    }
}
