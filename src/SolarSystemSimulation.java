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
        setTitle("Sistema Solar");
        setSize(1600, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        add(canvas);

        universe = new SimpleUniverse(canvas);

        // Ajustar la vista para alejar la cámara
        universe.getViewingPlatform().setNominalViewingTransform();
        TransformGroup viewingTransform = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D transform = new Transform3D();
        transform.setTranslation(new Vector3d(0.0, 0.0, 10.0)); // Alejar la cámara
        viewingTransform.setTransform(transform);

        BranchGroup escena = crearEscena();
        escena.compile();
        universe.addBranchGraph(escena);
    }


    private BranchGroup crearEscena() {
        BranchGroup raiz = new BranchGroup();
        //Background fondo = new Background(new Color3f(Color.BLACK));
        //fondo.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        //raiz.addChild(fondo);
        setBackgroundImage(raiz, "src/img/fondo.jpeg");
        Sphere sol = crearEsferaConTextura(0.4f, "sol.jpg");
        TransformGroup tgSol = new TransformGroup();
        tgSol.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgSol.addChild(sol);
        raiz.addChild(tgSol);
        Alpha alphaSol = new Alpha(-1, 4000); // Animación infinita
        RotationInterpolator rotacionSol = new RotationInterpolator(alphaSol, tgSol);
        rotacionSol.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        tgSol.addChild(rotacionSol);
        configurarLuzSolar(raiz, tgSol);
        Sphere tierra = crearEsferaConTextura(0.2f, "tierra.jpg");
        TransformGroup tgRotacionTierra = new TransformGroup();
        tgRotacionTierra.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgRotacionTierra.addChild(tierra);
        Alpha alphaTierra = new Alpha(-1, 2000); // Rotación infinita
        RotationInterpolator rotacionTierra = new RotationInterpolator(alphaTierra, tgRotacionTierra);
        rotacionTierra.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        tgRotacionTierra.addChild(rotacionTierra);
        TransformGroup tgOrbitaTierra = crearOrbita(tgRotacionTierra, 4.0, true);
        raiz.addChild(tgOrbitaTierra);
        String texto = "Sistema solar";
        TransformGroup tgTexto = crearTexto3D(texto, new Vector3f(-2.0f, -2.0f, -1.0f));
        raiz.addChild(tgTexto);

        return raiz;
    }
    private void setBackgroundImage(BranchGroup raiz, String path) {
        // Cargar la imagen
        TextureLoader loader = new TextureLoader(path, null);  // null indica el componente de contenedor
        ImageComponent2D image = loader.getImage();

        // Verificar si la imagen se carga correctamente
        if (image == null) {
            System.err.println("Error al cargar la imagen de fondo: " + path);
            return;
        }

        // Crear el fondo con la imagen
        Background fondo = new Background();
        fondo.setImage(image);
        fondo.setImageScaleMode(Background.SCALE_FIT_ALL); // Asegura que la imagen cubra toda la pantalla
        fondo.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0)); // Ajustar el radio

        // Añadir el fondo a la escena
        raiz.addChild(fondo);
    }

    private void configurarLuzSolar(BranchGroup root, TransformGroup tgSol) {
        PointLight luzSolar = new PointLight();
        luzSolar.setColor(new Color3f(1.0f, 1.0f, 0.8f));
        luzSolar.setPosition(new Point3f(0.0f, 0.0f, 0.0f));
        luzSolar.setAttenuation(1.0f, 0.1f, 0.01f);
        luzSolar.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        root.addChild(luzSolar);
    }

    private Sphere crearEsferaConTextura(float radio, String texturaArchivo) {
        Appearance apariencia = new Appearance();
        System.out.println("Ruta actual: " + new java.io.File(".").getAbsolutePath());
        String absolutePath = new java.io.File("").getAbsolutePath();
        System.out.println(absolutePath);
        TextureLoader cargador = new TextureLoader(absolutePath + "/src/texturas/" + texturaArchivo, this);
        Texture textura = cargador.getTexture();

        if (textura == null) {
            System.err.println("Error cargando la textura: " + texturaArchivo);
            System.err.println("Ruta actual: " + new java.io.File(".").getAbsolutePath());
            return new Sphere(radio);
        }
        apariencia.setTexture(textura);

        TextureAttributes atributosTextura = new TextureAttributes();
        atributosTextura.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(atributosTextura);

        Material material = new Material();
        material.setLightingEnable(true);
        if (texturaArchivo.contains("sol")) {
            material.setEmissiveColor(new Color3f(1.0f, 1.0f, 0.8f));
        }
        apariencia.setMaterial(material);

        return new Sphere(radio, Sphere.GENERATE_TEXTURE_COORDS | Sphere.GENERATE_NORMALS, 50, apariencia);
    }
    private TransformGroup crearTexto3D(String texto, Vector3f posicion) {
        Font3D fuente = new Font3D(new java.awt.Font("Arial", Font.BOLD, 1), new FontExtrusion());
        Text3D texto3D = new Text3D(fuente, texto);
        Appearance apariencia = new Appearance();
        Material material = new Material();
        material.setDiffuseColor(new Color3f(1.0f, 1.0f, 1.0f)); // Blanco
        material.setEmissiveColor(new Color3f(1.0f, 1.0f, 0.8f));
        apariencia.setMaterial(material);
        Shape3D shape = new Shape3D(texto3D, apariencia);
        Transform3D transformacion = new Transform3D();
        transformacion.setTranslation(posicion);
        TransformGroup tgTexto = new TransformGroup(transformacion);
        tgTexto.addChild(shape);
        return tgTexto;
    }


    private TransformGroup crearOrbita(Node objeto, double radioOrbita, boolean sentidoHorario) {
        TransformGroup tgOrbita = new TransformGroup();
        tgOrbita.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D transformacion = new Transform3D();
        Alpha alpha = new Alpha(-1, 10000); // Tiempo completo de la órbita

        RotationInterpolator rotacion = new RotationInterpolator(
                alpha,
                tgOrbita,
                transformacion,
                0.0f,
                sentidoHorario ? (float) Math.PI * 2 : -(float) Math.PI * 2
        );

        Transform3D translacion = new Transform3D();
        translacion.setTranslation(new Vector3d(radioOrbita, 0, 0));
        TransformGroup tgObjeto = new TransformGroup(translacion);
        tgObjeto.addChild(objeto);

        tgOrbita.addChild(rotacion);
        tgOrbita.addChild(tgObjeto);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        rotacion.setSchedulingBounds(bounds);

        return tgOrbita;
    }


    public static void main(String[] args) {
        SolarSystemSimulation simulacion = new SolarSystemSimulation();
        simulacion.setVisible(true);
    }
}
