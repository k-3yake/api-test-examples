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
import org.k3yake.deleteAll
import org.k3yake.repository.PopulationApi
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean

/**
 * Created by katsuki-miyake on 18/02/24.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class))
@AutoConfigureTestDatabase
class CityDomainRepositoryTest {
    @Autowired
    lateinit var cityDomainRepository: CityDomainRepository
    @Autowired
    lateinit var dataSource:DataSource
    @MockBean
    lateinit var populationApi: PopulationApi

    @Test
    fun Cityの保存のテスト_Countryがまだない場合_CityとCountryが登録される(){
        //準備
        dbSetup(to = dataSource) {
            deleteAll()
        }.launch()
        given(populationApi.get("name1")).willReturn(PopulationApi.PopulationApiResponse("name1",90))

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
    fun Cityの保存のテスト_countryが既にある場合_Cityのみが登録される(){
        //準備
        dbSetup(to = dataSource) {
            deleteAll()
            insertInto("country"){
                columns("id", "name")
                values(1, "Japan")
            }
        }.launch()
        given(populationApi.get("name1")).willReturn(PopulationApi.PopulationApiResponse("name1",90))

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
    fun City取得のテスト_同じ名前のCityが既にある場合_Cityを返す(){
        //準備
        dbSetup(to = dataSource) {
            deleteAll()
            insertInto("country"){
                columns("id", "name")
                values(1, "Japan")
            }
            insertInto("city"){
                columns("id", "name", "country_id")
                values(1, "ebisu" ,1)
            }
        }.launch()

        //実行
        val result = cityDomainRepository.find("ebisu")

        //確認
        assertThat(result).isEqualTo(CityDomain(1,"ebisu","Japan"))
    }
}