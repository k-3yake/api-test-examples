package org.k3yake.city.repository

import com.ninja_squad.dbsetup_kotlin.dbSetup
import org.assertj.db.api.Assertions
import org.assertj.db.type.Changes
import org.assertj.db.type.Table
import org.junit.Test
import org.junit.runner.RunWith
import org.k3yake.domain.CityDomain
import org.k3yake.Application
import org.k3yake.repository.CityDomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.sql.DataSource
import org.assertj.core.api.Assertions.*

/**
 * Created by katsuki-miyake on 18/02/24.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class))
@AutoConfigureTestDatabase
class CityRepositoryTestByDbSetupAndAssertJDB {
    @Autowired lateinit var cityDomainRepository: CityDomainRepository
    @Autowired lateinit var dataSource:DataSource

    @Test
    fun 名前によるCity取得のテスト_名前の一致したcityを返す(){
        //準備
        dbSetup(to = dataSource) {
            deleteAllFrom("city","country")
            insertInto("country"){
                columns("id", "name")
                values(1, "Japan")
            }
            insertInto("city"){
                columns("id", "name", "country_id")
                values(1, "Ebisu", 1)
            }
        }.launch()

        //実行
        val city = cityDomainRepository.findCity("Ebisu")

        //確認
        assertThat(city).isEqualTo(CityDomain(1,"Ebisu","Japan"))
    }

    @Test
    fun Cityの保存のテスト_Countryがまだない場合_CityとCountryが登録される_テーブルの状態確認によるテスト(){
        //準備
        dbSetup(to = dataSource) {
            deleteAllFrom("city","country")
        }.launch()

        //実行
        val city = CityDomain(name = "name1", country = "notExistCountry")
        cityDomainRepository.create(city)

        //確認
        Assertions.assertThat(Table(dataSource, "country"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("notExistCountry")
        Assertions.assertThat(Table(dataSource, "city"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("name1")
    }

    @Test
    fun Cityの保存のテスト_Countryがまだない場合_CityとCountryが登録される_テーブルの変更確認によるテスト(){
        //準備
        dbSetup(to = dataSource) {
            deleteAllFrom("city","country")
        }.launch()
        val changes = Changes(dataSource).setStartPointNow() //AssetJ-DBによる変更記録開始

        //実行
        cityDomainRepository.create(CityDomain(name = "name1", country = "notExistCountry"))

        //確認
        changes.setEndPointNow() //AssetJ-DBによる変更記録終了
        Assertions.assertThat(changes)
                .hasNumberOfChanges(2)
                .changeOnTable("country")
                .isCreation()
                .rowAtEndPoint()
                .value("name").isEqualTo("notExistCountry")
                .changeOnTable("city")
                .isCreation()
                .rowAtEndPoint()
                .value("name").isEqualTo("name1")
    }


    @Test
    fun Cityの保存のテスト_countryが既にある場合_Cityのみが登録される_テーブルの状態確認によるテスト(){
        //準備
        dbSetup(to = dataSource) {
            deleteAllFrom("city","country")
            insertInto("country"){
                columns("id", "name")
                values(1, "Japan")
            }
        }.launch()

        //実行
        cityDomainRepository.create(CityDomain(name = "name1", country = "Japan"))

        //確認
        Assertions.assertThat(Table(dataSource, "country"))
                .hasNumberOfRows(1) //行数は確認しているが変更されていないことを確認していない。手抜きでござる
        Assertions.assertThat(Table(dataSource, "city"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("name1")
                .value("country_id").isEqualTo(1)
    }

    @Test
    fun Cityの保存のテスト_countryが既にある場合_Cityのみが登録される_テーブルの変更確認によるテスト(){
        //準備
        dbSetup(to = dataSource) {
            deleteAllFrom("city","country")
            insertInto("country"){
                columns("id", "name")
                values(1, "Japan")
            }
        }.launch()
        val changes = Changes(dataSource).setStartPointNow() //AssetJ-DBによる変更記録開始

        //実行
        cityDomainRepository.create(CityDomain(name = "name1", country = "Japan"))

        //確認
        changes.setEndPointNow()
        Assertions.assertThat(changes)
                .hasNumberOfChanges(1)//下のアサーションと組み合わせて、cityテーブルのみ変更されていること（countryが変更されていないこと）が確認出来ている
                .changeOnTable("city")
                .isCreation()
                .rowAtEndPoint()
                .value("name").isEqualTo("name1")
    }
}