package star_path;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

public class Star_Path {
	
	static double[][] cordinates;
	static int numberOfStars;
	static int numberofProbes;
	
	static List<Probe> probes = new ArrayList<>();
	static int[][] map;
	
	static int currentIndex;
	static Random rand = new Random();
	static double probabilities[];
	
    static int[] bestTourOrder;
    static double bestTourLength;
	
	public Star_Path(double[][] cordinatesInput) {
		cordinates = cordinatesInput;
		numberOfStars = cordinates.length;
		numberofProbes = (int) (numberOfStars * 0.8);
		map = new int[numberOfStars][numberOfStars];
		probabilities = new double[numberOfStars];
	}
		
	int[] findPath() {
		//set numberofProbes different ants
		for(int i = 0; i<numberofProbes; i++) {
			probes.add(new Probe(numberOfStars));
		}		
		int attempts = 100; //this is the number of different attempts from scratch
		int maxIterations = 100; //this is the max iteration per probe per attempt
		
		for(int i = 1; i <= attempts; i++) {
			setup(); //Set all ant to start at 0
			currentIndex = 0;
			resetMap(); //resetMap so that all trails have 1 preference each
			
			//start the iteration - i.e start all ants from 0 and run maxIterations time
			for(int j = 1; j <= maxIterations; j++) { 
				
				move(); //move ants
				update(); //update path
				getBest(); //update best path
			}
		}
        return bestTourOrder;
	}
		
	static void setup() {
		//System.out.println("Setup");
		for(int k = 0; k < numberofProbes; k++) {
			probes.get(k).clear();
			probes.get(k).visitStar(-1, 0);
		}
	}
	
	static void resetMap(){
		//System.out.println("Reset");
    	for(int i = 0; i< numberOfStars; i++) {
    		for(int j=0; j < numberOfStars; j++) {
    			map[i][j] = 1;
    		}
    	}
    }
	
	static void move() {
		//System.out.println("Move");
		for(int i = currentIndex; i < numberOfStars-1; i++) {
			probes.forEach(ant -> ant.visitStar(currentIndex, bestNext(ant)));
            currentIndex++;
		}
	}
	
	static int bestNext(Probe probe) {
		
		int possiblePath = rand.nextInt(numberOfStars - currentIndex);
		if (rand.nextDouble() < 0.5) {//here 0.5 is the random factor which means how random should we be
            OptionalInt star = IntStream.range(0, numberOfStars)
                .filter(i -> i == possiblePath && !probe.visited(i))
                .findFirst();
            if (star.isPresent()) {
                return star.getAsInt();
            }
        }
		probability(probe);
        double total = 0;
        for (int i = 0; i < numberOfStars; i++) {
            total += probabilities[i];
            //total = total + 0.8;
        	if (total >= rand.nextDouble()) {
                return i;
        	}
        }
        throw new RuntimeException();
	}
	
	static void probability(Probe probe) {		
        double density = 0.0;
        for (int l = 0; l < numberOfStars; l++) {
            if (!probe.visited(l)) {
            	//System.out.println(i + " " + l + " "+ map.length + " " + map[0].length + " "
            	//		+ " "+ cordinates.length + " " + cordinates[0].length + " ");
                density += Math.pow(map[probe.path[currentIndex]][l], 1) * Math.pow(1.0 / cordinates[probe.path[currentIndex]][l], 5);
            }
        }
        for (int j = 0; j < numberOfStars; j++) {
            if (probe.visited(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(map[probe.path[currentIndex]][j], 1) * Math.pow(1.0 / cordinates[probe.path[currentIndex]][j], 5);
                probabilities[j] = numerator / density;
            }
        }
	}
	
	static void update() {
		
		//evaporate trails by a factor of 0.5
		for (int i = 0; i < numberOfStars; i++) {
            for (int j = 0; j < numberOfStars; j++) {
                map[i][j] *= 0.5;
            }
        }
		
		//update trails to reflect the current density
		for (Probe p : probes) {
            double density = 500 / p.pathSize(cordinates); //Here 500 represents what the amount dropped should be - density
            for (int i = 0; i < numberOfStars - 1; i++) {
                map[p.path[i]][p.path[i + 1]] += density;
            }
            map[p.path[numberOfStars - 1]][p.path[0]] += density;
        }
	}
	
	static void getBest() {
		//init case
    	if (bestTourOrder == null) {
            bestTourOrder = probes.get(0).path;
            bestTourLength = probes.get(0).pathSize(cordinates);
        }
    	//whichever ant took the shortest route
        for (Probe p : probes) {
            if (p.pathSize(cordinates) < bestTourLength) {
                bestTourLength = p.pathSize(cordinates);
                bestTourOrder = p.path.clone();
            }
        }
	}
}
