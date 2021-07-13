package org.jumpmind.pos.light;

public enum LYNXLaneLightCommand {
    
    OFF(new byte[] { 0x59, 0x04, (byte)0xFF, (byte)0xFB }),
    RED(new byte[] { 0x59, 0x04, (byte)0xDE, (byte)0x86 }),
    RED_FLASHING(new byte[] { 0x59, 0x04, (byte)0xDD, (byte)0x64 }),
    GREEN(new byte[] { 0x59, 0x04, (byte)0xFE, (byte)0xA5 }),
    GREEN_FLASHING(new byte[] { 0x59, 0x04, (byte)0xFD, (byte)0x47 }),
    BLUE(new byte[] { 0x59, 0x04, (byte)0xEE, (byte)0x38 });

    public final byte command[];

    private LYNXLaneLightCommand(byte[] command) {
        this.command = command;
    }

}
