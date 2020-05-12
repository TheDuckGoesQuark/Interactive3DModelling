package stacs.graphics;

import stacs.graphics.engine.ApplicationEngine;
import stacs.graphics.logic.Interactive3DModel;

public class App {

    public void run() {
        var applicationLogic = new Interactive3DModel();
        var engine = new ApplicationEngine("Interactive 3D Modelling", 1700, 800, true, applicationLogic);
        engine.run();
    }

    public static void main(String[] args) throws Exception {
        new App().run();
    }
}
