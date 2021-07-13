package org.jumpmind.pos.light;

public enum LaneLightStatus {
    
    OFF("OFF"),
    RED("RED"),
    RED_FLASHING("RED_FLASHING"),
    GREEN("GREEN"),
    GREEN_FLASHING("GREEN_FLASHING"),
    BLUE("BLUE");

    public final String label;

    private LaneLightStatus(String label) {
        this.label = label;
    }

}
