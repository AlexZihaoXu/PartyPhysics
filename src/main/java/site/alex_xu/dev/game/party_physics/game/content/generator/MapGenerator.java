package site.alex_xu.dev.game.party_physics.game.content.generator;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectWoodPlank;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync.ServerSideWorldSyncer;

import java.util.ArrayList;

public class MapGenerator {

    ServerSideWorldSyncer syncer;

    public MapGenerator(ServerSideWorldSyncer syncer) {
        this.syncer = syncer;
    }

    public void resetWorld() {
        ArrayList<GameObject> removed = new ArrayList<>();
        for (GameObject object : syncer.getWorld().getObjects()) {
            if (!(object instanceof GameObjectPlayerPart)) {
                removed.add(object);
            }
        }
        for (GameObject gameObject : removed) {
            syncer.syncRemoveObject(gameObject);
        }
    }

    public void movePlayers(double centerX, double centerY, double dispersionX) {
        this.spawnLocation.set(centerX, centerY);
        this.dispersionX = dispersionX;
        for (Player player : syncer.getWorld().getPlayers()) {
            player.moveTo(centerX + (Math.random() - 0.5) * 2 * dispersionX, centerY);
        }
    }

    Vector2 spawnLocation = new Vector2();
    double dispersionX = 0;

    public void addPlayer(Player player) {

    }

    public void regenerate() {
        resetWorld();

//        int id = (int) (Math.random() * 3);

        int id = 0;

        syncer.syncAddGround(-100, 2, 200, 1);

        if (id == 0) {


            syncer.syncAddGround(-10, 0.2, 4, 0.5);
            syncer.syncAddBox(-8.5, -1);

            syncer.syncAddGround(6, 0.2, 4, 0.5);
            syncer.syncAddBox(7.5, -1);

            syncer.syncAddObject(new GameObjectWoodPlank(
                    -12, -2, 24, 0.5
            ));

            movePlayers(0, -15, 5);

            return;
        }

        if (id == 1) {

        }

        if (id == 2) {

        }

    }

}
