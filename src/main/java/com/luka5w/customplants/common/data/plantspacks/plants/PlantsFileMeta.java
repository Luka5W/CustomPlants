package com.luka5w.customplants.common.data.plantspacks.plants;

public class PlantsFileMeta {
    
    private final int format;
    private final String version;
    
    public PlantsFileMeta(int format, String version) {
        this.format = format;
        this.version = version;
    }
    
    public int getFormat() {
        return format;
    }
    
    public String getVersion() {
        return version;
    }
}
