package com.example.carbondatapoc;

import com.example.carbondatapoc.model.MultiTypePojo;
import com.example.carbondatapoc.reader.CarbonFileReader;
import com.example.carbondatapoc.writer.CarbonFileWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

@Slf4j
public class CarbondataPocRunner implements ApplicationRunner {


    @Autowired
    private PojoGenerator pojoGenerator;

    @Autowired
    private CarbonFileWriter carbonFileWriter;

    @Autowired
    private CarbonFileReader carbonFileReader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
            log.info("Carbondata Poc started");

            String path = "./carbondata-files";

        try {
            StatisticsCollector statisticsCollector = new StatisticsCollector();
            List<MultiTypePojo> testPojos = pojoGenerator.generate(MultiTypePojo.class, 1);

            carbonFileWriter.write(testPojos, statisticsCollector, path);

            List<MultiTypePojo> loadedPojos = carbonFileReader.read(path, null,
            MultiTypePojo.class, statisticsCollector);
            //CarbonDataUtils.testSdkWriter("true");
            //CarbonDataUtils.testReader();

            //Define Schema

            log.info("Test completed, loaded {} pojo(s)", loadedPojos.size());

        } catch (Exception e) {
            log.error("Carbondata writing failed", e);
        }
    }

}
