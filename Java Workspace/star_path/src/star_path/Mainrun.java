package star_path;

import java.util.Arrays;
import java.util.Scanner;  // Import the Scanner class
import java.util.stream.Stream;
import java.io.File;
import java.io.FileNotFoundException;

import java.time.Duration;
import java.time.Instant;

public class MainRun {

public static void main(String[] args) {
		
		double cordinate[][];
	
		double[][] cordinateMatrix;
		
		Scanner readUserInput = new Scanner(System.in);  // Create a Scanner object
		
		System.out.print("Enter Number of Stars to Read: ");
		int stars = readUserInput.nextInt();  // Read user input
		
		System.out.print("Enter Max Distance: ");
		int maxDistance = readUserInput.nextInt();  // Read user input
		
		readUserInput.close();
		
		Instant start = Instant.now();
		cordinate = new double[stars][3];
		cordinateMatrix = new double[stars][stars];
		
		double x = 0.00;
		double y = 0.00;
		double z = 0.00;
		int arrIndex = 0;
		double arr[];
		try {
		      File myObj = new File("data.csv");
		      Scanner myReader = new Scanner(myObj);
		      myReader.nextLine();//disregarding the first line
		      while (arrIndex < stars && myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        //System.out.println(data);
		        
		        //send the data to an array
		        arr = Stream.of(data.split(","))
	                     .mapToDouble (Double::parseDouble)
	                     .toArray();
		        x = arr[0];
		        y = arr[1];
		        z = arr[2];
		        //System.out.println(arrIndex);
		        cordinate[arrIndex][0] = x;
		        cordinate[arrIndex][0] = y;
		        cordinate[arrIndex][0] = z;
		        
		        //System.out.println(x +","+ y+ ","+z);
		        arrIndex++;
		      }
		      myReader.close();
		    } 
		catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
				
		double dist = 0;
		int size = cordinate.length;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				dist = (getDistance3D(cordinate[i][0],cordinate[i][1],cordinate[i][2],cordinate[j][0],cordinate[j][1],cordinate[j][2]));
				cordinateMatrix[i][j] = dist;
			}
		}
		System.out.println("Matrix is setup");
		int currentDistance = 0;
		Star_Path sp = new Star_Path(cordinateMatrix);
		int[] bestTour = sp.findPath();
		System.out.println("Best tour order: " + Arrays.toString(bestTour)+'\n');
		System.out.println("Possible Path: ");
		for(int i=0; i<bestTour.length-1; i++) {
			currentDistance+=cordinateMatrix[i][i+1];
			if(currentDistance <= maxDistance) {
				System.out.println(bestTour[i]+" to "+bestTour[i+1]);
			}
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");
	}

	private static double getDistance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt((z2 - z1) * (z2 - z1) + (y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

}
