package site.alex_xu.dev.game.party_physics.game.utils;

public class Clock {
    /**
     * The time when the program launched
     */
    private static final double startTime = System.nanoTime() / 1e9d;

    /**
     * @return the current time in seconds (since program launched)
     */
    public static double currentTime() {
        return System.nanoTime() / 1e9d - startTime;
    }

    private double record = currentTime();

    /**
     * Resets the clock
     */
    public void reset() {
        record = currentTime();
    }

    /**
     * Calculates the delta time and reset the clock
     * @return delta time in seconds
     */
    public double deltaTime() {
        double now = currentTime();
        double dt = record;
        record = now;
        return dt;
    }

    /**
     * @return elapsed time in seconds
     */
    public double elapsedTime() {
        return currentTime() - record;
    }

}
