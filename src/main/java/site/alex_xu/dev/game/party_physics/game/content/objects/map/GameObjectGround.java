package site.alex_xu.dev.game.party_physics.game.content.objects.map;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectGround extends GameObject {

    private double x, y, width, height;
    private Color color = new Color(5, 10, 31);

    @Override
    public Color getHitParticleColor() {
        return color;
    }

    public GameObjectGround(double x, double y, double w, double h) {
        super();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        Rectangle rectangle = new Rectangle(w, h);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0.8);
        addFixture(fixture);

        setMass(MassType.INFINITE);
        translate(x + w / 2, y + h / 2);
    }

    @Override
    public Package createCreationPackage() {
        Package pkg = new Package(PackageTypes.WORLD_SYNC_ADD_OBJECT);
        pkg.setFraction("x", x);
        pkg.setFraction("y", y);
        pkg.setFraction("w", width);
        pkg.setFraction("h", height);
        pkg.setInteger("id", getObjectID());
        return pkg;
    }

    @Override
    public GameObject createFromPackage(Package pkg) {
        int id = pkg.getInteger("id");
        double x = pkg.getFraction("x");
        double y = pkg.getFraction("y");
        double w = pkg.getFraction("w");
        double h = pkg.getFraction("h");

        GameObject.nextObjectID = id;
        return new GameObjectGround(x, y, w, h);
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.translate(getRenderPos());
        renderer.setColor(color);
        renderer.rect(-width / 2, -height / 2, width, height);
        renderer.popState();
    }
}
