import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import java.awt.*;
import java.awt.event.*;

public class Shapes3DPanel extends JPanel implements KeyListener {
    private TransformGroup cubeTransformGroup;
    private TransformGroup cylinderTransformGroup;
    private double cubeRotX = 0.0;
    private double cubeRotY = 0.0;
    private double cubeRotZ = 0.0;
    private double cylinderRotX = 0.0;
    private double cylinderRotY = 0.0;
    private double cylinderRotZ = 0.0;

    public Shapes3DPanel() {
        setLayout(new GridLayout(1, 2));  // Dos columnas para los dos canvas
        setPreferredSize(new Dimension(800, 600));

        // Canvas para el cubo
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas1 = new Canvas3D(config);
        add(canvas1);

        // Canvas para el cilindro
        Canvas3D canvas2 = new Canvas3D(config);
        add(canvas2);

        // Crear un SimpleUniverse para cada figura
        SimpleUniverse universe1 = new SimpleUniverse(canvas1);
        SimpleUniverse universe2 = new SimpleUniverse(canvas2);

        // Configurar la vista para cada universo
        configureView(universe1);
        configureView(universe2);

        // Crear los BranchGroups y añadirlos a cada universo
        BranchGroup scene1 = createCubeSceneGraph();
        BranchGroup scene2 = createCylinderSceneGraph();
        universe1.addBranchGraph(scene1);
        universe2.addBranchGraph(scene2);

        canvas1.setFocusable(true);
        canvas1.requestFocus();
        canvas2.setFocusable(true);
        canvas2.requestFocus();

        // Añadir los listeners de teclado
        canvas1.addKeyListener(this);
        canvas2.addKeyListener(this);
    }

    private void configureView(SimpleUniverse universe) {
        // Configurar la cámara para cada SimpleUniverse
        ViewingPlatform viewingPlatform = universe.getViewingPlatform();
        TransformGroup viewingTransformGroup = viewingPlatform.getViewPlatformTransform();
        Transform3D viewTransform = new Transform3D();
        viewTransform.setTranslation(new Vector3d(0.0, 0.5, 12.0));  // Cámara atrás
        Transform3D rotX = new Transform3D();
        rotX.rotX(-Math.PI / 16.0); // Ligeramente hacia abajo
        viewTransform.mul(rotX);
        viewingTransformGroup.setTransform(viewTransform);
    }

    private BranchGroup createCubeSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Configurar el fondo
        TextureLoader loader = new TextureLoader("src/img/background.jpeg", this);
        Background background = new Background(loader.getImage());
        background.setApplicationBounds(new BoundingSphere(new Point3d(), 100.0));
        root.addChild(background);

        // Configurar la iluminación
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        DirectionalLight light1 = new DirectionalLight(
                new Color3f(0.8f, 0.8f, 0.8f),
                new Vector3f(-1.0f, -1.0f, -1.0f)
        );
        light1.setInfluencingBounds(bounds);
        root.addChild(light1);

        // Crear el cubo
        cubeTransformGroup = new TransformGroup();
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Appearance cubeAppearance = new Appearance();
        Material cubeMaterial = new Material(
                new Color3f(1.0f, 0.0f, 0.0f),  // ambient
                new Color3f(0.0f, 0.0f, 0.0f),  // emissive
                new Color3f(1.0f, 0.0f, 0.0f),  // diffuse
                new Color3f(0.7f, 0.7f, 0.7f),  // specular
                128.0f                          // shininess
        );
        cubeAppearance.setMaterial(cubeMaterial);

        Box cube = new Box(2f, 2f, 2f, Box.GENERATE_NORMALS, cubeAppearance);

        // Posicionar el cubo
        Transform3D cubePosition = new Transform3D();
        cubePosition.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        cubeTransformGroup.setTransform(cubePosition);
        cubeTransformGroup.addChild(cube);
        root.addChild(cubeTransformGroup);

        root.compile();
        return root;
    }

    private BranchGroup createCylinderSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Configurar el fondo
        TextureLoader loader = new TextureLoader("src/img/background.jpeg", this);
        Background background = new Background(loader.getImage());
        background.setApplicationBounds(new BoundingSphere(new Point3d(), 100.0));
        root.addChild(background);

        // Configurar la iluminación
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        DirectionalLight light1 = new DirectionalLight(
                new Color3f(0.8f, 0.8f, 0.8f),
                new Vector3f(-1.0f, -1.0f, -1.0f)
        );
        light1.setInfluencingBounds(bounds);
        root.addChild(light1);

        // Crear el cilindro
        cylinderTransformGroup = new TransformGroup();
        cylinderTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Appearance cylinderAppearance = new Appearance();
        Material cylinderMaterial = new Material(
                new Color3f(0.0f, 0.0f, 1.0f),  // ambient
                new Color3f(0.0f, 0.0f, 0.0f),  // emissive
                new Color3f(0.0f, 0.0f, 1.0f),  // diffuse
                new Color3f(0.7f, 0.7f, 0.7f),  // specular
                128.0f                          // shininess
        );
        cylinderAppearance.setMaterial(cylinderMaterial);

        Cylinder cylinder = new Cylinder(2f, 4.0f, Cylinder.GENERATE_NORMALS, cylinderAppearance);

        // Posicionar el cilindro
        Transform3D cylinderPosition = new Transform3D();
        cylinderPosition.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        cylinderTransformGroup.setTransform(cylinderPosition);
        cylinderTransformGroup.addChild(cylinder);
        root.addChild(cylinderTransformGroup);

        root.compile();
        return root;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        double rotationAngle = 0.1;

        // Rotación del cubo
        if (e.isShiftDown()) {
            Transform3D currentTransform = new Transform3D();
            cubeTransformGroup.getTransform(currentTransform);

            Transform3D rotationTransform = new Transform3D();
            Vector3d translation = new Vector3d();
            currentTransform.get(translation);

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X:
                    rotationTransform.rotX(rotationAngle);
                    cubeRotX += rotationAngle;
                    break;
                case KeyEvent.VK_Y:
                    rotationTransform.rotY(rotationAngle);
                    cubeRotY += rotationAngle;
                    break;
                case KeyEvent.VK_Z:
                    rotationTransform.rotZ(rotationAngle);
                    cubeRotZ += rotationAngle;
                    break;
            }

            currentTransform.mul(rotationTransform);
            currentTransform.setTranslation(translation);
            cubeTransformGroup.setTransform(currentTransform);
        }
        // Rotación del cilindro
        else {
            Transform3D currentTransform = new Transform3D();
            cylinderTransformGroup.getTransform(currentTransform);

            Transform3D rotationTransform = new Transform3D();
            Vector3d translation = new Vector3d();
            currentTransform.get(translation);

            switch (e.getKeyCode()) {
                case KeyEvent.VK_X:
                    rotationTransform.rotX(rotationAngle);
                    cylinderRotX += rotationAngle;
                    break;
                case KeyEvent.VK_Y:
                    rotationTransform.rotY(rotationAngle);
                    cylinderRotY += rotationAngle;
                    break;
                case KeyEvent.VK_Z:
                    rotationTransform.rotZ(rotationAngle);
                    cylinderRotZ += rotationAngle;
                    break;
            }

            currentTransform.mul(rotationTransform);
            currentTransform.setTranslation(translation);
            cylinderTransformGroup.setTransform(currentTransform);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Java3D Shapes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Shapes3DPanel panel = new Shapes3DPanel();
        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
