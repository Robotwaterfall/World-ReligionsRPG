package io.github.worldreligionsrpg.input;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Controller implements Component {
    public static final ComponentMapper<Controller> MAPPER =
            ComponentMapper.getFor(Controller.class);

    private final List<Command> pressedCommands;
    private final List<Command> releasedCommands;

    public Controller() {
        this.pressedCommands = new ArrayList<>();
        this.releasedCommands = new ArrayList<>();
    }

    public List<Command> getPressedCommands() {
        return pressedCommands;
    }

    public List<Command> getReleasedCommands() {
        return releasedCommands;
    }
}
