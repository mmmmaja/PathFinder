package PathFinder;

import Main.Node;
import graphics.Display;
import java.util.ArrayList;
import java.util.Random;

/**
 * create random maze using backtracking algorithm
 */
public class RecursiveMaze implements Runnable {

	private final Node[][] matrix;
	private final Display display;
	private final Random random = new Random();

	private boolean running = false;
	private Thread thread;

	private Node currentNode;
	private final ArrayList<Node> nodePath = new ArrayList<>();


	public RecursiveMaze(Node[][] matrix, Display display) {
		this.matrix = matrix;
		this.display = display;
		this.currentNode = this.matrix[1][1];
		this.nodePath.add(currentNode);
		fillMaze();
	}


	/**
	 * calls overridden run() method
	 */
	public synchronized void start() {
		running = true;
		this.thread = new Thread(this, "Recursive maze");
		this.thread.start();
	}


	@Override
	public void run() {
		double delta = 0;
		long lastTime = System.nanoTime();
		final double nanos = Math.pow(10, 9) / 80;
		// update 80 times per second

		while (running) {

			long now = System.nanoTime();
			delta+= (now - lastTime) / nanos;
			lastTime = now;
			while (delta >= 1) {

				display.repaint();
				maze();
				this.nodePath.add(this.currentNode);
				this.currentNode.setID(6);
				delta--;
			}
		}
		stop();
	}

	/**
	 * in each call moves two steps in random direction
	 * digs the way through the walls
	 */
	private void maze() {

		ArrayList<Node> neighbourNodes = findNeighbourNodes(currentNode.getRow(), currentNode.getColumn());

		// if there is possibility to move find new random direction
		if (neighbourNodes.size() != 0) {
			int direction = random.nextInt(neighbourNodes.size());
			connectNodes(neighbourNodes.get(direction));
			this.currentNode = neighbourNodes.get(direction);

		}
		// no moves left at the current node
		// backtrack to the previous node
		else {
			nodePath.remove(this.currentNode);
			try {
				Node newNode = nodePath.get(nodePath.size() - 1);
				connectNodes(newNode);
				this.currentNode = newNode;
				nodePath.remove(this.currentNode);
			}
			// all the moves vere considered
			catch (IndexOutOfBoundsException e) {
				stop();
			}
		}
	}


	/**
	 * create a way from this.currentNode and node
	 * based on the UNIT value
	 */
	private void connectNodes(Node node) {
		this.currentNode.setID(0);
		int row = this.currentNode.getRow();
		int column = this.currentNode.getColumn();

		while (row != node.getRow() || column != node.getColumn()) {
			int rowDifference = node.getRow() - row;
			int columnDifference = node.getColumn() - column;

			if (rowDifference < 0) {
				row--;
			}
			else if (rowDifference > 0) {
				row++;
			}
			else if (columnDifference < 0) {
				column--;
			}
			else if (columnDifference > 0) {
				column++;
			}
			this.matrix[row][column].setID(0);
		}
	}


	/**
	 * creates an array of neighbour nodes to the node at the position (row, column)
	 * new nodes have a reference to the ones from this.nodeMatrix
	 */
	private ArrayList<Node> findNeighbourNodes(int row, int column) {
		ArrayList<Node> neighbourNodes = new ArrayList<>();
		int UNIT = 2;
		addNode(row - UNIT, column, neighbourNodes);
		addNode(row + UNIT, column, neighbourNodes);
		addNode(row,column - UNIT, neighbourNodes);
		addNode(row,column + UNIT, neighbourNodes);
		return neighbourNodes;
	}


	/**
	 * try to add a node from this.nodeMatrix if it does not go out of bounds
	 */
	private void addNode(int row, int column, ArrayList<Node> neighbourNodes) {
		try {
			Node node = this.matrix[row][column];
			if (node.getID() == 1) {
				neighbourNodes.add(node);
			}
		}
		catch (ArrayIndexOutOfBoundsException ignored) {}
	}


	/**
	 * stop this thread and enable new processes
	 */
	public synchronized void stop() {
		display.enableKeyPress();
		running = false;
		try {
			this.thread.join();
		}
		catch (InterruptedException ignored) {}
	}



	/**
	 * fill the maze completely with walls,
	 * then the way through the walls will be dug
	 */
	private void fillMaze() {
		for (Node[] nodes : matrix) {
			for (Node node : nodes) {
				if (node.getID() == 0) {
					node.setID(1);
				}
			}
		}
		display.repaint();
	}
}
