import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;

import java.awt.*;
import javax.swing.JFrame;

public class SolarSystemSimulation extends JFrame {
    private Canvas3D canvas;
    private SimpleUniverse universe;

    public SolarSystemSimulation() {
        // Configuración de la ventana
        setTitle("Simulación de Sistema Solar");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración del Canvas 3D
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        add(canvas);

        // Creación del Universo
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();

        // Crear la escena
        BranchGroup escena = crearEscena();
        escena.compile();
        universe.addBranchGraph(escena);
    }

    private BranchGroup crearEscena() {
        BranchGroup raiz = new BranchGroup();

        // Configurar iluminación
        Color3f luzAmbiente = new Color3f(0.3f, 0.3f, 0.3f);
        AmbientLight luz = new AmbientLight(luzAmbiente);
        raiz.addChild(luz);

        // Luz direccional para sombras
        Color3f luzColor = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f direccionLuz = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight luzDireccional = new DirectionalLight(luzColor, direccionLuz);
        raiz.addChild(luzDireccional);

        // Fondo espacial
        Background fondo = new Background(new Color3f(Color.BLACK));
        fondo.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        raiz.addChild(fondo);

        // Crear Sol
        Sphere sol = crearEsfera("texturas/sol.jpg", 2.0f);
        TransformGroup tgSol = new TransformGroup();
        Transform3D rotacionSol = new Transform3D();
        rotacionSol.rotZ(Math.PI / 4);
        tgSol.setTransform(rotacionSol);
        tgSol.addChild(sol);
        raiz.addChild(tgSol);

        // Crear Tierra
        Sphere tierra = crearEsfera("texturas/tierra.jpg", 1.0f);
        TransformGroup tgTierra = crearObjetoRotatorio(tierra, new Vector3d(5.0, 0.0, 0.0));
        raiz.addChild(tgTierra);

        // Texto 3D
        Text3D texto = new Text3D(new Font3D(new Font("Arial", Font.BOLD, 1), new FontExtrusion()),
                "Sistema Solar", new Point3f(-2.0f, 3.0f, 0.0f));
        Shape3D formaTexto = new Shape3D(texto);
        raiz.addChild(formaTexto);

        return raiz;
    }

    private Sphere crearEsfera(String rutaTextura, float radio) {
        // Crear esfera con textura
        Appearance apariencia = new Appearance();
        try {
            TextureLoader cargadorTextura = new TextureLoader("src/" + rutaTextura, this);
            Texture textura = cargadorTextura.getTexture();
            apariencia.setTexture(textura);
        } catch (Exception e) {
            System.out.println("Error al cargar la textura: " + rutaTextura);
            System.out.println(e.getMessage());
        }

        return new Sphere(radio, Sphere.GENERATE_NORMALS | Sphere.GENERATE_TEXTURE_COORDS, 50, apariencia);
    }

    private TransformGroup crearObjetoRotatorio(Node objeto, Vector3d posicion) {
        TransformGroup tg = new TransformGroup();
        Transform3D transformacion = new Transform3D();
        transformacion.setTranslation(posicion);
        tg.setTransform(transformacion);

        // Rotación
        TransformGroup rotacion = new TransformGroup();
        rotacion.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Alpha alpha = new Alpha(-1, 4000);
        RotationInterpolator rotador = new RotationInterpolator(alpha, rotacion);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        rotador.setSchedulingBounds(bounds);
        rotacion.addChild(rotador);

        rotacion.addChild(objeto);
        tg.addChild(rotacion);

        return tg;
    }

    public static void main(String[] args) {
        SolarSystemSimulation simulacion = new SolarSystemSimulation();
        simulacion.setVisible(true);
    }
}