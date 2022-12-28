package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.SolvedContact;
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
        if (isPlayerPart((GameObject) collision.getBody1()) && !isPlayerPart((GameObject) collision.getBody2())) {
            ((GameObjectPlayerPart) collision.getBody1()).getPlayer().setTouchGround(now);
        }
    }
}
