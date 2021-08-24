package eu.dbortoluzzi.producer;

import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.model.Metadata;
import eu.dbortoluzzi.commons.model.Payload;
import eu.dbortoluzzi.commons.utils.FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.MD5FragmentValidationStrategy;
import eu.dbortoluzzi.commons.utils.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
@Slf4j
public class ProducerFragmentService {

    private final FragmentValidationStrategy fragmentValidationStrategy;

    public ProducerFragmentService() {
        this.fragmentValidationStrategy = new MD5FragmentValidationStrategy();
    }

    public boolean isValidFragment(Fragment fragment) {
        return fragmentValidationStrategy.isValid(fragment);
    }

    public byte[] decodeFragment(Fragment fragment) {
        return fragmentValidationStrategy.decodeFragment(fragment);
    }

    @SneakyThrows
    public Fragment createFragment(Integer index, Integer total, String instance, byte[] text) {
        String textEncoded = StringUtils.encodeHexString(text);
        return Fragment.builder()
                .index(index)
                .total(total)
                .timestamp(new Date())
                .payload(
                        Payload.builder()
                                .text(textEncoded)
                                .metadata(
                                        Metadata.builder()
                                                .instance(instance)
                                                .checksum(StringUtils.encodeHexString(MessageDigest.getInstance("MD5").digest(textEncoded.getBytes(StandardCharsets.UTF_8))))
                                                .build()
                                )
                                .build()
                )
                .build();
    }

}
