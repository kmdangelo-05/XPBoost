package me.titanet.kmdangelo.xPBoost.boosters;
import lombok.Getter;

@Getter
public class Booster {

    private final double multiplier;
    private long durationInMillis;
    private boolean updated;

    public Booster(double multiplier, long durationInMillis) {
        this.multiplier = multiplier;
        this.durationInMillis = durationInMillis;
        updated = false;
    }


    public void addDuration(long durationInMillis){
        this.durationInMillis += durationInMillis;
        update();
    }

    public void removeDuration(long durationInMillis){
        this.durationInMillis -= durationInMillis;
        update();
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

}
