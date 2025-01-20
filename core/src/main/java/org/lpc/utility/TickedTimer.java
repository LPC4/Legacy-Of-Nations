package org.lpc.utility;

public class TickedTimer {
    private int ticks;
    private final int delay;

    public TickedTimer(int delay) {
        this.delay = delay;
        this.ticks = 0;
    }

    public void tick() {
        ticks++;
    }

    public boolean isTime() {
        if (ticks >= delay) {
            ticks = 0;  // Reset ticks after the delay
            return true;
        }
        return false;
    }

    public int getProgressPercentage() {
        return (int) ((float) ticks / (float) delay * 100);
    }
}

