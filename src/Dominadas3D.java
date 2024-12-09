import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.sound.sampled.Clip;

public class Dominadas3D extends JFrame {
    private Canvas3D canvas;
    private SimpleUniverse universe;
    private BranchGroup escena;
    private TransformGroup tgBalon;
    private Transform3D transformBalon;
    private Transform3D rotacionBalon;
    private Timer gameLoop;
    private float balonX = 0.0f;
    private float balonY = 2.0f;
    private float velocidadBalonX = 0.0f;
    private float velocidadBalonY = -0.02f;
    private boolean enContacto = false;
    private int contadorToques = 0;

    private TransformGroup tgMouse;
    private Transform3D transformMouse;
    private JLabel contadorLabel;
    private int mouseXAnterior = 0;
    private int mouseYAnterior = 0;
    private long tiempoAnterior = System.currentTimeMillis();
    private Clip musicaDeFondo;
    private Clip sonidoGolpe;

    private void cargarMusicaDeFondo() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/sonidos/samba.wav"));
            musicaDeFondo = AudioSystem.getClip();
            musicaDeFondo.open(audioInputStream);
            musicaDeFondo.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException e) {
            System.err.println("Error de línea de audio: " + e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Formato de audio no soportado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        }
    }
    private void cargarSonidoGolpe() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/sonidos/patear_balon.wav"));
            sonidoGolpe = AudioSystem.getClip();
            sonidoGolpe.open(audioInputStream);
        } catch (LineUnavailableException e) {
            System.err.println("Error de línea de audio: " + e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Formato de audio no soportado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
        }
    }
    private void reproducirSonidoGolpe() {
        if (sonidoGolpe != null) {
            // Reiniciar y reproducir
            sonidoGolpe.setFramePosition(0);
            sonidoGolpe.start();
        }
    }
    public Dominadas3D() {
        setTitle("Juego de Dominadas 3D");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        cargarMusicaDeFondo();
        cargarSonidoGolpe();

        if (musicaDeFondo != null) {
            musicaDeFondo.start();
        }
        // Crear un cursor transparente
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        add(canvas, BorderLayout.CENTER);

        universe = new SimpleUniverse(canvas);
        escena = crearEscena();
        escena.compile();
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(escena);

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                moverPie(e.getX(), e.getY());
            }
        });

        // Añadir KeyListener para eventos de teclado
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                manejarEventoTeclado(e);
            }
        });

        contadorLabel = new JLabel("Toques: 0");
        contadorLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contadorLabel.setForeground(Color.WHITE);
        add(contadorLabel, BorderLayout.NORTH);

        iniciarBucleDelJuego();
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();
    }

    private void manejarEventoTeclado(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (gameLoop.isRunning()) {
                gameLoop.stop();
                System.out.println("Juego pausado");
            } else {
                gameLoop.start();
                System.out.println("Juego reanudado");
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_M) {
            if (musicaDeFondo.isRunning()) {
                musicaDeFondo.stop();
            } else {
                musicaDeFondo.start();
            }
        }
    }

    private void limpiarRecursosAudio() {
        if (musicaDeFondo != null) {
            musicaDeFondo.stop();
            musicaDeFondo.close();
        }
        if (sonidoGolpe != null) {
            sonidoGolpe.stop();
            sonidoGolpe.close();
        }
    }

    private BranchGroup crearEscena() {
        BranchGroup root = new BranchGroup();

        // Fondo
        Background fondo = new Background();
        TextureLoader loader = new TextureLoader("src/img/cesped2.jpg", this);
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
        rotacionBalon = new Transform3D();
        tgBalon.setTransform(transformBalon);

        Appearance aparienciaBalon = crearTextura("src/texturas/balon.jpg");
        Sphere balon = new Sphere(0.15f, Sphere.GENERATE_TEXTURE_COORDS | Sphere.GENERATE_NORMALS, 100, aparienciaBalon);
        tgBalon.addChild(balon);
        root.addChild(tgBalon);
        tgMouse = new TransformGroup();
        tgMouse.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformMouse = new Transform3D();
        tgMouse.setTransform(transformMouse);

        Appearance aparienciaMouse = crearTextura("src/texturas/pie.jpg"); // Imagen personalizada para el mouse
        com.sun.j3d.utils.geometry.Box mouseBox = new com.sun.j3d.utils.geometry.Box(0.05f, 0.05f, 0.05f, com.sun.j3d.utils.geometry.Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS, aparienciaMouse);
        tgMouse.addChild(mouseBox);
        root.addChild(tgMouse);

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

        contadorLabel.setText("Toques: 0");

        gameLoop.start();
    }

    private void salirAlMenu() {
        limpiarRecursosAudio();
        System.out.println("Saliendo al menú principal...");
        dispose();
        MenuPrincipal menu = new MenuPrincipal();
        menu.setVisible(true);
    }

    private void iniciarBucleDelJuego() {
        gameLoop = new Timer(16, e -> {
            balonY += velocidadBalonY;
            balonX += velocidadBalonX;

            // Rebotar en los bordes en X
            if (balonX > 1.0f || balonX < -1.0f) {
                velocidadBalonX = -velocidadBalonX;
                balonX = Math.max(-1.0f, Math.min(1.0f, balonX));
            }

            // Rebotar en los bordes en Y
            if (balonY > 2.0f || balonY < -2.0f) {
                velocidadBalonY = -velocidadBalonY;
                balonY = Math.max(-2.0f, Math.min(2.0f, balonY));
            }

            if (balonY <= -2.0f) {
                balonY = -2.0f;
                velocidadBalonY = 0.0f;
                velocidadBalonX = 0.0f;
                mostrarDialogoPerdida();
            } else if (enContacto) {
                velocidadBalonY = 0.02f + (contadorToques * 0.00005f);
                velocidadBalonX = (float) (Math.random() * 0.02 - 0.01);
                contadorToques++;
                enContacto = false;
                System.out.println("Toques: " + contadorToques);
                contadorLabel.setText("Toques: " + contadorToques);
            }

            // Rotación más natural del balón
            Transform3D rotacionX = new Transform3D();
            Transform3D rotacionY = new Transform3D();
            Transform3D rotacionZ = new Transform3D();

            // Rotar en base a la velocidad de movimiento
            rotacionX.rotX(velocidadBalonY * 5);    // Rotación en X basada en velocidad vertical
            rotacionY.rotY(velocidadBalonX * 5);    // Rotación en Y basada en velocidad horizontal
            rotacionZ.rotZ(0.05);                   // Rotación constante en Z para efecto de giro

            rotacionBalon.setIdentity();
            rotacionBalon.mul(rotacionX);
            rotacionBalon.mul(rotacionY);
            rotacionBalon.mul(rotacionZ);

            try {
                transformBalon.setIdentity();
                transformBalon.setTranslation(new Vector3f(balonX, balonY, 0.0f));
                transformBalon.mul(rotacionBalon);
                tgBalon.setTransform(transformBalon);
                velocidadBalonY -= 0.002f + (balonY * 0.001f);
            } catch (Exception ex) {
                ex.printStackTrace();
                transformBalon.setIdentity();
                tgBalon.setTransform(transformBalon);
            }
        });
        gameLoop.start();
    }

    private void moverPie(int mouseX, int mouseY) {
        long tiempoActual = System.currentTimeMillis();
        long deltaTiempo = tiempoActual - tiempoAnterior;
        float velocidadMouseX = (mouseX - mouseXAnterior) / (float) deltaTiempo;
        float velocidadMouseY = (mouseY - mouseYAnterior) / (float) deltaTiempo;

        mouseXAnterior = mouseX;
        mouseYAnterior = mouseY;
        tiempoAnterior = tiempoActual;

        float pieX = (mouseX - getWidth() / 2.0f) / 100.0f;
        float pieY = -(mouseY - getHeight() / 2.0f) / 100.0f;

        transformMouse.setTranslation(new Vector3f(pieX, pieY, 0.0f));
        tgMouse.setTransform(transformMouse);
        float distancia = (float) Math.sqrt(Math.pow(balonX - pieX, 2) + Math.pow(balonY - pieY, 2));

        if (distancia < 0.3f) {
            enContacto = true;
            float anguloImpacto = (float) Math.atan2(pieY - balonY, pieX - balonX);
            velocidadBalonY = 0.05f + Math.abs(velocidadMouseY) * 0.1f;
            velocidadBalonX = 0.05f * (float) Math.cos(anguloImpacto);
            velocidadBalonX += (float) (Math.random() * 0.02 - 0.01);
            reproducirSonidoGolpe();
        }
    }
    @Override
    public void dispose() {
        limpiarRecursosAudio();
        super.dispose();
    }

    public static void main(String[] args) {
        Dominadas3D juego = new Dominadas3D();
        juego.setVisible(true);
    }
}