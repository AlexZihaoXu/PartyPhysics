package site.alex_xu.dev.game.party_physics.game.graphics;

import javax.swing.*;

public class Font {
    java.awt.Font awtFont;
    java.awt.Font[] sizeList = new java.awt.Font[512];

    public static final Font DEFAULT = new Font(new JLabel().getFont());

    Font(java.awt.Font font) {
        awtFont = font;
    }

    public java.awt.Font getAwt(int size) {
        if (sizeList[size] == null) {
            sizeList[size] = awtFont.deriveFont((float) size);
        }
        return sizeList[size];
    }

}
