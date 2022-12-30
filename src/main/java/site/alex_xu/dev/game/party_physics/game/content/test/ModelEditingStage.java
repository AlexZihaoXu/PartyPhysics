package site.alex_xu.dev.game.party_physics.game.content.test;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ModelEditingStage extends Stage {

    Camera camera = new Camera();

    double selectedX = 0;
    double selectedY = 0;

    ArrayList<Vector2> vertices = new ArrayList<>();

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.setColor(Color.black);
        renderer.clear();

        renderer.pushState();
        camera.applyTransform(renderer);
        renderer.setLineWidth(1 / camera.scale);
        renderer.setColor(100);

        for (int x = -25; x < 25; x++) {
            renderer.line(x, -100, x, 100);
        }
        for (int y = -25; y < 25; y++) {
            renderer.line(-100, y, 100, y);
        }
        renderer.setColor(150, 0, 0);
        renderer.line(0, -100, 0, 100);
        renderer.setColor(0, 0, 150);
        renderer.line(-100, 0, 100, 0);


        renderer.setColor(150, 150, 30);
        for (int i = vertices.size() / 3 * 3; i < vertices.size(); i++) {
            Vector2 v = vertices.get(i);
            renderer.circle(v.x, v.y, 1 / camera.scale * 2);
        }
        renderer.setColor(200);

        for (int i = 0; i + 3 <= vertices.size(); i += 3) {
            Vector2 v1 = vertices.get(i);
            Vector2 v2 = vertices.get(i + 1);
            Vector2 v3 = vertices.get(i + 2);
            renderer.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
        }

        renderer.setLineWidth(1 / camera.scale * 3);


        renderer.setColor(180, 180, 180, 160);
        renderer.setLineWidth(1 / camera.scale * 1.1);
        renderer.line(selectedX, -100, selectedX, 100);
        renderer.line(-100, selectedY, 100, selectedY);
        renderer.setColor(0, 150, 0);
        renderer.circle(selectedX, selectedY, 1 / camera.scale * 1.2);
        renderer.popState();

    }

    @Override
    public void onOffload() {
        super.onOffload();
        System.out.println("Saving ...");

        try {
            FileOutputStream stream = new FileOutputStream("model.mdl");
            DataOutputStream dataOutputStream = new DataOutputStream(stream);

            // Length
            dataOutputStream.writeByte(vertices.size());

            // Vertices
            for (int i = 0; i < vertices.size() / 3 * 3; i++) {
                Vector2 v = vertices.get(i);
                dataOutputStream.writeByte((int) v.x);
                dataOutputStream.writeByte((int) v.y);
            }


            dataOutputStream.close();
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Saved to model.mdl");
    }

    @Override
    public void onMousePressed(double x, double y, int button) {
        super.onMousePressed(x, y, button);
        if (button == 1) {
            Vector2 worldPos = camera.getWorldMousePos();
            worldPos.x = Math.round(worldPos.x);
            worldPos.y = Math.round(worldPos.y);
            vertices.add(worldPos);
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        camera.scale += (30 - camera.scale) * Math.min(1, 3 * getDeltaTime());
        Vector2 worldPos = camera.getWorldMousePos();
        worldPos.x = Math.round(worldPos.x);
        worldPos.y = Math.round(worldPos.y);
        selectedX += (worldPos.x - selectedX) * Math.min(1, 10 * getDeltaTime());
        selectedY += (worldPos.y - selectedY) * Math.min(1, 10 * getDeltaTime());

    }
}
