package site.alex_xu.dev.game.party_physics.game.content.test;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.particles.PlayerHitParticle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Particle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Networking;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ParticleTestStage extends Stage {

    GameWorld world = new GameWorld();
    Camera camera = new Camera();

    final ArrayList<Particle> newParticles = new ArrayList<>();

    public static void main(String[] args) {
        Networking.getInstance().init();
        PartyPhysicsWindow.getInstance().changeStage(new ParticleTestStage());
        PartyPhysicsWindow.getInstance().getJFrame().setAlwaysOnTop(true);
        PartyPhysicsWindow.getInstance().getJFrame().setTitle("Particle Test");
        PartyPhysicsWindow.getInstance().start();


    }

    @Override
    public void onLoad() {
        super.onLoad();
        world.init();
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.setColor(Color.gray);
        renderer.clear();

        synchronized (newParticles) {
            for (Particle newParticle : newParticles) {
                world.addParticle(newParticle);
            }
            newParticles.clear();
        }

        camera.scale = 30;

        world.onTick();
        camera.render(world, renderer);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (keyCode == KeyEvent.VK_SPACE) {
            synchronized (newParticles) {
                for (int i = 0; i < 10; i++) {
                    newParticles.add(new PlayerHitParticle(
                            Color.GREEN.brighter(), 0, 0, Math.random(), Math.random() * 2 + 1,10 + Math.random() * 10, Math.random() * 0.2 + 0.1
                    ));
                }
            }
        }
    }
}
