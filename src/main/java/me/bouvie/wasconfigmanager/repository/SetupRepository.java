package me.bouvie.wasconfigmanager.repository;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.bouvie.wasconfigmanager.setup.AbstractSetupInfo;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class SetupRepository {
    public List<AbstractSetupInfo> list() throws Exception {
        MappingIterator<AbstractSetupInfo> objectMappingIterator = new ObjectMapper().reader().
                forType(AbstractSetupInfo.class)
                .readValues(jsonList());
        return objectMappingIterator.readAll();
    }

    public String jsonList() throws Exception {
        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(SetupRepository.class.getResource("/polo_config.json").toURI()));
        return bufferedReader.lines().reduce((x,y)->x+"\n"+y).orElse("[]");
    }
}
