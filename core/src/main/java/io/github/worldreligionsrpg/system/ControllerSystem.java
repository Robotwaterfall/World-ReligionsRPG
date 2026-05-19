package io.github.worldreligionsrpg.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.worldreligionsrpg.component.Move;
import io.github.worldreligionsrpg.input.Command;
import io.github.worldreligionsrpg.input.Controller;

public class ControllerSystem extends IteratingSystem {

    public ControllerSystem() {
        super(Family.all(Controller.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Controller controller = Controller.MAPPER.get(entity);
        if (controller.getPressedCommands().isEmpty()
                && controller.getReleasedCommands().isEmpty()) {
            return;
        }

        for (Command pressedCommand : controller.getPressedCommands()) {
            switch (pressedCommand) {
                case UP -> moveEntity(entity, 0f, 1f);
                case DOWN -> moveEntity(entity, 0f, -1f);
                case LEFT -> moveEntity(entity, -1f, 0f);
                case RIGHT -> moveEntity(entity, 1f, 0f);
            }
        }

        controller.getPressedCommands().clear();

        for (Command releaseCommand : controller.getReleasedCommands()) {
            switch (releaseCommand) {
                case UP -> moveEntity(entity, 0f, -1f);
                case DOWN -> moveEntity(entity, 0f, 1f);
                case LEFT -> moveEntity(entity, 1f, 0f);
                case RIGHT -> moveEntity(entity, -1f, 0f);
            }
        }

        controller.getReleasedCommands().clear();
    }

    private void moveEntity(Entity entity, float dx, float dy) {
        Move move = Move.MAPPER.get(entity);
        if (move == null) {
            return;
        }

        move.getDirection().x += dx;
        move.getDirection().y += dy;
    }
}
