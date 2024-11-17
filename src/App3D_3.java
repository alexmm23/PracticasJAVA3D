import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Vector3f;
import java.awt.*;

public class App3D_3 extends JPanel {
    public App3D_3() {
        // This is the constructor of the class
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        setLayout(new BorderLayout());
        add(canvas);
        SimpleUniverse universe = new SimpleUniverse(canvas);


        Vector3f posicionVista = new Vector3f();
        posicionVista.x = 2f;
        posicionVista.y = 5f;
        posicionVista.z = 20f;

        Transform3D vista = new Transform3D();
        vista.setTranslation(posicionVista);

        Transform3D rotacion = new Transform3D();
        rotacion.rotX(Math.toRadians(45));
        rotacion.mul(vista);

        universe.getViewingPlatform().getViewPlatformTransform().setTransform(rotacion);
        universe.getViewingPlatform().getViewPlatformTransform().getTransform(vista);

        BranchGroup escena= crearGrafoEscena();
        escena.compile();
        universe.addBranchGraph(escena);

    }

    public BranchGroup crearGrafoEscena() {
        BranchGroup objRoot = new BranchGroup();

        TransformGroup objetoGiro = new TransformGroup();
        objetoGiro.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRoot.addChild(objetoGiro);

        ColorCube cubo = new ColorCube(0.8f);
        objetoGiro.addChild(cubo);

        Alpha rotacionAlpha = new Alpha(-1, 4000);

        RotationInterpolator rotacion = new RotationInterpolator(rotacionAlpha, objetoGiro);
        rotacion.setSchedulingBounds(new BoundingSphere());
        objetoGiro.addChild(rotacion);

        return objRoot;
    }


}
