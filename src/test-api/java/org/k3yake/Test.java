package org.k3yake;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by katsuki-miyake on 18/03/07.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class Test {

    @Autowired
    private Controller controller;

    @MockBean
    private Service service;

    @org.junit.Test
    public void exampleControllerTest() throws Exception {
        if(!controller.get().equals("do service")) throw new RuntimeException("Not Equale");
        if(!service.execService().equals("do service")) throw new RuntimeException("Not Equale");
        //mockServer.perform(MockMvcRequestBuilders.get("/service"));
    }
}
