package org.jumpmind.pos.util;

import org.apache.commons.lang3.StringUtils;

public class StackTraceUtils {

    public static String formatStackTrace(StackTraceElement[] stackTrace) {
        return formatStackTrace(stackTrace, 0, true);
    }

    public static String formatStackTrace(StackTraceElement[] stackTrace, int indentSpaces, boolean indentFirst) {
        StringBuilder buff = new StringBuilder(256);

        boolean first = true;

        for (StackTraceElement stackTraceElement : stackTrace) {
            if (!first || indentFirst) {
                buff.append(StringUtils.rightPad("", indentSpaces));
            } else {
                first = false;
            }
            buff.append(stackTraceElement.getClassName());
            buff.append(".");
            buff.append(stackTraceElement.getMethodName());
            buff.append("()");
            int lineNumber = stackTraceElement.getLineNumber();
            if (lineNumber > 0) {
                buff.append(":");
                buff.append(Integer.toString(stackTraceElement.getLineNumber()));
            }
            buff.append("\r\n");
        }
        return buff.toString();
    }

}
