package stacs.graphics;

import stacs.graphics.engine.ApplicationEngine;
import stacs.graphics.logic.Configuration;
import stacs.graphics.logic.Interactive3DModel;

public class App {

    public void run(Configuration configuration) {
        var applicationLogic = new Interactive3DModel(configuration);
        var engine = new ApplicationEngine("Interactive 3D Modelling", 1700, 800, true, applicationLogic);
        engine.run();
    }

    public static void main(String[] args) {
        var configuration = new Configuration(args);
        new App().run(configuration);
    }
}
