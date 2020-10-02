package org.jumpmind.pos.util.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bytecode.Throw;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ServiceVisit {

    private String profileId;
    private long elapsedTimeMillis;
    private Throwable exception;

}
