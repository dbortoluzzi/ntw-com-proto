package eu.dbortoluzzi.commons.utils;

import eu.dbortoluzzi.commons.model.Fragment;

public interface FragmentValidationStrategy {
    boolean isValid(Fragment fragment);
    byte[] decodeFragment(Fragment fragment);
}
