package Main;

import graphics.Display;


/**
 * class responsible for drawing walls and setting nodes on the screen
 */
public class Painter implements Runnable {

	private Node[][] matrix;
	private final Display display;

	private int[] startNode  = {20, 20};
	private int[] targetNode = {70, 20};
	private boolean targetNodeDragged = false;
	private boolean startNodeDragged  = false;

	public static boolean running = false;
	private Thread thread;


	public Painter() {

		createNodeMatrix();

		this.display = new Display(matrix);
		this.display.repaint();
	}


	/**
	 * create new matrix filled with Node objects
	 * in whole project algorithm will be working on the SAME object!
	 * no copies
	 */
	private void createNodeMatrix() {
		this.matrix = new Node[Main.HEIGHT][Main.WIDTH];

		for (int i = 0; i < Main.HEIGHT; i++) {
			for (int j = 0; j < Main.WIDTH; j++) {

				Node node = new Node(i, j);
				if ((i == this.startNode[0]) && (j == this.startNode[1])) {
					node.setID(2);
				}
				else if ((i == this.targetNode[0]) && (j == this.targetNode[1])) {
					node.setID(3);
				}
				this.matrix[i][j] = node;
			}
		}
	}



	/**
	 * calls overridden run() method
	 */
	public synchronized void start() {
		running = true;
		this.thread = new Thread(this, "Painter");
		this.thread.start();
	}


	@Override
	public void run() {
		double delta = 0;
		long lastTime = System.nanoTime();
		final double nanos = Math.pow(10, 9) / 800;
		// number of nanoseconds between each update: 800 times per second

		while (running) {

			long now = System.nanoTime();
			delta+= (now - lastTime) / nanos;
			lastTime = now;
			while (delta >= 1) {
				paint();
				display.repaint();
				delta--;
			}
		}
		stop();
	}



	private void paint() {
		// current position of the cursor on the frame
		int x = Mouse.getMouseX() / Main.size;
		int y = Mouse.getMouseY() / Main.size;


		// RIGHT button was pressed
		// outOfBounds check
		if (Mouse.getMouseButton() == 1 && x >= 0 && y >= 0 && x < Main.HEIGHT - 1 && y < Main.WIDTH) {

			// startNode movement
			if (this.matrix[x][y].getID() == 2) {
				startNodeDragged = true;
			}

			else if (startNodeDragged) {
				if (this.matrix[x][y].getID() != 1) {
					this.matrix[x][y].setID(2);
					this.matrix[this.startNode[0]][this.startNode[1]].setID(0);
					this.startNode = new int[] {x, y};
				}
			}

			// targetNode movement
			else if (this.matrix[x][y].getID() == 3) {
				targetNodeDragged = true;
			}
			else if (targetNodeDragged) {
				if (this.matrix[x][y].getID() != 1 && this.matrix[x][y].getID() != 2) {
					this.matrix[x][y].setID(3);
					this.matrix[this.targetNode[0]][this.targetNode[1]].setID(0);
					this.targetNode = new int[] {x, y};
				}
			}

			// draw a wall
			else {
				this.matrix[x][y].setID(1);
			}
		}

		// RIGHT button released, stop node movement, stop wall builder
		if (Mouse.getMouseButton() == -1) {
			startNodeDragged  = false;
			targetNodeDragged = false;
		}
	}


	public synchronized void stop() {
		running = false;
		try {
			this.thread.join();
		}
		catch (InterruptedException ignored) {}
	}
}
