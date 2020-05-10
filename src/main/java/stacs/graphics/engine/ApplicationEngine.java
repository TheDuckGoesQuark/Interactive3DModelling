package stacs.graphics.engine;

import stacs.graphics.render.Window;

public class ApplicationEngine implements Runnable {

    private static final float TARGET_FPS = 30;
    private static final float TARGET_UPS = 30;
    private final Window window;
    private final Timer timer;
    private final IApplicationLogic applicationLogic;
    private final MouseInput mouseInput;

    public ApplicationEngine(String windowTitle, int width, int height, boolean vsync, IApplicationLogic applicationLogic) {
        this.window = new Window(windowTitle, width, height, vsync);
        this.mouseInput = new MouseInput();
        this.applicationLogic = applicationLogic;
        this.timer = new Timer();
    }

    public void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        applicationLogic.init();
    }

    @Override
    public void run() {
        try {
            init();
            loop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void loop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.shouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isVsync()) {
                sync();
            }
        }
    }

    protected void cleanup() {
        applicationLogic.cleanup();
        window.terminate();
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    protected void input() {
        mouseInput.input(window);
        applicationLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        applicationLogic.update(interval, mouseInput);
    }

    protected void render() {
        applicationLogic.render(window);
        window.update();
    }
}
