package com.example.carbondatapoc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Slf4j
public class CarbondataPocRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
            log.info("Carbondata Poc");
    }

}
