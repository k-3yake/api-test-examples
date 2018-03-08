package org.k3yake;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created by katsuki-miyake on 18/03/07.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class Test {

/*
    @Autowired
    private Controller controller;
*/
    @Autowired
    private MockMvc mockServer;

    @MockBean
    private Service service;


    @org.junit.Test
    public void exampleControllerTest() throws Exception {
        given(this.service.execService()).willReturn("mocked!");
        String result = mockServer.perform(MockMvcRequestBuilders.get("/service")).andReturn().getResponse().getContentAsString();
        assertThat(result).isEqualTo("mocked!");
    }
}
