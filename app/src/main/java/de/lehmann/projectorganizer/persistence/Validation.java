package de.lehmann.projectorganizer.persistence;

import java.util.regex.Pattern;

/**
 * An interface that holds global validation information.
 *
 * @author Tim Lehmann <l_@freenet.de />
 */

public interface Validation {

    Pattern ALPHANUMERICS = Pattern.compile("\\w+");

}
