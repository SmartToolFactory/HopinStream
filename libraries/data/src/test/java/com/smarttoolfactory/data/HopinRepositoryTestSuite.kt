package com.smarttoolfactory.data

import com.smarttoolfactory.data.repository.LoginRepositoryTest
import com.smarttoolfactory.data.repository.StageRepositoryTest
import com.smarttoolfactory.data.source.LocalLoginDataSourceTest
import com.smarttoolfactory.data.source.RemoteLoginDataSourceTest
import com.smarttoolfactory.data.source.StagesDataSourceTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

// Runs all unit tests with JUnit4.
@RunWith(Suite::class)
@Suite.SuiteClasses(
    LoginRepositoryTest::class,
    StageRepositoryTest::class
)
class HopinRepositoryTestSuite

// Runs all unit tests with JUnit4.
@RunWith(Suite::class)
@Suite.SuiteClasses(
    LocalLoginDataSourceTest::class,
    RemoteLoginDataSourceTest::class,
    StagesDataSourceTest::class
)
class HopinDataSourceTestSuite
