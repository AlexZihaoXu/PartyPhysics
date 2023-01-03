package site.alex_xu.dev.game.party_physics.game.engine.framework;

import java.io.InputStream;

public class ResourceManager {
    public static InputStream get(String path) {
        return ResourceManager.class.getClassLoader().getResourceAsStream(path);
    }
}
