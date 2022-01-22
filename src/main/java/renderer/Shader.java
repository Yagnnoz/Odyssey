package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private String vertexSource, fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6; //index of next word
            int eoL = source.indexOf("\r\n", index); //findet newline character nach "#type" und damit das Ende der Zeile
            String firstPattern = source.substring(index, eoL).trim(); // hier steht dann entweder vertex oder fragment, je nachdem was zuerst in der Datei steht

            index = source.indexOf("#type", eoL) + 6; //index verschieben zum zweiten Shader
            eoL = source.indexOf("\r\n", index); //s.o.
            String secondPattern = source.substring(index, eoL).trim(); //hier steht dann entweder vertex oder fragment, je nachdem was als zweiter Block in der Datei ist

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token: '" + firstPattern);
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token: '" + firstPattern);
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader at " + filepath;
        }

    }

    public void compileAndLink() {

        // ============================================================================
        // Compile and link shaders
        // ============================================================================

        int vertexID;
        int fragmentID;
        //Load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //pass shader sourcecode to GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        //Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error '" + filepath + "'\n\tVertex Shader Compilation FAILED!");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }


        //Load and compile the vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //pass shader sourcecode to GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        //Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error '" + filepath + "'\n\tFragment Shader Compilation FAILED!");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //checking for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error '" + filepath + "'\n\tLinking Shaders FAILED!");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }

        //Link Shaders & Check for Errors

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
    }

    public void use() {
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0);
    }
}
