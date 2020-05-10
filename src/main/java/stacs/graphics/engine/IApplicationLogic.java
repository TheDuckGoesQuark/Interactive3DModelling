package stacs.graphics.engine;

import stacs.graphics.render.Window;

public interface IApplicationLogic {

    void init() throws Exception;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput);

    void render(Window window);

    void cleanup();
}
