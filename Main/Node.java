package Main;

import java.awt.*;
import java.util.ArrayList;


public class Node {

	// display preference values
	private int ID;
	private double opacity = 0.0;
	private double size = 0.0;

	// path finding algorithm values
	private final int row;
	private final int column;
	private boolean visited;
	private int distance;
	private final ArrayList<Node> path;


	private final Color[] colorDatabase = {
			new Color(255, 255, 255),	 // 0. empty field OK
			new Color(97, 123, 227),    // 1. wall OK
			new Color(85, 10, 70),   	// 2. startNode
			new Color(85, 10, 70),   	// 3. targetNode
			new Color(154, 206, 255),  // 4. visitedNode
			new Color(217, 66, 163),   // 5. targetNode & visitedNode connected
			new Color(194, 53, 154),   // 6. path digger
	};



	public Node(int row, int column) {
		this.ID = 0;
		this.row = row;
		this.column = column;
		this.visited = false;
		this.distance = Integer.MAX_VALUE;
		this.path = new ArrayList<>();
	}


	public void setID(int ID) {
		this.ID = ID;
	}


	public int getID() {
		return this.ID;
	}


	public Color getColor() {

		// visited nodes and path display: make animation
		if (this.ID == 4 || this.ID == 5) {
			this.opacity+= 0.05;
			return new Color(
					colorDatabase[ID].getRed(),
					colorDatabase[ID].getGreen(),
					colorDatabase[ID].getBlue(),
					Math.min((int)(this.opacity), 255)
			);
		}
		return this.colorDatabase[ID];
	}


	public int getSize() {
		// visited nodes and wall nodes: make animation
		if (this.ID == 4) {
			this.size+= 0.05;
			return Math.min((int)(this.size), Main.size);
		}
		else if (this.ID == 1) {
			this.size+= 0.2;
			return Math.min((int)(this.size), Main.size);
		}
		return Main.size;
	}


	public void visit() {
		this.visited = true;
	}


	public boolean getVisited() {
		return this.visited;
	}


	public void setDistance(int distance) {
		this.distance = distance;
	}


	public int getDistance() {
		return this.distance;
	}


	public int getRow() {
		return this.row;
	}


	public int getColumn() {
		return this.column;
	}


	public void addNodeToPath(Node node) {
		this.path.add(node);
	}


	public ArrayList<Node> getPath() {
		return this.path;
	}


	public String toString() {
		return "("+row+", "+column+") distance: "+distance;
	}

}
