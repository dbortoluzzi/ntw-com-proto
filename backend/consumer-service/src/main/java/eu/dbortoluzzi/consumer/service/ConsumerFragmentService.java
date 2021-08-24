package eu.dbortoluzzi.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.utils.FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.MD5FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.StringUtils;
import eu.dbortoluzzi.consumer.model.MongoFragment;
import eu.dbortoluzzi.consumer.repository.FragmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class ConsumerFragmentService {

    private final ObjectMapper objectMapper;
    final FragmentRepository fragmentRepository;

    private final FragmentValidationStrategy fragmentValidationStrategy;

    public ConsumerFragmentService(FragmentRepository fragmentRepository, ObjectMapper objectMapper) {
        this.fragmentRepository = fragmentRepository;
        this.objectMapper = objectMapper;
        this.fragmentValidationStrategy = new MD5FragmentValidationStrategy();
    }

    public MongoFragment addFragment(String hexStr, Date receivingDate) throws IOException {
        String dataStr = new String(StringUtils.decodeHex(hexStr)); // TODO: add cypher
        Fragment fragment = objectMapper.readValue(dataStr.getBytes(StandardCharsets.UTF_8), Fragment.class);
        if (isValidFragment(fragment)) {
            return addFragment(fragment, receivingDate);
        } else {
            throw new IllegalStateException("INVALID FRAGMENT");
        }
    }

    public MongoFragment addFragment(Fragment fragment, Date receivingDate) {
        return fragmentRepository.insert(new MongoFragment(fragment, receivingDate));
    }

    public boolean isValidFragment(Fragment fragment) {
        return fragmentValidationStrategy.isValid(fragment);
    }
}
