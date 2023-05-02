package com.itheima.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RegieeApplicationTests {

    @Test
    void contextLoads() {
        String password = "abc123";
        System.out.println(password == "abc123");
    }

}
