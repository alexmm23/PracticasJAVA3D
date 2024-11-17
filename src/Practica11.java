import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;

public class Practica11 extends JPanel {
    private TransformGroup transformGroupCube;
    private TransformGroup transformGroupCylinder;
    private Transform3D transformCube = new Transform3D();
    private Transform3D transformCylinder = new Transform3D();
    private double angleX = 0;
    private double angleY = 0;
    private double angleZ = 0;

    public Practica11() {
        setLayout(new BorderLayout());

        // Configuración del canvas y universo
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas, BorderLayout.CENTER);
        SimpleUniverse universe = new SimpleUniverse(canvas);

        BranchGroup scene = createSceneGraph();
        scene.compile();

        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);

        // Controles para rotación
        JPanel controlPanel = new JPanel();
        JButton rotateXButton = new JButton("Rotate X");
        JButton rotateYButton = new JButton("Rotate Y");
        JButton rotateZButton = new JButton("Rotate Z");

        controlPanel.add(rotateXButton);
        controlPanel.add(rotateYButton);
        controlPanel.add(rotateZButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Listeners para los botones
        rotateXButton.addActionListener(e -> rotate(0.1, 0, 0));
        rotateYButton.addActionListener(e -> rotate(0, 0.1, 0));
        rotateZButton.addActionListener(e -> rotate(0, 0, 0.1));
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Fondo con textura
        addBackground(root);

        // Configuración del cubo
        transformGroupCube = new TransformGroup();
        transformGroupCube.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ColorCube cube = new ColorCube(0.2);

        Transform3D cubePosition = new Transform3D();
        cubePosition.setTranslation(new Vector3f(-0.8f, 0f, -3f)); // Mueve el cubo a la izquierda
        transformGroupCube.setTransform(cubePosition);
        transformGroupCube.addChild(cube);
        root.addChild(transformGroupCube);

        // Configuración del cilindro
        transformGroupCylinder = new TransformGroup();
        transformGroupCylinder.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Cylinder cylinder = new Cylinder(0.2f, 0.5f);

        Transform3D cylinderPosition = new Transform3D();
        cylinderPosition.setTranslation(new Vector3f(0.8f, 0f, -3f)); // Mueve el cilindro a la derecha
        transformGroupCylinder.setTransform(cylinderPosition);
        transformGroupCylinder.addChild(cylinder);
        root.addChild(transformGroupCylinder);

        return root;
    }

    private void addBackground(BranchGroup root) {
        // Cargar la textura del fondo
        TextureLoader loader = new TextureLoader("src/img/background.jpeg", null);
        ImageComponent2D image = loader.getImage();

        if (image != null) {
            Background background = new Background();
            background.setImage(image);

            // Escala para abarcar toda la pantalla
            background.setImageScaleMode(Background.SCALE_FIT_ALL);

            // Ajustar los límites del fondo
            BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
            background.setApplicationBounds(bounds);

            root.addChild(background);
        }
    }

    private void rotate(double deltaX, double deltaY, double deltaZ) {
        angleX += deltaX;
        angleY += deltaY;
        angleZ += deltaZ;

        // Rotar el cubo
        Transform3D rotationX = new Transform3D();
        Transform3D rotationY = new Transform3D();
        Transform3D rotationZ = new Transform3D();

        rotationX.rotX(angleX);
        rotationY.rotY(angleY);
        rotationZ.rotZ(angleZ);

        transformCube.setIdentity();
        transformCube.mul(rotationX);
        transformCube.mul(rotationY);
        transformCube.mul(rotationZ);
        transformGroupCube.setTransform(transformCube);

        // Rotar el cilindro
        Transform3D rotationCylinder = new Transform3D();
        rotationCylinder.mul(rotationX);
        rotationCylinder.mul(rotationY);
        rotationCylinder.mul(rotationZ);
        transformCylinder.setIdentity();
        transformCylinder.mul(rotationCylinder);
        transformGroupCylinder.setTransform(transformCylinder);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Practica 11");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new Practica11(), BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
