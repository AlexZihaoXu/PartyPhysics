package site.alex_xu.dev.game.party_physics.game.content.stages;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Font;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

class Button {
    private final String title;
    private Vector2 pos = new Vector2();
    private final double width = 220;
    private final double height = 50;

    private double animationProgress = 0;
    private double animationRate = 0;

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(getX(), getY(), width, height);
    }

    public Button(String title) {
        this.title = title;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public void setPos(double x, double y) {
        pos.set(x, y);
    }

    public double getX() {
        return pos.x;
    }

    public double getY() {
        return pos.y;
    }

    public void onTick(double dt, Stage stage) {
        Vector2 pos = stage.getMousePos();
        if (getBounds().contains(pos.x, pos.y)) {
            animationProgress += dt * 5;
        } else {
            animationProgress -= dt * 5;
        }
        animationProgress = Math.max(0, Math.min(1, animationProgress));
        double x = 1 - animationProgress;
        animationRate = Math.max(0, Math.min(1, 1 + (x * x * x * (x - 2))));
    }

    public void onRender(Renderer renderer) {
        renderer.pushState();

        renderer.setTextSize(28);

        double posOffset = animationRate * 1.2;
        int colorOffset = (int) (animationRate * 30);

        renderer.setColor(new Color(138, 132, 127));
        renderer.text(title, getX() + 16 + posOffset, getY() + 12 + posOffset);
        renderer.setColor(98 - colorOffset, 92 - colorOffset, 85 - colorOffset);
        renderer.text(title, getX() + 16 - posOffset, getY() + 12 - posOffset);

        if (animationRate > 0.01) {
            double length = animationRate * (width - 30);
            renderer.setColor(new Color(138, 132, 127));
            renderer.line(getX() + 15, getY() + height - 6, getX() + length, getY() + height - 6);
        }

        renderer.popState();
    }
}

public class MenuStage extends Stage {

    Button btnPlay = new Button("PLAY");
    Button btnOptions = new Button("OPTIONS");
    Button btnTutorials = new Button("TUTORIAL");
    Button btnExit = new Button("EXIT");

    GameWorld world = new GameWorld();
    Camera camera = new Camera();

    Player player;

    @Override
    public void onLoad() {
        super.onLoad();
        world.init();
        world.addObject(new GameObjectGround(-60, 4, 120, 1));
        player = new Player(Color.WHITE, 0, -20, 0);
        world.addPlayer(player);
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);

        renderer.pushState();

        renderer.setFont(Font.get("fonts/bulkypix.ttf"));

        renderer.setColor(new Color(211, 196, 172));
        renderer.clear();
        renderBackground(renderer);
        renderer.setColor(new Color(211, 196, 172, 120));
        renderer.clear();
        renderButtons(renderer);
        renderForeGround(renderer);

        getWindow().setAutoSwitchAALevelEnabled(false);
        getWindow().setAALevel(2);

        renderer.popState();

    }

    public void renderBackground(Renderer renderer) {
        camera.render(world, renderer);
    }

    public void renderForeGround(Renderer renderer) {
        renderer.setColor(0);
        renderer.setTextSize(65);

        String title = "Party Physics!";
        renderer.pushState();
        renderer.translate(getWidth() * 0.03 + renderer.getTextWidth(title) / 2, getHeight() * 0.1 + renderer.getTextHeight() / 2);
        renderer.rotate(-0.05);

        renderer.setColor(new Color(98, 92, 85));
        renderer.text(title, -renderer.getTextWidth(title) / 2 + 3, -renderer.getTextHeight() / 2 + 3);
        renderer.setColor(new Color(38, 34, 25));
        renderer.text(title, -renderer.getTextWidth(title) / 2 + 0, -renderer.getTextHeight() / 2 + 0);

        renderer.setColor(new Color(94, 82, 57));
        renderer.translate(renderer.getTextWidth(title) - 450, 70);
        renderer.setTextSize(20);
        renderer.scale(1 + Math.sin(getCurrentTime() * 4) * 0.01);
        String subTitle = "Made by Alex";
        renderer.text(subTitle, -renderer.getTextWidth(subTitle) / 2, -renderer.getTextHeight() / 2);
        renderer.popState();


        renderer.pushState();
        renderer.setColor(new Color(98, 92, 85));
        String version = PartyPhysicsGame.VERSION_STRING;
        renderer.setTextSize(15);
        renderer.text("Version: " + version, 10, getHeight() - renderer.getTextHeight() - 10);

        String copyRight = "(C) 2023 alex-xu.site - All Rights Reserved.";
        renderer.text(copyRight, getWidth() - renderer.getTextWidth(copyRight) - 10, getHeight() - renderer.getTextHeight() - 10);

        renderer.popState();
    }

    public void renderButtons(Renderer renderer) {
        btnPlay.onRender(renderer);
        btnTutorials.onRender(renderer);
        btnOptions.onRender(renderer);
        btnExit.onRender(renderer);
    }

    @Override
    public void onTick() {
        super.onTick();
        btnPlay.setPos(getWidth() * 0.01 + 40, getHeight() / 2d - 60);
        btnTutorials.setPos(getWidth() * 0.01 + 40, getHeight() / 2d);
        btnOptions.setPos(getWidth() * 0.01 + 40, getHeight() / 2d + 60);
        btnExit.setPos(getWidth() * 0.01 + 40, getHeight() / 2d + 120);

        btnPlay.onTick(getDeltaTime(), this);
        btnTutorials.onTick(getDeltaTime(), this);
        btnOptions.onTick(getDeltaTime(), this);
        btnExit.onTick(getDeltaTime(), this);

        world.onTick();
        camera.scale += (Math.min(getWidth(), getHeight()) / 13d - camera.scale) * Math.min(1, getDeltaTime() * 3);

        if (Math.abs(player.getPos().x - camera.getWorldMousePos().x) > 2) {
            if (player.getPos().x > camera.getWorldMousePos().x) {
                player.setMovementX(-1);
            } else {
                player.setMovementX(1);
            }
            Vector2 direction = player.getPos().subtract(camera.getWorldMousePos());
            player.setReachDirection(Vector2.create(-1, direction.getDirection()));
        } else {
            player.setMovementX(0);
            player.setReachDirection(new Vector2());
        }
    }
}
