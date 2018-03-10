package org.k3yake


import com.ninja_squad.dbsetup.Operations.*
import com.ninja_squad.dbsetup.operation.Operation
import com.ninja_squad.dbsetup_kotlin.DbSetupBuilder
import com.ninja_squad.dbsetup_kotlin.dbSetup
import org.assertj.db.api.Assertions
import org.assertj.db.type.Table
import org.junit.Test
import org.junit.runner.RunWith
import org.k3yake.repository.PopulationApi
import org.mockito.BDDMockito.given
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import javax.sql.DataSource


/**
 * Created by katsuki-miyake on 18/03/06.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class CityApiTestBySpringBootTest {

    @Autowired
    lateinit var mockServer: MockMvc
    @Autowired
    lateinit var dataSource: DataSource
    @MockBean
    lateinit var populationApi: PopulationApi

    @Test
    fun postTest_未登録の都市の場合_人口情報を付与して登録する() {
        //準備
        dbSetup(to = dataSource) {
            deleteAll()
            insertInto("country") {
                columns("id", "name")
                values(1, "Japan")
            }
        }.launch()
        given(populationApi.get("ebisu")).willReturn(PopulationApi.PopulationApiResponse("ebisu", 900000))

        //実行
        mockServer.perform(MockMvcRequestBuilders.post("/city")
                .content("""{"name":"ebisu", "country":"Japan"}""".toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("""{id=1}""".toString()))

        //確認
        Assertions.assertThat(Table(dataSource, "country"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("Japan")
        Assertions.assertThat(Table(dataSource, "city"))
                .hasNumberOfRows(1)
                .row(0)
                .value("name").isEqualTo("ebisu")
                .value("population").isEqualTo(900000)
    }

    @Test
    fun postTest_未登録の都市の場合_人口情報を取得出来ない場合エラーとなる() {
        //準備
        dbSetup(to = dataSource) {
            /*
                        deleteAllFrom("city", "country")
                        sql("ALTER TABLE city ALTER COLUMN id RESTART WITH 1")
            */
            deleteAll()
            insertInto("country") {
                columns("id", "name")
                values(1, "Japan")
            }
        }.launch()
        given(populationApi.get("ebisu")).willThrow(RuntimeException())

        //実行
        mockServer.perform(MockMvcRequestBuilders.post("/city")
                .content("""{"name":"ebisu", "country":"Japan"}""".toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())

        //確認
        Assertions.assertThat(Table(dataSource, "country"))
                .hasNumberOfRows(1)
                .row(0)
        Assertions.assertThat(Table(dataSource, "city"))
                .hasNumberOfRows(0)
    }
}

fun DbSetupBuilder.deleteAll() {
    val tables = listOf("city", "country")
    deleteAllFrom(tables)
    tables.forEach {
        sql("ALTER TABLE ${it} ALTER COLUMN id RESTART WITH 1")
    }
}

