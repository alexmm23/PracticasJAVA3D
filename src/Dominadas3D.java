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

    public Dominadas3D() {
        // Configuración de la ventana
        setTitle("Juego de Dominadas 3D");
        setSize(800, 800);
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

        return root;
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
        gameLoop.stop(); // Detener el bucle del juego mientras se muestra el diálogo

        int opcion = JOptionPane.showOptionDialog(
                this,
                "Has perdido. ¿Qué deseas hacer?",
                "Fin del juego",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Reiniciar", "Salir al menú"}, // Opciones
                "Reiniciar" // Opción predeterminada
        );

        if (opcion == JOptionPane.YES_OPTION) { // Opción "Reiniciar"
            reiniciarJuego();
        } else if (opcion == JOptionPane.NO_OPTION) { // Opción "Salir al menú"
            salirAlMenu();
        }
    }

    private void reiniciarJuego() {
        // Reiniciar las variables del juego
        balonX = 0.0f;
        balonY = 2.0f;
        velocidadBalonX = 0.0f;
        velocidadBalonY = 0.0f;
        contadorToques = 0;

        // Actualizar transformaciones iniciales
        transformBalon.setTranslation(new Vector3f(balonX, balonY, 0.0f));
        tgBalon.setTransform(transformBalon);

        // Reiniciar el bucle del juego
        gameLoop.start();
    }

    private void salirAlMenu() {
        // Aquí puedes implementar la lógica para regresar al menú principal.
        System.out.println("Saliendo al menú principal...");
        dispose(); // Cierra la ventana actual
        // Carga el menú principal o finaliza el programa
    }

    private void iniciarBucleDelJuego() {
        gameLoop = new Timer(16, e -> {
            // Movimiento del balón
            balonY += velocidadBalonY;
            balonX += velocidadBalonX;

            // Restricciones de los límites de la pantalla
            if (balonX > 3.5f || balonX < -3.5f) {
                velocidadBalonX = -velocidadBalonX;
                balonX = Math.max(-3.5f, Math.min(3.5f, balonX));
            }

            if (balonY <= -2.0f) { // Si el balón toca el suelo
                balonY = -2.0f;
                velocidadBalonY = 0.0f;
                velocidadBalonX = 0.0f;
                mostrarDialogoPerdida(); // Mostrar el cuadro de diálogo
            } else if (enContacto) {
                velocidadBalonY = 0.04f;
                velocidadBalonX = (float) (Math.random() * 0.02 - 0.01);
                contadorToques++;
                System.out.println("Toques: " + contadorToques);
                enContacto = false;
            }

            // Actualizar posición del balón
            transformBalon.setTranslation(new Vector3f(balonX, balonY, 0.0f));
            tgBalon.setTransform(transformBalon);

            // Aplicar gravedad
            velocidadBalonY -= 0.002f;

            // Detectar colisión con el mouse
            detectarColision();
        });
        gameLoop.start();
    }



    private void moverPie(int mouseX, int mouseY) {
        // Convertir coordenadas del mouse a coordenadas del universo
        float pieX = (mouseX - getWidth() / 2.0f) / 100.0f;
        float pieY = -(mouseY - getHeight() / 2.0f) / 100.0f;

        // Detectar colisión entre el pie y el balón
        if (Math.abs(balonX - pieX) < 0.3f && Math.abs(balonY - pieY) < 0.3f) {
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
