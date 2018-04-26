package model;

import java.time.temporal.ChronoUnit;

public class ArchivePeriod {

    private final long archivePeriod;
    private final ChronoUnit unit;

    public ArchivePeriod(long archivePeriod, ChronoUnit unit) {
        this.archivePeriod = archivePeriod;
        this.unit = unit;
    }

    public long getArchivePeriod() {
        return archivePeriod;
    }

    public String getUnit() {
        return unit.toString().toUpperCase();
    }
}
