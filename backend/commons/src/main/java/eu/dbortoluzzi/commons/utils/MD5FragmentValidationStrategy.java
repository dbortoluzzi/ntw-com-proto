package eu.dbortoluzzi.commons.utils;

import eu.dbortoluzzi.commons.model.Fragment;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class MD5FragmentValidationStrategy implements FragmentValidationStrategy{
    @SneakyThrows
    @Override
    public boolean isValid(Fragment fragment) {
        byte[] textDigest = MessageDigest.getInstance("MD5").digest(fragment.getPayload().getText().getBytes(StandardCharsets.UTF_8));
        return StringUtils.encodeHexString(textDigest).equals(fragment.getPayload().getMetadata().getChecksum());
    }

    @Override
    public byte[] decodeFragment(Fragment fragment) {
        return StringUtils.decodeHex(fragment.getPayload().getText());
    }
}
