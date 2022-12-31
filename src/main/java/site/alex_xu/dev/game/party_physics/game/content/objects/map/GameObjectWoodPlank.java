package site.alex_xu.dev.game.party_physics.game.content.objects.map;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectWoodPlank extends GameObject {

    private final Color color = new Color(197, 161, 65);
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public GameObjectWoodPlank(double x, double y, double w, double h) {
        super();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        Rectangle rectangle = new Rectangle(w, h);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0.4);
        addFixture(fixture);

        setMass(new Mass(new Vector2(0, 0), 0.5, 0.5));
        translate(x + w / 2, y + h / 2);
    }

    @Override
    public Package createCreationPackage() {
        Package pkg = super.createCreationPackage();
        pkg.setFraction("w", width);
        pkg.setFraction("h", height);
        return pkg;
    }

    @Override
    public GameObject createFromPackage(Package pkg) {
        int id = pkg.getInteger("id");
        double posX = pkg.getFraction("pos.x");
        double posY = pkg.getFraction("pos.y");
        double posA = pkg.getFraction("pos.a");
        double velX = pkg.getFraction("vel.x");
        double velY = pkg.getFraction("vel.y");
        double velA = pkg.getFraction("vel.a");
        double w = pkg.getFraction("w");
        double h = pkg.getFraction("h");

        GameObject.objectIDCounter = id;
        GameObjectWoodPlank plank = new GameObjectWoodPlank(1000, 1000, w, h);
        plank.getTransform().setTranslation(posX, posY);
        plank.getTransform().setRotation(posA);
        plank.setLinearVelocity(velX, velY);
        plank.setAngularVelocity(velA);
        return plank;
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        renderer.setColor(color);
        renderer.rect(-width / 2, -height / 2, width, height);
        renderer.setColor(color.darker());
        renderer.rect(-width / 2 + 0.1, -height / 2 + 0.1, width - 0.2, height - 0.2);
        renderer.popState();
    }
}
