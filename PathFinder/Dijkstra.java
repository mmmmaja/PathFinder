package PathFinder;

import Main.Node;
import graphics.Display;
import java.util.Comparator;
import java.util.PriorityQueue;


public class Dijkstra implements Runnable {

	private final PriorityQueue<Node> priorityQueue;
	private final Node[][]  matrix;
	private Node startNode;

	private final Display display;
	private boolean running = false;
	private Thread thread;


	public Dijkstra(Node[][] matrix, Display display) {
		this.matrix = matrix;
		this.display = display;

		// compare two nodes based on their distance form startNode
		this.priorityQueue = new PriorityQueue<>(
				Comparator.comparingInt(Node::getDistance)
		);

		findNodeMatrix();
		this.priorityQueue.add(this.startNode);
	}


	/**
	 * calls overridden run() method
	 */
	public synchronized void start() {
		running = true;
		this.thread = new Thread(this, "Dijkstra");
		this.thread.start();
	}


	@Override
	public void run() {

		double delta = 0;
		long lastTime = System.nanoTime();
		final double nanos = Math.pow(10, 9) / 400;
		// number of nanoseconds between each update: 400 times per second

		while (running) {

			long now = System.nanoTime();
			delta+= (now - lastTime) / nanos;
			lastTime = now;
			while (delta >= 1) {

				display.repaint();
				if (dijkstra()) {
					running = false;
					stop();
					break;
				}
				delta--;
			}
		}
		stop();
	}


	private boolean dijkstra() {
		Node currentNode = priorityQueue.poll();
		if (currentNode != null) {

			// targetNode was found
			// TODO add animation here
			if (this.matrix[currentNode.getRow()][currentNode.getColumn()].getID() == 3) {

				// once targetNode is found run the thread with animation displaying the path
				PathDisplay pathDisplay = new PathDisplay(currentNode.getPath(), this.matrix, this.display);
				pathDisplay.start();
				return true;
			}

			// mark current node as visited
			currentNode.visit();
			if (this.matrix[currentNode.getRow()][currentNode.getColumn()].getID() != 2) {
				this.matrix[currentNode.getRow()][currentNode.getColumn()].setID(4);
			}

			Node[] neighbourNodes = findNeighbourNodes(
					currentNode.getRow(),
					currentNode.getColumn()
			);


			for (Node node : neighbourNodes) {
				if (node != null) {
					if (!node.getVisited()) {
						if (currentNode.getDistance() + 1 < node.getDistance()) {
							node.setDistance(currentNode.getDistance() + 1);

							node.addNodeToPath(currentNode);
							for (Node n :  currentNode.getPath()) {
								node.addNodeToPath(n);
							}
							priorityQueue.add(node);
						}
					}
				}
			}
		}
		return false;
	}


	public synchronized void stop() {
		display.enableKeyPress();
		running = false;
		try {
			this.thread.join();
		}
		catch (InterruptedException ignored) {}
	}


	/**
	 * creates an array of neighbour nodes to the node at the position (row, column)
	 * new nodes have a reference to the ones from this.nodeMatrix
	 */
	private Node[] findNeighbourNodes(int row, int column) {
		Node[] neighbourNodes = new Node[4];
		addNode(row - 1, column, neighbourNodes, 0);
		addNode(row + 1, column, neighbourNodes, 1);
		addNode(row,column - 1, neighbourNodes,2);
		addNode(row,column + 1, neighbourNodes,3);
		return neighbourNodes;
	}


	/**
	 * try to add a node from this.nodeMatrix if it does not go out of bounds
	 */
	private void addNode(int row, int column, Node[] neighbourNodes, int index) {
		try {
			Node node = this.matrix[row][column];
			neighbourNodes[index] = node;
			if (neighbourNodes[index].getID() == 1) {
				neighbourNodes[index] = null;
			}
		}
		catch (ArrayIndexOutOfBoundsException ignored) {}
	}


	/**
	 * fill this.nodeMatrix with new nodes corresponding to box from this.Matrix
	 * wall boxes are represented with null
	 * moreover creates this.startNode
	 */
	private void findNodeMatrix() {

		for (Node[] node : this.matrix) {
			for (int j = 0; j < this.matrix[0].length; j++) {

				// 2 startNode
				if (node[j].getID() == 2) {
					node[j].visit();
					node[j].setDistance(0);
					this.startNode = node[j];
				}

			}
		}
	}

	private void printArray() {
		for (Node[] nodes : this.matrix) {
			for (int j = 0; j < this.matrix[0].length; j++) {
				System.out.print(nodes[j].getID());
			}
			System.out.println();
		}
	}


}
