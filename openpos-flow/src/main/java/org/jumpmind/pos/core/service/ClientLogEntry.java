package org.jumpmind.pos.core.service;

import java.io.Serializable;

public class ClientLogEntry implements Serializable {
    ClientLogType type;
    String message;
}
