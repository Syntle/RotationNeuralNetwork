package game;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class Game
{
	private long window;

	private double width = 500;
	private double height = 1000;
	
	private Player player = new Player();

	public static void main(String[] args)
	{
		new Game().run();
	}

	public void run()
	{
		try
		{
			init();
			loop();

			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		}
		finally
		{
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
	}

	private void init()
	{
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		window = glfwCreateWindow((int) width, (int) height, "Rotation", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
		{
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true);
		});
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.width() - (int) width) / 2, (vidmode.height() - (int) height) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
	}

	private double rotation = 0.0;
	private void loop()
	{
		GL.createCapabilities();
		glClearColor(0, 0, 0, 0);
		glViewport(0, 0, (int) width, (int) height);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, 0, height, -1, 1);
		glMatrixMode(GL_MODELVIEW);

		while (!glfwWindowShouldClose(window))
		{
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			glBegin(GL_QUADS);
			glColor3d(179.0/255, 157.0/255, 219.0/255);
			glVertex2d(0, 0);
			glVertex2d(width, 0);
			glColor3d(38.0/255, 198.0/255, 218.0/255);
			glVertex2d(width, height);
			glVertex2d(0, height);
			glEnd();
			
			double size = player.getSize();
			double halfSize = size * 0.5;
			double x = player.getX();
			double y = player.getY();
			
			// Shadow
			glTranslated(6, -6, 0);
			glRotated(rotation, 0, 0, 0);
			glBegin(GL_QUADS);
			glColor4d(0, 0, 0, 0.5);
			glVertex2d(x - halfSize, y - halfSize);
			glVertex2d(x + halfSize, y - halfSize);
			glVertex2d(x + halfSize, y + halfSize);
			glVertex2d(x - halfSize, y + halfSize);
			glEnd();
			glTranslated(-6, 6, 0);
			
			// Player
			glBegin(GL_QUADS);
			glColor4d(216.0/255, 27.0/255, 96.0/255, 1.0);
			glVertex2d(x - halfSize, y - halfSize);
			glVertex2d(x + halfSize, y - halfSize);
			glVertex2d(x + halfSize, y + halfSize);
			glVertex2d(x - halfSize, y + halfSize);
			glEnd();
			glLoadIdentity();
			
			glfwSwapBuffers(window);
			glfwPollEvents();
			
			rotation += 1;
		}
	}

}
