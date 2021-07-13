package org.jumpmind.pos.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileConfig implements Cloneable {

    protected int httpTimeout;
    protected int connectTimeout;
    protected String url;
    protected String apiToken;

    public ProfileConfig copy() {
        ProfileConfig copy;
        try {
            copy = (ProfileConfig)this.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
