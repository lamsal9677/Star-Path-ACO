package star_path;

public class Probe {
	protected int distancePoints;
	protected int path[];
	protected boolean visited[];

	public Probe(int tourSize) {
		this.distancePoints = tourSize;
		this.path = new int[tourSize];
		this.visited = new boolean[tourSize];
	}
	
	void clear() {
		for (int i = 0; i < distancePoints; i++)
			visited[i] = false;
	}
	
	void visitStar(int currentIndex, int star) {
		path[currentIndex + 1] = star;
		visited[star] = true;
	}

	boolean visited(int i) {
		return visited[i];
	}

	double pathSize(double map[][]) {
		double length = map[path[distancePoints - 1]][path[0]];
		for (int i = 0; i < distancePoints - 1; i++) {
			length += map[path[i]][path[i + 1]];
		}
		return length;
	}
}