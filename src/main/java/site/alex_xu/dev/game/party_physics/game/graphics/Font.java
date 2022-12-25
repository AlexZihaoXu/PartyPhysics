package site.alex_xu.dev.game.party_physics.game.graphics;

import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Font {

    private static class TextureMap {
        static int CACHE_SIZE = 64;
        static int GAP = 0;
        BufferedImage texture;
        int[] xOffsets = new int[CACHE_SIZE];

        int width = 0;
    }

    public static final Font DEFAULT = new Font(new java.awt.Font(null, java.awt.Font.PLAIN, 16));

    java.awt.Font awtFont;

    public HashMap<Integer, TextureMap> fontMap = new HashMap<>();

    Font(java.awt.Font font) {
        this.awtFont = font.deriveFont(java.awt.Font.PLAIN, 256);
    }

    private TextureMap getTextureMap(Graphics2D g, char c) {
        int index = ((int) (c)) / TextureMap.CACHE_SIZE;
        if (!fontMap.containsKey(index)) {
            Clock clock = new Clock();
            TextureMap textureMap = new TextureMap();
            FontMetrics metrics = g.getFontMetrics(awtFont);
            char[] chars = new char[TextureMap.CACHE_SIZE];
            for (int i = 0; i < TextureMap.CACHE_SIZE; i++) {
                int ch = TextureMap.CACHE_SIZE * index + i;
                int w = awtFont.canDisplay(ch) ? metrics.charWidth(ch) : TextureMap.GAP;
                textureMap.xOffsets[i] = textureMap.width;
                textureMap.width += w;
                chars[i] = (char) ch;
            }
            Clock c2 = new Clock();

            textureMap.texture = new BufferedImage(textureMap.width, metrics.getAscent() + metrics.getDescent(), BufferedImage.TYPE_INT_ARGB);
            System.out.printf("Took %.3f s to generated picture.\n", c2.elapsedTime());
            Graphics2D g2 = textureMap.texture.createGraphics();
            g2.setFont(awtFont);
            g2.setColor(Color.WHITE);
            for (int i = 0; i < TextureMap.CACHE_SIZE; i++) {
                int ch = TextureMap.CACHE_SIZE * index + i;
                if (awtFont.canDisplay(ch)) {
                    g2.drawString("" + ((char) ch), textureMap.xOffsets[i], metrics.getAscent());
                }
            }
            g2.dispose();

            double elapsed = clock.elapsedTime();

            try {
                ImageIO.write(textureMap.texture, "PNG", new File("output.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            fontMap.put(index, textureMap);
            System.out.println("Generated: " + String.format("%.3f s", elapsed));
        }
        return fontMap.get(index);
    }

    int height = 0;

    int render(Graphics2D g, char c, double x, double y) {
        TextureMap map = getTextureMap(g, c);
        int index = (int) (c) % TextureMap.CACHE_SIZE;
        int left = map.xOffsets[index];
        int right = index + 1 < map.xOffsets.length ? map.xOffsets[index + 1] : map.texture.getWidth();
        right -= TextureMap.GAP;
        int w = right - left;
        int h = map.texture.getHeight();
        g.drawImage(map.texture, (int) x, (int) y, (int) (x + w), h, left, 0, right, h, null);
        height = h;
        return w;
    }

}
