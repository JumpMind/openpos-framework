package org.jumpmind.pos.util;

import org.apache.commons.lang3.BooleanUtils;
import org.jumpmind.pos.util.web.ServerException;

import java.util.Arrays;

public class BoolUtils {

    public static boolean toBoolean(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Number) {
            return ((Number)object).longValue() > 0;
        } else if (object instanceof String) {
            return isTruthy((String)object);
        } else if (object instanceof Boolean) {
            return ((Boolean)object).booleanValue();
        }

        throw new ServerException("Can't convert object to a boolean " + object);

    }

    /**
     * Evaluates the given string to determine if it is a "truthy" value.  Truthy values
     * are the following (case-insensitive): {@code 1}, {@code on}, {@code y}, {@code yes}, {@code t}, {@code true}
     * Allows passing an optional array of extra values to also be considered truthy.
     * @param boolString The string to evaluate
     * @param extraPermittedTruthys An optional array of values to also be considered truthy. The given {@code boolString} is
     *   compared to these values in a case-insensitive manner. If the value of {@code boolString} is found in this array,
     *   {@link Boolean#TRUE} is returned.
     * @return {@link Boolean#TRUE} if truthy, {@link Boolean#FALSE} if {@code null} or the given string is any other value
     *   than the aforementioned case-insensitive truthy values with the optional evaluation of
     *   the given {@code boolString} against the set of optional {@code extraPermittedTruthys}.
     */
    public static Boolean isTruthy(String boolString, String... extraPermittedTruthys) {
        if (boolString == null) {
            return false;
        }

        final String boolStr = boolString.trim();
        Boolean truthy = BooleanUtils.toBoolean(boolStr) || "1".equals(boolStr);
        if (! truthy && extraPermittedTruthys != null && extraPermittedTruthys.length > 0) {
            truthy = Arrays.asList(extraPermittedTruthys).stream().anyMatch(b -> boolStr.equalsIgnoreCase(b));
        }
        return truthy;
    }
}
