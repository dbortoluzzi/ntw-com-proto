package eu.dbortoluzzi.commons.utils;

import eu.dbortoluzzi.commons.model.Fragment;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MD5FragmentValidationStrategy implements FragmentValidationStrategy{
    @SneakyThrows
    @Override
    public boolean isValid(Fragment fragment) {
        return StringUtils.md5sum(fragment.getPayload().getText()).equals(fragment.getPayload().getMetadata().getChecksum());
    }

    @Override
    public byte[] decodeFragment(Fragment fragment) {
        return StringUtils.decodeHex(fragment.getPayload().getText());
    }
}
