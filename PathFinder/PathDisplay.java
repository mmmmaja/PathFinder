package PathFinder;

import Main.Node;
import graphics.Display;
import java.util.ArrayList;


/**
 * shows found path from startNode to targetNode
 */
public class PathDisplay implements Runnable {

	private final ArrayList<Node> path;
	private final Node[][] matrix;
	private final Display display;
	private int index = 0;
	private boolean running = false;
	private Thread thread;


	public PathDisplay(ArrayList<Node> path, Node[][] matrix, Display display) {
		this.path = path;
		this.matrix = matrix;
		this.display = display;
	}


	/**
	 * calls overridden run() method
	 */
	public synchronized void start() {
		running = true;
		this.thread = new Thread(this, "Path Display");
		this.thread.start();
	}


	@Override
	public void run() {
		double delta = 0;
		long lastTime = System.nanoTime();
		final double nanos = Math.pow(10, 9) / 20;
		// number of nanoseconds between each update: 20 times per second

		while (running && index < this.path.size()) {

			long now = System.nanoTime();
			delta+= (now - lastTime) / nanos;
			lastTime = now;
			while (delta >= 1) {

				Node node = this.path.get(index);
				index++;
				this.matrix[node.getRow()][node.getColumn()].setID(5);
				display.repaint();
				delta--;
			}
		}
		stop();
	}


	public synchronized void stop() {
		running = false;
		this.path.get(path.size() - 1).setID(2);
		try {
			this.thread.join();
		}
		catch (InterruptedException ignored) {}
	}
}
