package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.Client;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.ServerManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.GameObjectManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Networking;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;

public class NetworkingServerTestingStage extends Stage {

    GameWorld world = new GameWorld();
    Camera camera = new Camera();

    ServerManager serverManager = new ServerManager();

    Clock addClock = new Clock();
    Clock syncClock = new Clock();

    @Override
    public void onLoad() {
        super.onLoad();
        world.init();
        world.addObject(new GameObjectGround(-10, 2, 20, 1));
        world.addObject(new GameObjectBox(0, -10));
        serverManager.start();

    }

    @Override
    public void onOffload() {
        super.onOffload();
        serverManager.close();
    }

    @Override
    public void onTick() {
        super.onTick();
        Package pkg = serverManager.pull();
        if (pkg != null) {
            System.out.println(pkg);
            if (pkg.getType() == PackageTypes.HANDSHAKE) {
                Client client = serverManager.getClient(pkg);
                for (GameObject object : world.getObjects()) {
                    Package creationPackage = GameObjectManager.getInstance().createCreationPackage(object);
                    client.send(creationPackage);
                }
            }
        }
        world.onTick();
        if (addClock.elapsedTime() > 1) {
            GameObject obj = new GameObjectBox(Math.random() - 0.5, -20);
            serverManager.broadCast(GameObjectManager.getInstance().createCreationPackage(obj));
            world.addObject(obj);
            addClock.reset();
        }

        if (syncClock.elapsedTime() > 0.25) {
            syncClock.reset();
            for (GameObject object : world.getObjects()) {
                Package syncPackage = object.createSyncPackage();
                serverManager.broadCast(syncPackage);
            }
        }
    }

    @Override
    public void onRender(Renderer renderer) {

        camera.scale += (50 - camera.scale) * Math.min(1, getDeltaTime() * 5);
        camera.pos.y += (-2 - camera.pos.y) * Math.min(1, getDeltaTime() * 5);

        super.onRender(renderer);
        renderer.setColor(new Color(11, 43, 93));
        renderer.clear();
        camera.render(world, renderer);
    }

    public static void main(String[] args) {
        Networking.getInstance().init();
        PartyPhysicsWindow.getInstance().changeStage(new NetworkingServerTestingStage());
        PartyPhysicsWindow.getInstance().start();
    }
}
