package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.listener.ContactListener;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerBody;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerHead;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerLimb;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;

class PlayerCollisionHandler implements ContactListener<GameObject> {
    GameWorld world;

    double now = 0;
    PlayerCollisionHandler(GameWorld world) {
        this.world = world;
    }

    public boolean isPlayerPart(GameObject object) {
        return object instanceof GameObjectPlayerHead || object instanceof GameObjectPlayerBody || object instanceof GameObjectPlayerLimb;
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

    }

    @Override
    public void postSolve(ContactCollisionData collision, SolvedContact contact) {
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
