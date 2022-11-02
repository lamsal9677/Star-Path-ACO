package star_path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

public class Star_Path {
	
	static double[][] cordinates;
	static int numberOfCities;
	static int numberOfAnts;
	
	static List<Ant> ants = new ArrayList<>();
	static int[][] map;
	
	static int currentIndex;
	static Random rand = new Random();
	static double probabilities[];
	
    static int[] bestTourOrder;
    static double bestTourLength;
	
	public Star_Path(double[][] cordinatesInput) {
		cordinates = cordinatesInput;
		numberOfCities = cordinates.length;
		numberOfAnts = (int) (numberOfCities * 0.8);
		map = new int[numberOfCities][numberOfCities];
		probabilities = new double[numberOfCities];
	}
		

	void findPath() {
		
		//set numberOfAnts different ants
		for(int i = 0; i<numberOfAnts; i++) {
			ants.add(new Ant(numberOfCities));
		}
		
		
		
		//List<Ant> ants = new ArrayList<>();
		
		
		int attempts = 100; //this is the number of different attempts from scratch
		int maxIterations = 100; //this is the max iteration per ant per attempt
		
		
		for(int i = 1; i <= attempts; i++) {
			
			setup(); //Set all ant to start at 0
			currentIndex = 0;
			resetMap(); //resetMap so that all trails have 1 preference each
			
			
			//start the iteration - i.e start all ants from 0 and run maxIterations time
			for(int j = 1; j <= maxIterations; j++) { 
				
				//move ants to desired path
				move();
				//System.out.println("Ant is moved");
				update();
				//System.out.println("Trail is updated");
				getBest();
				//System.out.println("Best is updated");
				//update the trail
				
				
				//update the best trail //return the best trail at the end
			}
		}
		System.out.println("Best tour length: " + (bestTourLength - numberOfCities));
        System.out.println("Best tour order: " + Arrays.toString(bestTourOrder));
        
        double currentDistance = 0;
        double maxDistance = 100;

        for(int i=0; i<bestTourOrder.length-1; i++) {
        	//if(i+1 < bestTourOrder.length) {
    			currentDistance += cordinates[i][i+1];
        		if (currentDistance <= maxDistance) {
            		//System.out.println("Trail "+ bestTourOrder[i]+ " to "+ bestTourOrder[i+1]);
            		System.out.print(bestTourOrder[i]);
        		}
        }
        if (currentDistance <= maxDistance)
    		System.out.print(bestTourOrder[bestTourOrder.length-1]); // print last element if there is still place
	
	
	}
	
	static double[][] generateRandomMatrix(int n) {
        double[][] randomMatrix = new double[n][n];
        IntStream.range(0, n)
            .forEach(i -> IntStream.range(0, n)
                .forEach(j -> randomMatrix[i][j] = Math.abs(rand.nextInt(100) + 1)));
        return randomMatrix;
    }
	
	
	
	static void setup() {
		System.out.println("Setup");
		for(int k = 0; k < numberOfAnts; k++) {
			ants.get(k).clear();
			ants.get(k).visitCity(-1, 0);
		}
	}
	
	static void resetMap(){
		System.out.println("Reset");
    	for(int i = 0; i< numberOfCities; i++) {
    		for(int j=0; j < numberOfCities; j++) {
    			map[i][j] = 1;
    		}
    	}
    }
	
	static void move() {
		System.out.println("Move");
		for(int i = currentIndex; i < numberOfCities-1; i++) {/////////////
			ants.forEach(ant -> ant.visitCity(currentIndex, bestNext(ant)));
            currentIndex++;
		}
	}
	
	static int bestNext(Ant ant) {
		
		int possiblePath = rand.nextInt(numberOfCities - currentIndex);
		if (rand.nextDouble() < 0.5) {//here 0.5 is the random factor which means how random should we be
            OptionalInt cityIndex = IntStream.range(0, numberOfCities)
                .filter(i -> i == possiblePath && !ant.visited(i))
                .findFirst();
            if (cityIndex.isPresent()) {
                return cityIndex.getAsInt();
            }
        }
		probability(ant);
        double r = rand.nextDouble();
        double total = 0;
        for (int i = 0; i < numberOfCities; i++) {
            total += probabilities[i];
            //total = total + 0.8;
        	if (total >= r) {
                return i;
            }
        }

        throw new RuntimeException("There are no other cities");
	}
	
	static void probability(Ant ant) {
		int alpha = 1;
		int beta = 5;
		
		int i = ant.trail[currentIndex];
        double pheromone = 0.0;
        for (int l = 0; l < numberOfCities; l++) {
            if (!ant.visited(l)) {
            	System.out.println(i + " " + l + " "+ map.length + " " + map[0].length + " "
            			+ " "+ cordinates.length + " " + cordinates[0].length + " ");
                pheromone += Math.pow(map[i][l], alpha) * Math.pow(1.0 / cordinates[i][l], beta);
            }
        }
        for (int j = 0; j < numberOfCities; j++) {
            if (ant.visited(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(map[i][j], alpha) * Math.pow(1.0 / cordinates[i][j], beta);
                probabilities[j] = numerator / pheromone;
            }
        }
	}
	
	static void update() {
		
		//evaporate trails by a factor of 0.5
		for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                map[i][j] *= 0.5;
            }
        }
		
		//update trails to reflect the current density
		for (Ant a : ants) {
            double contribution = 500 / a.trailLength(cordinates); //Here 500 represents what the amount dropped should be
            for (int i = 0; i < numberOfCities - 1; i++) {
                map[a.trail[i]][a.trail[i + 1]] += contribution;
            }
            map[a.trail[numberOfCities - 1]][a.trail[0]] += contribution;
        }
		
	}
	
	static void getBest() {
		//init case
    	if (bestTourOrder == null) {
            bestTourOrder = ants.get(0).trail;
            bestTourLength = ants.get(0).trailLength(cordinates);
        }
    	//whichever ant took the shortest route
        for (Ant a : ants) {
            if (a.trailLength(cordinates) < bestTourLength) {
                bestTourLength = a.trailLength(cordinates);
                bestTourOrder = a.trail.clone();
            }
        }
	}
}