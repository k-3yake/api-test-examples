package org.k3yake


import com.ninja_squad.dbsetup_kotlin.dbSetup
import org.assertj.db.api.Assertions
import org.assertj.db.type.Table
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.k3yake.Application
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import javax.sql.DataSource


/**
 * Created by katsuki-miyake on 18/03/06.
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(Application::class))
@AutoConfigureTestDatabase
class CityApiTestBySpringBootTest {

    @Autowired
    lateinit var wac: WebApplicationContext
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var dataSource: DataSource

    @Before
    fun setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    fun postTest_未登録の都市の場合_() {
        //準備
        dbSetup(to = dataSource) {
            deleteAllFrom("city", "country")
            insertInto("country") {
                columns("id", "name")
                values(1, "Japan")
            }
        }.launch()

        //実行
        this.mockMvc.perform(post("/city")
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
    }

/*
    @Test
    fun postTest_nameがない場合_エラーになる() {
        this.mockMvc.perform(post("/city")
                .content("""{"country":"Japan"}""".toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().`is`(400))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("""{"message":"Empty name."}"""))
        Assertions.assertThat(Table(dataSource, "city", arrayOf(Table.Order.asc("id")))).hasNumberOfRows(1)
    }
*/
}