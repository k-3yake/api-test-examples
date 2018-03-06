package org.k3yake.city.repository

import org.dbunit.Assertion
import org.dbunit.database.DatabaseConnection
import org.dbunit.dataset.DefaultDataSet
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.excel.XlsDataSet
import org.dbunit.operation.DatabaseOperation
import org.junit.Test
import org.junit.runner.RunWith
import org.k3yake.domain.CityDomain
import org.k3yake.Application
import org.k3yake.repository.CityDomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.Closeable
import java.io.File
import java.sql.Connection
import javax.sql.DataSource
import org.assertj.core.api.Assertions.*


/**
 * Created by katsuki-miyake on 18/03/03.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class))
@AutoConfigureTestDatabase
class CityRepositoryTestByDbUnit {
    @Autowired lateinit var cityDomainRepository: CityDomainRepository
    @Autowired lateinit var dataSource:DataSource

    @Test
    fun 名前によるCity取得のテスト_名前の一致したcityを返す(){
        //準備
        ClosableConnection(dataSource.connection).use {
            val xlsDataSet = XlsDataSet(File("src/test/resources/testData.xlsx"))
            val city = DefaultDataSet(xlsDataSet.getTable("city"))
            val country = DefaultDataSet(xlsDataSet.getTable("country"))
            DatabaseOperation.DELETE_ALL.execute(it, city)
            DatabaseOperation.DELETE_ALL.execute(it, country)
            DatabaseOperation.INSERT.execute(it, country)
            DatabaseOperation.INSERT.execute(it, city)
        }

        //実行
        val city = cityDomainRepository.findCity("ebisu")

        //確認
        assertThat(city).isEqualTo(CityDomain(1,"ebisu","Japan"))
    }

    @Test
    fun Cityの保存のテスト_Countryがまだない場合_CityとCountryが登録される(){
        //準備
        ClosableConnection(dataSource.connection).use {
            val xlsDataSet = XlsDataSet(File("src/test/resources/testData.xlsx"))
            val city = DefaultDataSet(xlsDataSet.getTable("city"))
            val country = DefaultDataSet(xlsDataSet.getTable("country"))
            DatabaseOperation.DELETE_ALL.execute(it, city)
            DatabaseOperation.DELETE_ALL.execute(it, country)
            DatabaseOperation.INSERT.execute(it, country)
            DatabaseOperation.INSERT.execute(it, city)
        }

        //実行
        cityDomainRepository.create(CityDomain(name = "notExistCityName", country = "notExistCoutry"))

        //確認
        val databaseDataSet = DatabaseConnection(dataSource.connection).createDataSet()
        val expectedDataSet = ReplacementDataSet(XlsDataSet(File("src/test/resources/testData_expect.xlsx")))
        expectedDataSet.addReplacementSubstring("[EmptyStr]","")
        Assertion.assertEqualsIgnoreCols(expectedDataSet.getTable("city"), databaseDataSet.getTable("city"), arrayOf("id","country_id"));
        Assertion.assertEqualsIgnoreCols(expectedDataSet.getTable("country"), databaseDataSet.getTable("country"), arrayOf("id"));
    }

    @Test
    fun Cityの保存のテスト_countryが既にある場合_Cityのみが登録される(){
        //準備
        ClosableConnection(dataSource.connection).use {
            val xlsDataSet = XlsDataSet(File("src/test/resources/testData.xlsx"))
            val city = DefaultDataSet(xlsDataSet.getTable("city"))
            val country = DefaultDataSet(xlsDataSet.getTable("country"))
            DatabaseOperation.DELETE_ALL.execute(it, city)
            DatabaseOperation.DELETE_ALL.execute(it, country)
            DatabaseOperation.INSERT.execute(it, country)
        }

        //実行
        cityDomainRepository.create(CityDomain(name = "notExistCityName", country = "Japan"))

        //確認
        val databaseDataSet = DatabaseConnection(dataSource.connection).createDataSet()
        val expectedDataSet = ReplacementDataSet(XlsDataSet(File("src/test/resources/testData_expect2.xlsx")))
        expectedDataSet.addReplacementSubstring("[EmptyStr]","")
        Assertion.assertEqualsIgnoreCols(expectedDataSet.getTable("city"), databaseDataSet.getTable("city"), arrayOf("id","country_id"));
        Assertion.assertEqualsIgnoreCols(expectedDataSet.getTable("country"), databaseDataSet.getTable("country"), arrayOf("id"));
    }
}

class ClosableConnection(connection: Connection) : DatabaseConnection(connection),Closeable {


}