import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.*;

public class App3D_4 extends JPanel {
    SimpleUniverse universe;
    public App3D_4() {
        // This is the constructor of the class
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        setLayout(new BorderLayout());
        add(canvas);
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
        BranchGroup escena= crearGrafoEscena();
        escena.compile();

        universe.addBranchGraph(escena);
    }
    public BranchGroup crearGrafoEscena() {
        BranchGroup objRoot = new BranchGroup();
        TransformGroup mouseGroup = new TransformGroup();
        mouseGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mouseGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRoot.addChild(mouseGroup);


        ColorCube cubo = new ColorCube(0.4f);
        mouseGroup.addChild(cubo);

        MouseRotate mr = new MouseRotate();
        mr.setTransformGroup(mouseGroup);
        mr.setSchedulingBounds(new BoundingSphere(new Point3d(),1000f));

        objRoot.addChild(mr);

        return objRoot;
    }
}
