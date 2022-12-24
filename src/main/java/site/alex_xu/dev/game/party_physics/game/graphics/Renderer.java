package site.alex_xu.dev.game.party_physics.game.graphics;

import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
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

    private Font font = Font.DEFAULT;

    private int textSize = 16;

    public Renderer(Graphics g, int width, int height) {
        this.g = (Graphics2D) g;
        this.width = width;
        this.height = height;
        this.g.setFont(font.getAwt(textSize));

        setRenderHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
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

    public void setRenderHint(RenderingHints.Key hint, Object value) {
        g.setRenderingHint(hint, value);
    }

    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setFont(Font font) {
        this.font = font;
        g.setFont(font.getAwt(textSize));
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        g.setFont(font.getAwt(textSize));
    }

    // Others

    public void enableTextAA() {
        setRenderHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    }

    public void disableTextAA() {
        setRenderHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    // Getters

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
        pushState();
        g.fillRect(0, 0, width, height);
        popState();
    }

    public void rect(Rectangle2D.Double rect) {
        g.fill(rect);
    }

    public void rect(double x, double y, double w, double h) {
        rect(new Rectangle2D.Double(x, y, w, h));
    }

    public void circle(double x, double y, double r) {
        g.fillOval((int) (x - r), (int) (y - r), (int) (r * 2), (int) (r * 2));
    }

    public void line(double x1, double y1, double x2, double y2) {
        pushState();

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double dis = Math.sqrt((x2 - x1) * (x2 - x1) + (y1 - y2) * (y1 - y2));

        translate(x1, y1);
        rotate(angle);
        rect(-lineWidth, -lineWidth, dis, lineWidth * 2);

        popState();
    }

    public void line(Point p1, Point p2) {
        line(p1.x, p1.y, p2.x, p2.y);
    }

    public void line(Vector2 p1, Vector2 p2) {
        line(p1.x, p1.y, p2.x, p2.y);
    }

    public void text(String text, double x, double y) {
        g.drawString(text, (float) x, (float) y + textSize);
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
        state.font = font;
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
        this.font = state.font;
        this.lineWidth = state.lineWidth;

    }

}
