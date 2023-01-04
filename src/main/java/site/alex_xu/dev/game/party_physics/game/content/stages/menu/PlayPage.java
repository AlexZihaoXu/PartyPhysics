package site.alex_xu.dev.game.party_physics.game.content.stages.menu;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

class PlayPage {
    MenuStage stage;
    private Vector2 pos = new Vector2();
    private double hostCardOverProgress = 1;
    private double joinCardOverProgress = 1;

    private boolean mouseOverHost = false;
    private boolean mouseOverJoin = false;

    Rectangle2D hostBound = new Rectangle2D.Double();
    Rectangle2D joinBound = new Rectangle2D.Double();

    PlayPage(MenuStage stage) {
        this.stage = stage;
    }

    public void onRender(Renderer renderer) {
        Vector2 pos = getPos().copy();

        renderer.pushState();

        renderCard(renderer, true, pos.x, pos.y);
        renderCard(renderer, false, pos.x + 250, pos.y);


        renderer.popState();
    }

    void renderCard(Renderer renderer, boolean host, double x, double y) {
        int cardWidth = 200;
        int cardHeight = 240;

        Rectangle2D bounds = new Rectangle2D.Double(x, y, cardWidth + 3, cardHeight + 3);

        if (host) {
            hostBound.setRect(bounds);
        } else {
            joinBound.setRect(bounds);
        }

        Vector2 mouse = getStage().getMousePos();

        renderer.pushState();

        double progress = host ? hostCardOverProgress : joinCardOverProgress;
        if (bounds.contains(mouse.x, mouse.y)) {
            progress += getDeltaTime() * 5;
        } else {
            progress -= getDeltaTime() * 5;
        }
        progress = Math.max(0, Math.min(1, progress));


        boolean mouseOver;
        if (host) {
            hostCardOverProgress = progress;
            mouseOver = mouseOverHost;
        } else {
            joinCardOverProgress = progress;
            mouseOver = mouseOverJoin;
        }

        double size;
        {
            double n = 1 - progress;
            size = n * n * n * 0.05;
        }

        renderer.translate(cardWidth / 2, cardHeight / 2);
        renderer.translate(x, y);
        renderer.scale(1 - size);
        renderer.translate(-cardWidth / 2, -cardHeight / 2);

        renderer.setTextSize(24);

        renderer.setColor(98, 92, 85);
        renderer.rect(3, 3, cardWidth, cardHeight);
        if (mouseOver) {
            renderer.setColor(new Color(162, 148, 128));
        } else {
            renderer.setColor(156, 145, 130);
        }
        renderer.rect(0, 0, cardWidth, cardHeight);

        String text = host ? "HOST" : "JOIN";
        int width = (int) renderer.getTextWidth(text);
        renderer.setColor(98, 92, 85);
        renderer.text(text, (cardWidth - width) / 2d + 2, cardHeight - renderer.getTextHeight() - 23);
        if (mouseOver) {
            renderer.setColor(58, 52, 45);
        } else {
            renderer.setColor(75, 69, 62);
        }
        renderer.text(text, (cardWidth - width) / 2d, cardHeight - renderer.getTextHeight() - 25);

        if (host) {
            renderer.setColor(98, 92, 85);

            for (int i = 0; i < 2; i++) {
                double tx = cardWidth / 2d - i * 3 + 1;
                double ty = cardHeight * 0.3 - i * 3 + 1;
                renderer.triangle(tx, ty, tx - 40, ty + 40, tx + 40, ty + 40);
                ty += 40;
                renderer.rect(tx - 40, ty, 80, 15);
                renderer.rect(tx - 40, ty + 15, 25, 35);
                renderer.rect(tx + 15, ty + 15, 25, 35);

                renderer.pushState();
                renderer.translate(tx, ty - 52);
                renderer.rotate(Math.PI / 4);
                renderer.rect(-6, -6, 80, 12);
                renderer.rect(-6, -6, 12, 80);
                renderer.popState();
                if (mouseOver) {
                    renderer.setColor(58, 52, 45);
                } else {
                    renderer.setColor(75, 69, 62);
                }
            }



        }
        else {
            renderer.setColor(98, 92, 85);

            for (int i = 0; i < 2; i++) {
                double rx = cardWidth / 2d - i * 3 + 1;
                double ry = cardHeight * 0.3 - i * 3 + 1 - 10;

                renderer.rect(rx - 30, ry, 60, 12);
                renderer.rect(rx - 42, ry, 12, 100);
                renderer.rect(rx + 30, ry, 12, 100);

                Vector2 topRight = new Vector2(rx + 23, ry + 17);
                Vector2 botRight = new Vector2(rx + 23, ry + 95);
                renderer.triangle(topRight.x, topRight.y, botRight.x, botRight.y, topRight.x - 45, topRight.y + 8);
                renderer.triangle(topRight.x - 45, topRight.y + 8, botRight.x, botRight.y, topRight.x - 45, botRight.y - 8);

                if (mouseOver) {
                    renderer.setColor(58, 52, 45);
                } else {
                    renderer.setColor(75, 69, 62);
                }
            }
        }

        renderer.popState();
    }

    public void onTick() {
        Vector2 mouse = getStage().getMousePos();

        if (hostBound.contains(mouse.x, mouse.y)) {
            if (!mouseOverHost) {
                mouseOverHost = true;
                onMouseOver(true);
            }
        } else {
            mouseOverHost = false;
        }

        if (joinBound.contains(mouse.x, mouse.y)) {
            if (!mouseOverJoin) {
                mouseOverJoin = true;
                onMouseOver(false);
            }
        } else {
            mouseOverJoin = false;
        }
    }

    public void onMouseOver(boolean host) {
        SoundSystem.getInstance().getUISourceGroup().play("sounds/ui/mouse-over-0.wav");
    }

    public MenuStage getStage() {
        return stage;
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public void setPos(double x, double y) {
        this.pos.set(x, y);
    }

    public double getDeltaTime() {
        return getStage().getDeltaTime();
    }
}
