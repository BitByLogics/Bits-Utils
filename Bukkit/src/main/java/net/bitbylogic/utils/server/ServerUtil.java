package net.bitbylogic.utils.server;

public class ServerUtil {

    public static boolean isPaper() {
        boolean isPaper = false;

        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {

        }

        return isPaper;
    }

}
