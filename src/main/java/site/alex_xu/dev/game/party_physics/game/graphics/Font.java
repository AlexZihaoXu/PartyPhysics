package site.alex_xu.dev.game.party_physics.game.graphics;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * A wrap up of java's awt Font class
 */
public class Font {
    java.awt.Font awtFont;
    /**
     * An array that cashes awt fonts with different sizes
     */
    java.awt.Font[] sizeList = new java.awt.Font[512];

    /**
     * A hashmap that cashes all loaded fonts
     */
    private static final HashMap<String, Font> cache = new HashMap<>();

    public static final Font DEFAULT = new Font(new JLabel().getFont());

    Font(java.awt.Font font) {
        awtFont = font;
    }

    /**
     * @param size size
     * @return the awt font of the given size
     */
    public java.awt.Font getAwt(int size) {
        if (sizeList[size] == null) {
            sizeList[size] = awtFont.deriveFont((float) size);
        }
        return sizeList[size];
    }

    /**
     * @param path the path of the font
     * @return the font of the given size
     */
    public static Font get(String path) {
        if (!cache.containsKey(path)) {
            try {
                InputStream stream = Font.class.getClassLoader().getResourceAsStream(path);
                assert stream != null;
                cache.put(path, new Font(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, stream)));
            } catch (NullPointerException | IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
        return cache.get(path);
    }

}
