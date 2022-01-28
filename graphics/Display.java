package graphics;

import Main.Main;
import PathFinder.RecursiveMaze;
import Main.Node;
import Main.Painter;
import Main.Mouse;
import PathFinder.Dijkstra;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;


/**
 * class responsible for UI, holds the frame
 * connected to Mouse object
 * listens to clicks
 */
public class Display extends JPanel implements KeyListener {


	private final Node[][] matrix;
	private final Random random = new Random();
	private final Color lines = new Color(154, 206, 255);
	private double probability = 0.0;
	private boolean keyPressed = false;


	public Display(Node[][] matrix) {
		this.matrix = matrix;

		Dimension dimension = new Dimension(
				Main.HEIGHT * Main.size + 15,
				Main.WIDTH * Main.size + 38
		);
		JFrame window = new JFrame("path visualizer");
		window.setPreferredSize(new Dimension(dimension));
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.add(this);
		window.pack();
		window.setVisible(true);
		window.addKeyListener(this);

		Mouse mouse = new Mouse();
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
	}


	/**
	 * This function is called BY THE SYSTEM if required for a new frame,
	 * uses the matrix stored by the Painter class.
	 */
	public void paintComponent(Graphics g) {

		Graphics2D localGraphics2D = (Graphics2D) g;

		// fill the blocks with assigned colors from Colors interface
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				int size =this. matrix[i][j].getSize();
				Rectangle rectangle = new Rectangle(
						(Main.size - size) + i * Main.size,
						(Main.size - size) + j * Main.size,
						size,
						size
				);

				localGraphics2D.setColor(this.matrix[i][j].getColor());
				localGraphics2D.fill(rectangle);
			}
		}

		localGraphics2D.setColor(lines);
		// draw vertical lines
		for (int i = 0; i <= Main.HEIGHT; i++) {
			localGraphics2D.drawLine(
					i * Main.size, 0,
					i * Main.size, matrix[0].length * Main.size
			);
		}
		// draw horizontal lines
		for (int i = 0; i <= Main.WIDTH; i++) {
			localGraphics2D.drawLine(
					0, i * Main.size,
					matrix.length * Main.size, i * Main.size
			);
		}
	}



	/**
	 * Key press control
	 * possibilities
	 * 				ENTER
	 * 				keyUP
	 * 				keyDOWN
	 * 				m
	 */
	@Override
	public void keyPressed(KeyEvent e) {

		// ENTER was pressed
		if (e.getKeyCode() == 10 && !keyPressed) {
			// kill the thread in Painter class
			Painter.running = false;
			keyPressed = true;

			// run new Dijkstra algorithm: display the path between nodes
			Dijkstra dijkstra = new Dijkstra(this.matrix, this);
			dijkstra.start();
		}

		// keyUp was pressed
		else if (e.getKeyCode() == 38 && !keyPressed) {
			// generate random walls with incremented probability
			this.probability+= 0.05;
			randomWalls();
		}

		// keyDown was pressed
		else if (e.getKeyCode() == 40 && !keyPressed) {
			// delete all the walls, set the probability back to 0
			this.probability = 0.0;
			clearMatrix();
		}

		// m key was pressed
		else if (e.getKeyCode() == 77 && !keyPressed) {
			// generate random maze, starting new Thread
			this.keyPressed = true;
			RecursiveMaze recursiveMaze = new  RecursiveMaze(this.matrix, this);
			recursiveMaze.start();
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}


	/**
	 * generate random walls depending on global variable this.probability
	 * u can control this values with keyUP and keyDOWN
	 */
	private void randomWalls() {
		for (Node[] nodes : this.matrix) {
			for (Node node : nodes) {
				if (node.getID() != 2 && node.getID() != 3) {
					double r = random.nextDouble();
					if (r <= this.probability) {
						node.setID(1);
					}
					else {
						node.setID(0);
					}
				}
			}
		}
		repaint();
	}


	/**
	 * set the state of the matrix back to beginning
	 * crate new unvisited nodes
	 */
	private void clearMatrix() {
		for (Node[] nodes : this.matrix) {
			for (int i = 0; i < nodes.length; i++) {

				Node newNode = new Node(nodes[i].getRow(), nodes[i].getColumn());

				if (nodes[i].getID() == 2) {
					newNode.setID(2);
				}
				else if (nodes[i].getID() == 3) {
					newNode.setID(3);
				}
				nodes[i] = newNode;
			}
		}
		repaint();
	}



	/**
	 * enable key pressing
	 * once all the processes triggered by the previous click are finished
	 */
	public void enableKeyPress() {
		this.keyPressed = false;
	}

}
