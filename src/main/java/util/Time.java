package util;

public class Time {
    public static float timeStarted = System.nanoTime(); //initialized on App startup because STATIC

    public static float getTime() {
        return (float) ((System.nanoTime() - timeStarted) * 1E-9);
    }
}
