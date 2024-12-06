import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;

public class Dominadas3D extends JFrame {
    private Canvas3D canvas;
    private SimpleUniverse universe;
    private BranchGroup escena;
    private TransformGroup tgBalon;
    private Transform3D transformBalon;
    private Timer gameLoop;
    private float balonX = 0.0f; // Posición horizontal del balón
    private float balonY = 2.0f; // Altura inicial del balón
    private float velocidadBalonX = 0.0f; // Velocidad horizontal del balón
    private float velocidadBalonY = -0.02f; // Velocidad vertical del balón
    private boolean enContacto = false; // Estado del balón respecto al pie
    private int contadorToques = 0;

    private TransformGroup tgContador;
    private Text3D text3DContador;
    private TransformGroup tgMouse;
    private Transform3D transformMouse;

    public Dominadas3D() {
        // Configuración de la ventana
        setTitle("Juego de Dominadas 3D");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        add(canvas);

        // Configurar universo y escena
        universe = new SimpleUniverse(canvas);
        escena = crearEscena();
        escena.compile();
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(escena);

        // Configurar eventos del mouse
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                moverPie(e.getX(), e.getY());
            }
        });

        // Iniciar el bucle del juego
        iniciarBucleDelJuego();
    }

    private BranchGroup crearEscena() {
        BranchGroup root = new BranchGroup();

        // Fondo
        Background fondo = new Background();
        TextureLoader loader = new TextureLoader("src/texturas/cesped.jpg", this);
        fondo.setImage(loader.getImage());
        fondo.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0));
        fondo.setImageScaleMode(Background.SCALE_FIT_MAX);
        root.addChild(fondo);

        // Iluminación
        Color3f luzColor = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f direccionLuz = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight luz = new DirectionalLight(luzColor, direccionLuz);
        luz.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        root.addChild(luz);

        // Balón
        tgBalon = new TransformGroup();
        tgBalon.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformBalon = new Transform3D();
        tgBalon.setTransform(transformBalon);

        Appearance aparienciaBalon = crearTextura("src/texturas/balon.jpg");
        Sphere balon = new Sphere(0.3f, Sphere.GENERATE_TEXTURE_COORDS | Sphere.GENERATE_NORMALS, 100, aparienciaBalon);
        tgBalon.addChild(balon);
        root.addChild(tgBalon);

        // Esfera del mouse
        tgMouse = new TransformGroup();
        tgMouse.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformMouse = new Transform3D();
        tgMouse.setTransform(transformMouse);

        Appearance aparienciaMouse = new Appearance();
        Color3f colorMouse = new Color3f(1.0f, 0.0f, 0.0f); // Rojo
        ColoringAttributes ca = new ColoringAttributes(colorMouse, ColoringAttributes.NICEST);
        aparienciaMouse.setColoringAttributes(ca);

        Sphere mouseSphere = new Sphere(0.05f, aparienciaMouse); // Esfera pequeña para el mouse
        tgMouse.addChild(mouseSphere);
        root.addChild(tgMouse);

        // Contador 3D
        tgContador = crearTexto3D("Toques: 0", new Vector3f(-3.5f, 3.5f, 0.0f));
        root.addChild(tgContador);

        return root;
    }
    private TransformGroup crearTexto3D(String texto, Vector3f posicion) {
        Font3D font3D = new Font3D(new java.awt.Font("Arial", Font.BOLD, 1), new FontExtrusion());
        text3DContador = new Text3D(font3D, texto);
        text3DContador.setCapability(Text3D.ALLOW_STRING_WRITE); // Set capability to modify the string
        Appearance appearance = new Appearance();
        Material material = new Material();
        material.setDiffuseColor(new Color3f(1.0f, 1.0f, 1.0f)); // White color
        appearance.setMaterial(material);
        Shape3D shape3D = new Shape3D(text3DContador, appearance);
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(posicion);
        TransformGroup tgText = new TransformGroup(transform3D);
        tgText.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); // Set capability to modify the transform
        tgText.addChild(shape3D);
        return tgText;
    }

    private Appearance crearTextura(String path) {
        TextureLoader loader = new TextureLoader(path, this);
        Texture textura = loader.getTexture();

        Appearance apariencia = new Appearance();
        apariencia.setTexture(textura);

        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        Material material = new Material();
        material.setLightingEnable(true);
        apariencia.setMaterial(material);

        return apariencia;
    }

    private void mostrarDialogoPerdida() {
        gameLoop.stop();

        int opcion = JOptionPane.showOptionDialog(
                this,
                "Has perdido. ¿Qué deseas hacer?",
                "Fin del juego",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Reiniciar", "Salir al menú"},
                "Reiniciar"
        );

        if (opcion == JOptionPane.YES_OPTION) {
            reiniciarJuego();
        } else if (opcion == JOptionPane.NO_OPTION) {
            salirAlMenu();
        }
    }

    private void reiniciarJuego() {
        balonX = 0.0f;
        balonY = 2.0f;
        velocidadBalonX = 0.0f;
        velocidadBalonY = 0.0f;
        contadorToques = 0;

        transformBalon.setTranslation(new Vector3f(balonX, balonY, 0.0f));
        tgBalon.setTransform(transformBalon);

        gameLoop.start();
    }

    private void salirAlMenu() {
        System.out.println("Saliendo al menú principal...");
        dispose();
    }

    private void iniciarBucleDelJuego() {
        gameLoop = new Timer(16, e -> {
            // Movimiento del balón
            balonY += velocidadBalonY;
            balonX += velocidadBalonX;

            if (balonX > 3.5f || balonX < -3.5f) {
                velocidadBalonX = -velocidadBalonX;
                balonX = Math.max(-3.5f, Math.min(3.5f, balonX));
            }

            if (balonY <= -2.0f) {
                balonY = -2.0f;
                velocidadBalonY = 0.0f;
                velocidadBalonX = 0.0f;
                mostrarDialogoPerdida();
            } else if (enContacto) {
                velocidadBalonY = 0.05f;
                velocidadBalonX = (float) (Math.random() * 0.02 - 0.01);
                contadorToques++;
                enContacto = false;
                System.out.println("Toques: " + contadorToques);

                // Update 3D text counter
                text3DContador.setString("Toques: " + contadorToques);
                // Update 3D text counter
                text3DContador.setString("Toques: " + contadorToques);
                Transform3D transform3D = new Transform3D();
                transform3D.setTranslation(new Vector3f(-3.5f, 3.5f, 0.0f));
                tgContador.setTransform(transform3D);


            }
            transformBalon.setTranslation(new Vector3f(balonX, balonY, 0.0f));
            tgBalon.setTransform(transformBalon);
            velocidadBalonY -= 0.002f;
        });
        gameLoop.start();
    }

    private void moverPie(int mouseX, int mouseY) {
        // Convertir coordenadas del mouse a coordenadas del universo
        float pieX = (mouseX - getWidth() / 2.0f) / 100.0f;
        float pieY = -(mouseY - getHeight() / 2.0f) / 100.0f;

        // Actualizar la posición de la esfera del mouse
        transformMouse.setTranslation(new Vector3f(pieX, pieY, 0.0f));
        tgMouse.setTransform(transformMouse);

        // Calcular la distancia entre el pie y el balón
        float distancia = (float) Math.sqrt(Math.pow(balonX - pieX, 2) + Math.pow(balonY - pieY, 2));

        // Detectar colisión entre el pie y el balón
        if (distancia < 0.3f) { // 0.3f es el radio del balón
            enContacto = true;
        }
    }

    private void detectarColision() {
        // Ya detectamos colisión en moverPie
    }

    public static void main(String[] args) {
        Dominadas3D juego = new Dominadas3D();
        juego.setVisible(true);
    }
}
