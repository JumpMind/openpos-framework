package org.jumpmind.pos.server.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Cached message not found")
public class CachedMessageNotFoundException extends RuntimeException {
}
