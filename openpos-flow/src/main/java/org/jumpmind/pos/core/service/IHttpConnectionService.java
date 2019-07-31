package org.jumpmind.pos.core.service;


import java.io.IOException;

public interface IHttpConnectionService {
    byte[] get(String url) throws IOException;
}
