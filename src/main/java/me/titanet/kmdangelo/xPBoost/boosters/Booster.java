package me.titanet.kmdangelo.xPBoost.boosters;
import lombok.Getter;
import me.titanet.kmdangelo.xPBoost.utility.TimeStamp;

import java.util.UUID;

@Getter
public class Booster {

    private final double multiplier;
    private final UUID boosterUuid;
    private final boolean onlineOnly;
    private final long startingDurationInMillis;

    private long durationInMillis;
    private boolean updated;

    public Booster(UUID boosterUuid, double multiplier, long durationInMillis, boolean onlineOnly) {
        this.multiplier = multiplier;
        this.durationInMillis = durationInMillis;
        updated = false;
        this.boosterUuid = boosterUuid;
        this.onlineOnly = onlineOnly;
        startingDurationInMillis = durationInMillis;
    }

    public Booster(UUID boosterUuid, double multiplier, long durationInMillis, long startingDurationInMillis, boolean onlineOnly) {
        this.multiplier = multiplier;
        this.durationInMillis = durationInMillis;
        updated = false;
        this.boosterUuid = boosterUuid;
        this.onlineOnly = onlineOnly;
        this.startingDurationInMillis = startingDurationInMillis;
    }

    public void addDuration(long durationInMillis){
        this.durationInMillis += durationInMillis;
        update();
    }

    public void removeDuration(long durationInMillis){
        this.durationInMillis -= durationInMillis;
        update();
    }

    public boolean isDurationExpired() {
        return (durationInMillis <= 0);
    }

    public void update(){
            this.updated = true;
    }

    public void unUpdate() {
        this.updated = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return multiplier == ((Booster) o).getMultiplier();
    }

    @Override
    public String toString() {
        return multiplier+"x_"+ TimeStamp.formattingTimeWithoutSpaces(startingDurationInMillis/1000);
    }

}
