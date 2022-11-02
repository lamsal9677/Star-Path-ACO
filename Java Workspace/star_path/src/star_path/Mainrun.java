package star_path;

public class Mainrun{
	public static void main(String[] args) {
		
		double[][] cordinates = {{0,200,200,300}, {200,0,200,400}, {200,200,0,200}, {300,400,200,0}};
		Star_Path sp = new Star_Path(cordinates);
		sp.findPath();
		
		AntColonyOptimization ao = new AntColonyOptimization(cordinates);
		//ao.startAntOptimization();
	}
}
