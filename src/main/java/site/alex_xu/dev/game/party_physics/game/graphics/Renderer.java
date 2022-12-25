package site.alex_xu.dev.game.party_physics.game.graphics;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.LinkedList;

public class Renderer {

    private static final class State {

        Color color;

        AffineTransform trans;
        RenderingHints hints;
        Composite composite;
        Font font;
        double lineWidth = 1;



    }
    private final Graphics2D g;

    private final LinkedList<State> states = new LinkedList<>();

    private final int width, height;
    private double lineWidth = 1;

    private final Font font = Font.DEFAULT;

    private final int textSize = 16;

    private PartyPhysicsWindow window;

    public Renderer(PartyPhysicsWindow window, Graphics g, int width, int height) {
        this.g = (Graphics2D) g;
        this.window = window;
        this.width = width;
        this.height = height;
        setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    }


    // Setters

    public void setColor(Color color) {
        g.setColor(color);
    }
    public void setColor(int red, int green, int blue, int alpha) {

        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));
        alpha = Math.min(255, Math.max(0, alpha));

        g.setColor(new Color(red, green, blue, alpha));
    }

    public void setColor(int red, int green, int blue) {
        setColor(red, green, blue, 255);
    }

    public void setColor(int brightness) {
        setColor(brightness, brightness, brightness, 255);
    }


    public void setComposite(Composite composite) {
        g.setComposite(composite);
    }

    public void setRenderingHint(RenderingHints.Key hint, Object value) {
        g.setRenderingHint(hint, value);
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }


    // Others

    public void enableTextAA() {
        setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    }
    public void disableTextAA() {
        setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }


    // Getters

    public Font getFont() {
        return font;
    }
    Graphics2D getGraphics() {
        return g;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public int getTextSize() {
        return textSize;
    }

    public double getTextHeight() {
        return g.getFontMetrics().getHeight();
    }

    public double getTextWidth(String text) {
        return g.getFontMetrics().stringWidth(text);
    }

    // Render

    public void clear() {
        AffineTransform trans = (AffineTransform) g.getTransform().clone();
        g.fillRect(0, 0, width, height);
        g.setTransform(trans);
    }

    public void rect(Rectangle2D.Double rect) {
        g.fill(rect);
    }

    public void rect(double x, double y, double w, double h) {
        rect(new Rectangle2D.Double(x, y, w, h));
    }

    public void circle(double x, double y, double r) {
        oval(x, y, r, r);
    }

    public void oval(double x, double y, double rx, double ry) {
        g.fillOval((int) (x - rx), (int) (y - ry), (int) (rx * 2), (int) (ry * 2));
    }

    public void line(double x1, double y1, double x2, double y2) {
        pushState();

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double dis = Math.sqrt((x2 - x1) * (x2 - x1) + (y1 - y2) * (y1 - y2));

        translate(x1, y1);
        rotate(angle);
        rect(-lineWidth / 2, -lineWidth / 2, dis, lineWidth);

        popState();
    }

    public void line(Point p1, Point p2) {
        line(p1.x, p1.y, p2.x, p2.y);
    }

    public void line(Vector2 p1, Vector2 p2) {
        line(p1.x, p1.y, p2.x, p2.y);
    }

    public void text(String text, double x, double y) {
        int offset = 0;
        pushState();

        VolatileImage buffer = window.activeRenderingFrame.tempRenderBuffer;
        Graphics2D g = buffer.createGraphics();



        Composite composite = g.getComposite();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g.setComposite(composite);

        int red = 255;
        int green = 0;
        int blue = 0;
        int alpha = 100;

        g.setXORMode(new Color(255 - red, 255 - green, 255 - blue, 255 - alpha));

        for (int i = 0; i < text.length(); i++) {
            offset += font.render(g, text.charAt(i), offset, 0);
        }

        g.dispose();

        this.g.drawImage(
                buffer, (int) x, (int) y, this.window.activeRenderingFrame
        );

        popState();
    }

    // transformations

    public void translate(double x, double y) {
        g.translate(x, y);
    }

    public void translate(Vector2 vec) {
        translate(vec.x, vec.y);
    }

    public void translate(Point point) {
        translate(point.x, point.y);
    }

    public void translate(int x, int y) {
        translate(x, (double) y);
    }

    public void rotate(double angle) {
        g.rotate(angle);
    }

    public void scale(double x, double y) {
        g.scale(x, y);
    }

    public void scale(double s) {
        scale(s, s);
    }

    public void scale(int s) {
        scale((double) s);
    }

    public void scale(Vector2 vec) {
        scale(vec.x, vec.y);
    }

    public void scale(Point point) {
        scale(point.x, point.y);
    }

    public void pushState() {
        State state = new State();
        states.addLast(state);

        state.color = g.getColor();
        state.composite = g.getComposite();
        state.hints = (RenderingHints) g.getRenderingHints().clone();
        state.trans = (AffineTransform) g.getTransform().clone();
        state.lineWidth = lineWidth;
    }

    public void popState() {
        State state = states.removeLast();

        g.setColor(state.color);
        g.setComposite(state.composite);
        g.setRenderingHints(state.hints);
        g.setTransform(state.trans);
        this.lineWidth = state.lineWidth;

    }

}
