package site.alex_xu.dev.game.party_physics.game.utils;

public class Clock {
    private static final double startTime = System.nanoTime() / 1e9d;

    public static double currentTime() {
        return System.nanoTime() / 1e9d - startTime;
    }

    private double record = currentTime();

    public void reset() {
        record = currentTime();
    }

    public double deltaTime() {
        double now = currentTime();
        double dt = record;
        record = now;
        return dt;
    }

    public double elapsedTime() {
        return currentTime() - record;
    }

}
