package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.listener.ContactListener;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectProjectile;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerBody;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerHead;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerLimb;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;

/**
 * World Collision Handler class
 * handles collisions in physics simulation
 */
class WorldCollisionHandler implements ContactListener<GameObject> {
    GameWorld world;

    double now = 0;

    WorldCollisionHandler(GameWorld world) {
        this.world = world;
    }

    public boolean isPlayerPart(GameObject object) {
        return object instanceof GameObjectPlayerPart;
    }

    @Override
    public void begin(ContactCollisionData collision, Contact contact) {

    }

    @Override
    public void persist(ContactCollisionData collision, Contact oldContact, Contact newContact) {

    }

    @Override
    public void end(ContactCollisionData collision, Contact contact) {
    }

    @Override
    public void destroyed(ContactCollisionData collision, Contact contact) {

    }

    @Override
    public void collision(ContactCollisionData collision) {

    }

    @Override
    public void preSolve(ContactCollisionData collision, Contact contact) {

        // Remove contacted projectiles

        GameObject body1 = (GameObject) collision.getBody1();
        GameObject body2 = (GameObject) collision.getBody2();
        Vector2 point = contact.getPoint();
        if (body1 instanceof GameObjectProjectile) {
            ((GameObjectProjectile) body1).onHit(body2, point);
            if (((GameObjectProjectile) body1).shouldDelete()) {
                world.removeObject(body1);
            }
        } else if (body2 instanceof GameObjectProjectile) {
            ((GameObjectProjectile) body2).onHit(body1, point);
            if (((GameObjectProjectile) body2).shouldDelete()) {
                world.removeObject(body2);
            }
        }
    }

    @Override
    public void postSolve(ContactCollisionData collision, SolvedContact contact) {

        // Help players to grab items

        GameObject body1 = (GameObject) collision.getBody1();
        GameObject body2 = (GameObject) collision.getBody2();
        Vector2 point = contact.getPoint();
        if (isPlayerPart(body1) && !isPlayerPart(body2)) {
            ((GameObjectPlayerPart) body1).getPlayer().setTouchGround(now, body1, body2, point);
            ((GameObjectPlayerPart) body1).getPlayer().tryGrabItem(body2, body1);
        } else if (isPlayerPart(body2) && !isPlayerPart(body1)) {
            ((GameObjectPlayerPart) body2).getPlayer().setTouchGround(now, body2, body1, point);
            ((GameObjectPlayerPart) body2).getPlayer().tryGrabItem(body1, body2);
        }

    }
}
