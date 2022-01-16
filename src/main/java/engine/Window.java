package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private int width, height;
    private String title;
    private long glfwWindow;

    public float r, g, b, a;

    private static Scene currentScene;

    private static Window window = null;

    private Window() { //private, damit NIEMAND ein neues Window erstellen kann
        this.width = 1280;
        this.height = 720;
        this.r = 1.0f;
        this.g = 1.0f;
        this.b = 1.0f;
        this.a = 1.0f;
        this.title = "Engine";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window; //siehe Singleton
    }

    public static void changeScene(int sceneNumber) {
        switch (sceneNumber) {
            case 0:
                currentScene = new LevelEditorScene();
                //currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                //inti;
                break;
            default:
                assert false : "Unknown scene '" + sceneNumber + "'";
                break;
        }
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion());
        init();
        loop();

        //Free Memory after loop ended
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate GLFW, Free Error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public void init() {
        //setup error callback
        GLFWErrorCallback.createPrint(System.err).set(); //hiermit werden alle Fehler in die Konsole gepostet

        //initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        //configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        //create window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW_Window!");
        }

        //setup Callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback);

        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //make the OpenGL Context current
        glfwMakeContextCurrent(glfwWindow);
        //Enable VSync
        glfwSwapInterval(1); // swap every frame

        //make window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            //Poll all Events (keyboard, mouse, etc)
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);


            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
