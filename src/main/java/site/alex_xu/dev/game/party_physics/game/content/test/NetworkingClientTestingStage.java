package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.ClientManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.*;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.io.IOException;

public class NetworkingClientTestingStage extends Stage {

    GameWorld world = new GameWorld();
    Camera camera = new Camera();
    ClientManager clientManager = new ClientManager();

    @Override
    public void onLoad() {
        super.onLoad();
        world.init();
        clientManager.start();
        clientManager.send(new Package(PackageTypes.HANDSHAKE));
    }

    @Override
    public void onTick() {
        super.onTick();
        while (true) {
            Package pkg = clientManager.pull();
            if (pkg == null) {
                break;
            } else {
                if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_OBJECT_CREATE) {
                    world.addObject(GameObjectManager.getInstance().createFromPackage(pkg));
                } else if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_OBJECT_TRANSFORM) {
                    world.syncObject(pkg);
                }
            }
        }

        world.onTick();

        clientManager.flush();
    }

    @Override
    public void onOffload() {
        super.onOffload();
        clientManager.close();
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
        PartyPhysicsWindow.getInstance().changeStage(new NetworkingClientTestingStage());
        PartyPhysicsWindow.getInstance().start();
    }
}
