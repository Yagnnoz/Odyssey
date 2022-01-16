package engine;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSource = "#version 330 core\n" +
            "\n" +
            "layout(location=0) in vec3 aPos;\n" +
            "layout(location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main(){\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSource = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main(){\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
            //position XYZ           color RGBA
            0.5f, -0.5f, 0.0f,          1.0f, 0.0f, 0.0f, 1.0f, //Bottom right pixel
            -0.5f, 0.5f, 0.0f,          0.0f, 1.0f, 0.0f, 1.0f, //Top left
            0.5f, 0.5f, 0.0f,           0.0f, 0.0f, 1.0f, 1.0f, //Top right
            -0.5f, -0.5f, 0.0f,         1.0f, 1.0f, 0.0f, 1.0f, //Bottom left
    };

    //IMPORTANT: MUST BE IN COUNTER CLOCKWISE ORDER!! -> scheint irgendwie egal zu sein?
    private int[] elementArray = {
            1, 2, 0, //Top Right Triangle
            1, 0, 3 //Bottom left Triangle
    };

    private int vaoID, vboID, eboID;


    public LevelEditorScene() {
    }

    @Override
    public void init() {
        // Compile and Link the Shaders


        //Load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //pass shader sourcecode to GPU
        glShaderSource(vertexID, vertexShaderSource);
        glCompileShader(vertexID);

        //Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error default.glsl\n\tVertex Shader Compilation FAILED!");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }


        //Load and compile the vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //pass shader sourcecode to GPU
        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);

        //Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error default.glsl\n\tFragment Shader Compilation FAILED!");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        //Link Shaders & Check for Errors

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        //checking for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("Error default.glsl\n\tLinking Shaders FAILED!");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }


        // ----------------------------
        // GENERATE VAO, VBO and EBO buffer objects and send to GPU
        // ----------------------------

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //create float buffer or vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //Create VBO upload Vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //create indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();


        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //Add vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize+colorSize)*floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize*floatSizeBytes);
        glEnableVertexAttribArray(1);



    }

    @Override
    public void update(float dt) {
        //bind shader program
        glUseProgram(shaderProgram);
        //Bind VAO
        glBindVertexArray(vaoID);
        //enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);
    }


}
