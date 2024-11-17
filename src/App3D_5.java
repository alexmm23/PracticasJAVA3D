import com.sun.j3d.utils.behaviors.keyboard.KeyNavigator;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.*;

public class App3D_5 extends JPanel {
    SimpleUniverse universo;
    public App3D_5() {

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        setLayout(new BorderLayout());
        add(canvas3D);

        universo = new SimpleUniverse(canvas3D);
        universo.getViewingPlatform().setNominalViewingTransform();
        BranchGroup escena = crearGrafoEscena();
        escena.compile();
    }
    public BranchGroup crearGrafoEscena() {
        BranchGroup objetoRaiz = new BranchGroup();
        TransformGroup tecladoGroup = new TransformGroup();
        objetoRaiz.addChild(tecladoGroup);


        ColorCube cubo = new ColorCube(0.4f);
        tecladoGroup.addChild(cubo);

        KeyNavigatorBehavior knb = new KeyNavigatorBehavior(
            universo.getViewingPlatform().getViewPlatformTransform()
        );

        BoundingSphere bs = new BoundingSphere(new Point3d(),1000.0);
        knb.setSchedulingBounds(bs);
        tecladoGroup.addChild(knb);

        return objetoRaiz;



    }
}
