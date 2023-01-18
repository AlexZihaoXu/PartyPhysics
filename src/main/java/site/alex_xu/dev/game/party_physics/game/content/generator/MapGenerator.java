package site.alex_xu.dev.game.party_physics.game.content.generator;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.items.GameObjectItemSMG;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectTNT;
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

    public void repopulatePlayers() {
        for (Player player : syncer.getWorld().getPlayers()) {
            respawnPlayer(player);
        }
    }

    public void setSpawnRule(double centerX, double centerY, double dispersionX) {
        this.spawnLocation.set(centerX, centerY);
        this.dispersionX = dispersionX;
    }

    Vector2 spawnLocation = new Vector2(0, -5);
    double dispersionX = 0;

    public void respawnPlayer(Player player) {
        player.setHealth(1);
        player.cancelGrabbing();
        player.setReachDirection(new Vector2());
        player.moveTo(spawnLocation.x + (Math.random() - 0.5) * 2 * dispersionX, spawnLocation.y);
        syncer.syncPlayerUpdateHP(player);
    }

    public void regenerate() {
        resetWorld();

        int id = (int) (Math.random() * 2);


        if (id == 0) {
//            syncer.syncAddGround(-100, 2, 200, 1);


            syncer.syncAddGround(-10, 0.2, 4, 0.5);
            syncer.syncAddBox(-8.5, -1);

            syncer.syncAddGround(6, 0.2, 4, 0.5);
            syncer.syncAddBox(7.5, -1);

            syncer.syncAddObject(new GameObjectWoodPlank(-12, -3, 24, 0.75));

            setSpawnRule(0, -5, 2);
            repopulatePlayers();

            return;
        }

        if (id == 1) {
            double width = 12;
            double thickness = 1;
            double height = 4;

            syncer.syncAddGround(-width - thickness, -height - thickness, width * 2 + thickness * 2, thickness);
            syncer.syncAddGround(-width - thickness, height, width * 2 + thickness * 2, thickness);
            syncer.syncAddGround(-width - thickness, -height, thickness, height * 2);
            syncer.syncAddGround(width, -height, thickness, height * 2);

            int size = 3;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < size - i; j++) {
                    syncer.syncAddBox((i - size) * 0.5 + 0.5 + j, -i);
                }
            }

            for (int i = 0; i < 3; i++) {
                syncer.syncAddObject(new GameObjectTNT(-10, 2 - i));
                syncer.syncAddObject(new GameObjectTNT(10, 2 - i));
            }

            for (int i = -2; i <= 2; i++) {
                syncer.syncAddObject(new GameObjectItemSMG(i * 3, -3.5));
            }

            setSpawnRule(0, -4, 10);
            repopulatePlayers();

            return;
        }

        if (id == 2) { // TODO: add the 3rd map

        }

    }

}
