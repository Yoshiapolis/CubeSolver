package com.github.yoshiapolis.renderEngine.shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.github.yoshiapolis.math.Matrix3D;
import com.github.yoshiapolis.math.Vector3f;
import com.github.yoshiapolis.renderEngine.loaders.ResourceLoader;

public abstract class Shader {
	
	private static final String SHADER_PATH = "shaders/";
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	public Shader(String vertexShader, String fragShader) {
		vertexShaderID = loadShader(vertexShader, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragShader, GL20.GL_FRAGMENT_SHADER);
		
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected abstract void bindAttributes();
	
	protected abstract void getAllUniformLocations();
	
	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	protected void loadVector3f(int location, Vector3f value) {
		GL20.glUniform3f(location, value.x, value.y, value.z);
	}
	
	protected void loadMatrix(int location, Matrix3D matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}
	
	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	protected void bindAttribute(int attribute, String name) {
		GL20.glBindAttribLocation(programID, attribute, name);
	}
	
	public void start() {
		GL20.glUseProgram(programID);
	}
	
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	public void cleanUp() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteProgram(programID);
		GL20.glDeleteProgram(programID);
	}
	
	private static int loadShader(String file, int type) {
		int shaderID = GL20.glCreateShader(type);
		
		String shaderSource = readTextFile(SHADER_PATH + file);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader " + "\"" + file + "\".");
			System.exit(-1);
		}
		
		return shaderID;
	}
	
	private static String readTextFile(String file) {
		StringBuilder content = new StringBuilder();		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceStream(file)));
			
			String line;
			while((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			
			reader.close();
		} catch(IOException e) {
			System.err.println("Could not read shader file!");
			e.printStackTrace();
			System.exit(-1);
		}
		
		return content.toString();
	}
	
}