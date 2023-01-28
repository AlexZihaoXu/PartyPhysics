package site.alex_xu.dev.game.party_physics.game.graphics;

import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

/**
 * A wrap up class of Java's Graphics
 */
public class Renderer {

    /**
     * A class that saves the current state of the renderer
     */
    private static final class State {

        Color color;
        AffineTransform trans;
        RenderingHints hints;
        Composite composite;
        Font font;

        double lineWidth = 1;

    }

    private final Graphics2D g;

    /**
     * Used to save and restore the states when pushState or popState is called
     */
    private final LinkedList<State> states = new LinkedList<>();
    private final int width, height;

    private final AffineTransform originalTransform;

    private double lineWidth = 1;

    private Font font = Font.DEFAULT;

    private int textSize = 16;

    public Renderer(Graphics g, int width, int height) {
        this.g = (Graphics2D) g;
        this.width = width;
        this.height = height;
        this.g.setFont(font.getAwt(textSize));
        originalTransform = (AffineTransform) ((Graphics2D) g).getTransform().clone();

        setRenderHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    // Setters
    /**
     * @param path sets the font from the given path
     */
    public void setFont(String path) {
        setFont(Font.get(path));
    }

    /**
     * @param color sets the color of the renderer
     */
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


    /**
     * @param composite sets the composite for the wrapped graphics
     */
    public void setComposite(Composite composite) {
        g.setComposite(composite);
    }

    /**
     * @param hint the hint to set
     * @param value the value to set
     */
    public void setRenderHint(RenderingHints.Key hint, Object value) {
        g.setRenderingHint(hint, value);
    }

    /**
     * @param lineWidth set the lineWidth
     */
    public void setLineWidth(double lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * @param font set font from the given font object
     */
    public void setFont(Font font) {
        this.font = font;
        g.setFont(font.getAwt(textSize));
    }

    /**
     * @param textSize update text size
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
        g.setFont(font.getAwt(textSize));
    }

    // Others

    /**
     * Enable Anti-aliasing Feature
     */
    public void enableTextAA() {
        setRenderHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    }


    /**
     * Disable Anti-aliasing Feature
     */
    public void disableTextAA() {
        setRenderHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    // Getters

    /**
     * @return the width of line
     */
    public double getLineWidth() {
        return lineWidth;
    }

    /**
     * @return the text size
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * @return the text height based on the current font and text size
     */
    public double getTextHeight() {
        return g.getFontMetrics().getHeight();
    }

    /**
     * @param text the string to calculate width
     * @return the width in pixels of the given string
     */
    public double getTextWidth(String text) {
        return g.getFontMetrics().stringWidth(text);
    }

    // Render

    /**
     * Clear the background
     */
    public void clear() {
        AffineTransform trans = g.getTransform();
        g.setTransform(originalTransform);
        g.fillRect(0, 0, width, height);
        g.setTransform(trans);
    }

    /**
     * @param rect the rectangle location
     */
    public void rect(Rectangle2D.Double rect) {
        g.fill(rect);
    }

    /**
     * @param x top left x-position of the rectangle
     * @param y top left y-position of the rectangle
     * @param w width of the rectangle
     * @param h height of the rectangle
     */
    public void rect(double x, double y, double w, double h) {
        AffineTransform trans = g.getTransform();

        g.translate(x, y);
        g.scale(w, h);
        g.fillRect(0, 0, 1, 1);

        g.setTransform(trans);
    }

    /**
     * @param x center x
     * @param y center y
     * @param r radius
     */
    public void circle(double x, double y, double r) {
        AffineTransform trans = g.getTransform();

        g.translate(x, y);
        g.scale(r, r);
        g.fillOval(-1, -1, 2, 2);

        g.setTransform(trans);
    }

    /**
     * @param center center
     * @param r radius
     */
    public void circle(Vector2 center, double r) {
        circle(center.x, center.y, r);
    }

    /**
     * @param x top left x-coordinate
     * @param y top left y-coordinate
     * @param w width
     * @param h height
     * @param r border radius
     */
    public void rect(double x, double y, double w, double h, double r) {
        g.fill(new RoundRectangle2D.Double(x, y, w, h, r, r));
    }

    /**
     * @param x1 point 1 x-coordinate
     * @param y1 point 1 y-coordinate
     * @param x2 point 2 x-coordinate
     * @param y2 point 2 y-coordinate
     */
    public void line(double x1, double y1, double x2, double y2) {
        pushState();

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double dis = Math.sqrt((x2 - x1) * (x2 - x1) + (y1 - y2) * (y1 - y2));

        translate(x1, y1);
        rotate(angle);
        rect(-lineWidth, -lineWidth, dis + lineWidth * 2, lineWidth * 2);

        popState();
    }

    /**
     * @param x1 point 1 x-coord
     * @param y1 point 1 y-coord
     * @param x2 point 2 x-coord
     * @param y2 point 2 y-coord
     * @param x3 point 3 x-coord
     * @param y3 point 3 y-coord
     */
    public void triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        Path2D path = new Path2D.Double();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.closePath();
        g.fill(path);
    }

    /**
     * @param triangle triangle object to draw
     */
    public void triangle(Triangle triangle) {
        Vector2[] vertices = triangle.getVertices();
        this.triangle(vertices[0].x, vertices[0].y, vertices[1].x, vertices[1].y, vertices[2].x, vertices[2].y);
    }

    /**
     * @param p1 point 1
     * @param p2 point 2
     */
    public void line(Point p1, Point p2) {
        line(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * @param p1 point 1
     * @param p2 point 2
     */
    public void line(Vector2 p1, Vector2 p2) {
        line(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * @param text the string to render
     * @param x the top left x-coordinate
     * @param y the top left y-coordinate
     */
    public void text(String text, double x, double y) {
        g.drawString(text, (float) x, (float) y + g.getFontMetrics().getAscent());
    }

    // transformations

    /**
     * Translates the origin by the given offset
     * @param x x-offset
     * @param y y-offset
     */
    public void translate(double x, double y) {
        g.translate(x, y);
    }

    /**
     * Translates the origin by the given offset
     * @param vec offsets
     */
    public void translate(Vector2 vec) {
        translate(vec.x, vec.y);
    }

    /**
     * Translates the origin by the given offset
     * @param point offsets
     */
    public void translate(Point point) {
        translate(point.x, point.y);
    }

    /**
     * Translates the origin by the given offset
     * @param x x-offset
     * @param y y-offset
     */
    public void translate(int x, int y) {
        translate(x, (double) y);
    }

    /**
     * Rotates the origin clock-wise for the given angle
     * @param angle angle in radians
     */
    public void rotate(double angle) {
        g.rotate(angle);
    }

    /**
     * Scale the coordinates system on the x/y-axis
     * @param x x-stretch/compress
     * @param y y-stretch/compress
     */
    public void scale(double x, double y) {
        g.scale(x, y);
    }

    /**
     * Scale coordinate system on both x&y-axis
     * @param s scale
     */
    public void scale(double s) {
        scale(s, s);
    }

    /**
     * Scale the coordinate system on both x&y-axis
     * @param s scale
     */
    public void scale(int s) {
        scale((double) s);
    }

    /**
     * Stretch/compress the coordinate system by the given vector
     * @param vec scale
     */
    public void scale(Vector2 vec) {
        scale(vec.x, vec.y);
    }

    /**
     * Stretch/compress the coordinate system by the given vector
     * @param point scale
     */
    public void scale(Point point) {
        scale(point.x, point.y);
    }

    /**
     * Save the state of the renderer (includes all translations, colors, lineWidth, sizes, etc..)
     */
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

    /**
     * Restores last pushed state
     */
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
