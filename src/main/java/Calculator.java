import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//cancella

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.SystemOutLogger;
import org.apache.xmlbeans.impl.jam.internal.elements.ArrayClassImpl;

public class Calculator {

	/*
	 * input factory to work and list of zones to allocate
	 */
	private static Factory initial;
	private static Zone[][] newFactoryStructure;
	ArrayList<Integer> combinations = new ArrayList<Integer>();

	/*
	 * The initial factory is created with the input data. The factory is analyzed
	 * and a list of the empty zones is created.
	 */
	public Calculator() throws InvalidFormatException, IOException {
		// qui ho levato la initialisieurung di factoy structure, perche viene gia fatta
		// nella main.
		// inoltre la methode createfactorystructure non esite piú!

	}

	public Factory performAlgorithm() throws Exception {

		System.out.println("ciao padrone");

		ArrayList<Zone> zonesToBeAllocated = initial.getZonesToAllocate();

		// in case the list of zonesToBeAllocated is empty
		if (zonesToBeAllocated.isEmpty()) {
			return initial;
		}

		/*
		 * What should happen in this method: Thinking of our algorithm, we have an
		 * initial list with all the zonesToBeAllocated. We want than individually each
		 * of these zones get into the algorithm (calculate)) once. When each of them
		 * has hone thorugh once, we check the information of each of these zones,
		 * comparing the cost and choose the cheapest one. Only now the structure in
		 * factory.getFactoryStructure is really modified. This implies that when
		 * entering in calculate the structure needs to be copied.
		 */

		while (!zonesToBeAllocated.isEmpty()) {
			zonesToBeAllocated = initial.getZonesToAllocate(); // reupdate
			System.out.println("------------------------------");
			System.out.println("zonesToBeAllocated.size: " + zonesToBeAllocated.size());

			for (int i = 0; i < zonesToBeAllocated.size(); i++) {
				System.out.println("zonesToBeAllocated nr: " + i);
				zonesToBeAllocated.get(i).information = calculate(zonesToBeAllocated.get(i), initial);
			}

			// choose the cheapest for this iteration
			int i = 0, j = 0;
			Information informationOfBestZone = null;
			boolean found = false;
			while (!found) {
				if (zonesToBeAllocated.get(i).information.applicable) {
					found = true;
					informationOfBestZone = zonesToBeAllocated.get(i).information;
				} else {
					i++;
					j = i;
				}
			}
			for (i++; i < zonesToBeAllocated.size(); i++) {
				if (zonesToBeAllocated.get(i).information.applicable) {
					if (zonesToBeAllocated.get(i).information.costs < informationOfBestZone.costs) {
						informationOfBestZone = zonesToBeAllocated.get(i).information;
						j = i;
					}
				}
			}

			// apply the modifications to real factoryStructure. take zone out of
			// zonesToBeAllocated
			initial = zonesToBeAllocated.get(j).information.modifiedStructure;
		}

		return initial;
	}

	public Information calculate(Zone zone, Factory factoryAsParameter) throws Exception {
		Information information; // information (boolean applicable, Zone[][] modifiedStructure, double cost)
		Factory factory = copyFactory(factoryAsParameter);
		// FIRST HIERARCHY BASED ON PERFECT FIT

		// level 0: Edge-case: checkForLargerZone
		if (zone.equals(initial.getZonesToAllocate().get(0))) {
			information = checkForLargerZone(zone, factory);
			if (information.applicable) {
				System.out.println("solution for " + zone.name + " found in level 0");
				return information;
			}
		}

		// level 1: fitPerfectlyWithList
//		information = fitPerfectlyWithList(zone, factory);
//		if (information.applicable) return information;

		// level 2: fitPerfectly
		information = fitPerfectly(zone, factory);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 2");
			return information;
		}

		// level 3: fitMoving1NeighbourWithList
//		information = fitMovingNeighbourWithList(); 
//		if (information.applicable) return information;

		// level 4: fitMoving1Neighbour
		information = fitMovingNeighbour(zone, factory, 1);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 4");
			return information;
		}

		// level 5: fitMoving2NeighbourWithList
//		information = fitMoving2NeighboursWithList(); 
//		if (information.applicable) return information;

		// level 6: fitMoving2Neighbour
		information = fitMovingNeighbour(zone, factory, 2);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 6");
			return information;
		}

		// level 7: fitMoving3NeighboursWithList
//		fitMoving3NeighboursWithList();
//		if (information.applicable) return information;

		// level 8: fitMoving3Neighbours
		information = fitMovingNeighbour(zone, factory, 3);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 8");
			return information;
		}

		// level 9: fitMoving4NeighbourWithList
//		fitMoving4NeighboursWithList(); 
//		if (information.applicable) return information;

		// level 10: fitMoving4Neighbour
		information = fitMovingNeighbour(zone, factory, 4);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 10");
			return information;
		}

		// level 11: fitMoving5NeighboursWithList
//		fitMoving5NeighboursWithList(); 
//		if (information.applicable) return information;

		// level 12: fitMoving5Neighbours
		information = fitMovingNeighbour(zone, factory, 5);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 12");
			return information;
		}

		// level 13: fitMoving6NeighboursWithList
//		fitMoving6NeighboursWithList(); 
//		if (information.applicable) return information;

		// level 14: fitMoving6Neighbours
		information = fitMovingNeighbour(zone, factory, 6);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 14");
			return information;
		}

		// SECOND HIERARCHY BASED ON FINDING A BIGGER ZONE. "WITH REST".

		// level 1: fitWithRestWithList()
//		information = fitWithRestWithList(); 
//		if (information.applicable)
//		return information;

		// level 2: fitWithRest()
		information = fitWithRest(zone, factory);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 2 (second hierarchy) ");
			return information;
		}

		// level 2: fitMoving1NeighbourWithRestWithList()
//		information = fitMoving1NeighbourWithRestWithList();
//		if (information.applicable)
//		return information;

		// level 3: fitMoving1NeighbourWithRest()
		information = fitMovingNeighbourWithRest(zone, factory, 1);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level 3 (second hierarchy) ");
			return information;
		}

		// level 4: fitMoving2NeighbourWithRestWithList()
//		information = fitMoving2NeighbourWithRestWithList();
//		if (information.applicable)
//		return information;

		// level 5: fitMoving2eighbourWithRest()
//		information = fitMoving2eighbourWithRest(); 
//		if (information.applicable)
//		return information;

		// level 6: fitMoving3NeighbourWithRestWithList()
//		information = fitMoving3NeighbourWithRestWithList();
//		if (information.applicable)
//		return information;

		// level 7: fitMoving3NeighbourWithRest()
//		information = fitMoving3NeighbourWithRest(); 
//		if (information.applicable)
//		return information;

		// level 8: fitMoving4NeighbourWithRestWithList()
//		information = fitMoving4NeighbourWithRestWithList();
//		if (information.applicable)
//		return information;

		// level 9: fitMoving4NeighbourWithRest()
//		information = fitMoving4NeighbourWithRest(); 
//		if (information.applicable)
//		return information;

		// level 10: fitMoving5NeighbourWithRestWithList()
//		information = fitMoving5NeighbourWithRestWithList();
//		if (information.applicable)
//		return information;

		// level 11: fitMoving5NeighbourWithRest
//		information = fitMoving5NeighbourWithRest(); 
//		if (information.applicable)
//		return information;

		// level 12: fitMoving6NeighbourWithRestWithList()
//		information = fitMoving6NeighbourWithRestWithList();
//		if (information.applicable)
//		return information;

		// level 13: fitMoving6NeighbourWithRest()
//		information = fitMoving6NeighbourWithRest(); 
//		if (information.applicable)
//		return information;

		// THIRD HIRARHY BASED ON FINDING ANY FEASIBLE SOLUTION; SPLLITTING ZONES IN
		// HALF IF NEEDED

		// level 1:
		// ...

		System.out.println("no solution found");
		return new Information(false, factory, 0);
	}

	/*
	 * checkForLargerZone()
	 */
	public Information checkForLargerZone(Zone zone, Factory factoryAsParameter) throws Exception {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Factory factory = copyFactory(factoryAsParameter);

		Zone toAllocate = zone;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			Zone freeZoneTemp = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneTemp.name, freeZoneTemp.amountRasterRow1,
					freeZoneTemp.amountRasterRow2, freeZoneTemp.locationInFactory[0],
					freeZoneTemp.locationInFactory[1]);
			freeZone.setDimensionTrainStationRow1(freeZoneTemp.dimensionTrainStationRow1);
			freeZone.setDimensionTrainStationRow2(freeZoneTemp.dimensionTrainStationRow2);
			freeZone.calculateAmounts();
			if (toAllocate.totalNumberRaster < freeZone.totalNumberRaster) {
				int cost = calculateCost(freeZone, toAllocate);
				Information information = allocateInLargerZone(factory, freeZone, toAllocate);
				information.costs += cost;
				allocationOptions.add(information);
			}
		}

		// check if there is any feasible solution.
		// If there is more than one, chosse the cheapest allocation.
		if (allocationOptions.size() == 0) {
			// qui ce da vedere se e quando usare la seconda hirarchy
			return new Information(false, null, 0);
		} else if (allocationOptions.size() == 1) {
			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
		} else {
			// create an array out of the list
			Information[] allocationOptionsArray = new Information[allocationOptions.size()];
			for (int i = 0; i < allocationOptionsArray.length; i++) {
				allocationOptionsArray[i] = allocationOptions.get(i);
			}
			int counter = 0;
			double minCost = allocationOptionsArray[counter].costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > allocationOptionsArray[counter].costs) {
					minCost = allocationOptionsArray[counter].costs;
					counter = j;
				}
			}
			return new Information(true, allocationOptions.get(counter).modifiedStructure,
					allocationOptions.get(counter).costs);
		}
	}

//	private Information fitPerfectlyWithList(Zone zone, Factory factory) {
//		ArrayList<Information> allocationOptions = new ArrayList<Information>();
//		Factory modifiedStructure = factory; 
//		
//		// for the ZoneToAllocate given as parameter iterate over the emptyZones and
//		// check if there is a feasible solution.
//		// save information (applicable, modifiedstructure, cost) for every feasible
//		// solution
//		Zone toAllocate = zone;
//		
//		ArrayList<Zone> zonesToAllocate = initial.getZonesToAllocate();
//		int length = zonesToAllocate.size();
//		
//		//check if the zoneToAllocate is already into the list of zoneSToBeAllocated
//		boolean alreadyIn = false; 
//		for (int i = 0; i < length; i++) {
//			if (zonesToAllocate.get(i).equals(toAllocate)) {
//				alreadyIn = true; 
//				break;
//			}
//		}
//		if (alreadyIn == false) {
//			zonesToAllocate.add(toAllocate);
//		}
//		
//		// algorithm that creates all combinations of permutations.
//		length = zonesToAllocate.size();
//		int[] array = new int[length];
//		for (int i = 0; i < length; i++) {
//			array[i] = i + 1;
//		}
//
//		combinations.clear();
//		combinations(array, length, length);
//
//		int numberPermutations = combinations.size() / length;
//		for (int j = 0; j < numberPermutations; j++) {
//			Factory modifiedStructurePot = modifiedStructure;
//			boolean alleTrue = true;
//			int cost = 0;
//			for (int j2 = 0; j2 < length; j2++) {
//				int inAddition = j * length;
//				int turn = (int) combinations.get(j2 + inAddition);
//				
//				//modificare to allocate con tutti quelli della lista 
//				
//				for (int j = 0; j < factory.getEmptyZones().size(); j++) {
//					EmptyZone freeZone = (EmptyZone) factory.getEmptyZones().get(j);
//					if (toAllocate.totalNumberRaster == freeZone.totalNumberRaster) {
//						int cost = calculateCost(freeZone, toAllocate);
//						Information information = allocatePerfectFit(factory, freeZone, toAllocate);
//						information.costs = +cost;
//						allocationOptions.add(information);
//					}
//				}
//				
//				Information information = calculate(neighboursToTakeIntoConsideration.get(turn), modifiedStructure);
//				if (information.applicable == false) {
//					alleTrue = false;
//					break;
//				} else {
//					modifiedStructurePot = information.modifiedStructure;
//					cost += information.costs;
//				}
//			}
//			if (alleTrue == true) {
//				allocationOptions.add(new Information(true, modifiedStructurePot, cost));
//			}
//		}
//
//		// check if there is any feasible solution.
//		// If there is more than one, chosse the cheapest allocation.
//		if (allocationOptions.size() == 0) {
//			// qui ce da vedere se e quando usare la seconda hirarchy
//			return new Information(false, null, 0);
//		} else if (allocationOptions.size() == 1) {
//			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
//		} else {
//			Information[] allocationOptionsArray = (Information[]) allocationOptions.toArray();
//			int counter = 0;
//			double minCost = allocationOptionsArray[counter].costs;
//			for (int j = counter; j < allocationOptions.size(); j++) {
//				if (minCost > allocationOptionsArray[counter].costs) {
//					minCost = allocationOptionsArray[counter].costs;
//					counter = j;
//				}
//			}
//			return new Information(true, allocationOptions.get(counter).modifiedStructure,
//					allocationOptions.get(counter).costs);
//		}
//	}

	public Information fitPerfectly(Zone zone, Factory factoryAsParameter) throws Exception {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Factory factory = copyFactory(factoryAsParameter);

		// for the ZoneToAllocate given as parameter iterate over the emptyZones and
		// check if there is a feasible solution.
		// save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = zone;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			// copy the empty zone j into the variable freeZone
			Zone freeZoneTemp = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneTemp.name, freeZoneTemp.amountRasterRow1,
					freeZoneTemp.amountRasterRow2, freeZoneTemp.locationInFactory[0],
					freeZoneTemp.locationInFactory[1]);
			freeZone.setDimensionTrainStationRow1(freeZoneTemp.dimensionTrainStationRow1);
			freeZone.setDimensionTrainStationRow2(freeZoneTemp.dimensionTrainStationRow2);
			freeZone.calculateAmounts();

			// check if the zone to allocate fits perfectly
			if (toAllocate.totalNumberRaster == freeZone.totalNumberRaster) {
				int cost = calculateCost(freeZone, toAllocate);
				Information information = allocatePerfectFit(factory, freeZone, toAllocate);
				information.costs += cost;
				allocationOptions.add(information);
			}
		}

		// check if there is any feasible solution.
		// If there is more than one, chosse the cheapest allocation.
		if (allocationOptions.size() == 0) {
			// qui ce da vedere se e quando usare la seconda hirarchy
			return new Information(false, null, 0);
		} else if (allocationOptions.size() == 1) {
			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
		} else {
			Information[] allocationOptionsArray = new Information[allocationOptions.size()];
			for (int i = 0; i < allocationOptionsArray.length; i++) {
				allocationOptionsArray[i] = allocationOptions.get(i);
			}
			int counter = 0;
			double minCost = allocationOptionsArray[counter].costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > allocationOptionsArray[counter].costs) {
					minCost = allocationOptionsArray[counter].costs;
					counter = j;
				}
			}
			return new Information(true, allocationOptions.get(counter).modifiedStructure,
					allocationOptions.get(counter).costs);
		}
	}

	public Information fitMovingNeighbour(Zone zone, Factory factoryAsParameter, int numberNeighbours)
			throws Exception {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Factory factory = copyFactory(factoryAsParameter);

		// for the ZoneToAllocate given as parameter iterate over the emptyZones.
		// Combine this emptyzone with 1 neigbour.
		// There are "numberNeighbours" possible combinations when moving 1 neighbour.
		// Check if there is a feasible solution.
		// Save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = new Zone(zone.name, zone.amountRasterRow1, zone.amountRasterRow2, zone.locationInFactory[0],
				zone.locationInFactory[1]);
		toAllocate.setDimensionTrainStationRow1(zone.dimensionTrainStationRow1);
		toAllocate.setDimensionTrainStationRow2(zone.dimensionTrainStationRow2);
		toAllocate.calculateAmounts();
		toAllocate.setEmpty(zone.isEmpty());

		// list with all the combinations of neighbours for each empty Zones

		for (int j = 0; j < factory.getEmptyZones().size(); j++) {

			Zone freeZoneAlone = factory.getEmptyZones().get(j);
			// copy the empty zone into the variable freeZone
			EmptyZone freeZone = new EmptyZone(freeZoneAlone.name, freeZoneAlone.amountRasterRow1,
					freeZoneAlone.amountRasterRow2, freeZoneAlone.locationInFactory[0],
					freeZoneAlone.locationInFactory[1]);
			freeZone.setDimensionTrainStationRow1(freeZoneAlone.dimensionTrainStationRow1);
			freeZone.setDimensionTrainStationRow2(freeZoneAlone.dimensionTrainStationRow2);
			freeZone.calculateAmounts();
			freeZone.setEmpty(freeZoneAlone.isEmpty());

			// create all combinations between empty zone and neigbours
			// iterate over the empty zone
			int numberNeighbour = numberNeighbours;
			int locationInFactoryRow = freeZone.locationInFactory[0];
			int locationInFactoryColumn = freeZone.locationInFactory[1];

			ArrayList<Zone> neighboursToTakeIntoConsideration = new ArrayList<Zone>();
			// for this one specific empty zone one list at the time will be created. in the
			// first iteration (for example with 1 neighbour) the list will contain one left
			// neighbour and 0 right
			// neighbours. In the second iteration the list will contain 0 left neighbours
			// and 1 right neighbour. in every iteration a possible allocation will be
			// checked. Here the recursion will be used.

			for (int i = numberNeighbour; i >= 0; i--) {
				neighboursToTakeIntoConsideration = new ArrayList<Zone>();
				int right = i;
				int left = numberNeighbour - right;

				while (right > 0) {
//					int toDebug = factory.getFactoryStructure()[0].length; 
					if (locationInFactoryColumn + right < factory.getFactoryStructure()[0].length) {
						Zone neighbourOnTheRight = factory
								.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn + right];
						if (neighbourOnTheRight != null)
							neighboursToTakeIntoConsideration
									.add(factory.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn
											+ right]);
					}
					right--;
				}

				// extra list for left neighbours. the final thought here is that i want the
				// left outer neighbour to be last in the list:
				// neighboursToTakeIntoConsideration. so this list for the left part will be
				// attached to the normal list, but in inverted sequence
				// the reason for this decision relies in the allocatePerfectFitWithNeighbour
				// method.
				ArrayList<Zone> neighboursToTakeIntoConsiderationOnLeftSide = new ArrayList<Zone>();
				while (left > 0) {
					if (locationInFactoryColumn - left >= 0) {
						Zone neighbourOnTheLeft = factory
								.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn - left];
						if (neighbourOnTheLeft != null)
							neighboursToTakeIntoConsiderationOnLeftSide.add(neighbourOnTheLeft);
					}
					left--;
				}

				// attach the list of the left neighbour the the right neighbours but in
				// opposite sequence
				for (int k = neighboursToTakeIntoConsiderationOnLeftSide.size() - 1; k >= 0; k--) {
					neighboursToTakeIntoConsideration.add(neighboursToTakeIntoConsiderationOnLeftSide.get(k));
				}

				// calculate the total size of the freeZoneAlone and the neighbours taken into
				// consideration in this cycle
				int totalNumberRasterIncludingNeighbours = freeZoneAlone.totalNumberRaster;
				for (int k = 0; k < neighboursToTakeIntoConsideration.size(); k++) {
					totalNumberRasterIncludingNeighbours += neighboursToTakeIntoConsideration.get(k).totalNumberRaster;
				}

				// We allocate the zoneToAllocate only if it fits perfectly in the generated
				// space
				if (toAllocate.totalNumberRaster == totalNumberRasterIncludingNeighbours) {
					int cost = calculateCost((EmptyZone) freeZoneAlone, neighboursToTakeIntoConsideration, toAllocate);
					Factory toReturn = copyFactory(factoryAsParameter);
					toReturn.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn] = null; // set the
					// emptyZone
					// on null;
					Information information = allocatePerfectFitWithNeighbours(toReturn, (EmptyZone) freeZoneAlone,
							neighboursToTakeIntoConsideration, toAllocate);
					information.costs = +cost;
					allocationOptions.add(information);
				}
			}
		}

		// check if there is any feasible solution.
		// If there is more than one, chosse the cheapest allocation.
		if (allocationOptions.size() == 0) {
			// qui ce da vedere se e quando usare la seconda hirarchy
			return new Information(false, null, 0);
		} else if (allocationOptions.size() == 1) {
			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
		} else {
			Information[] allocationOptionsArray = (Information[]) allocationOptions.toArray();
			int counter = 0;
			double minCost = allocationOptionsArray[counter].costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > allocationOptionsArray[counter].costs) {
					minCost = allocationOptionsArray[counter].costs;
					counter = j;
				}
			}
			return new Information(true, allocationOptions.get(counter).modifiedStructure,
					allocationOptions.get(counter).costs);

		}
	}

	private Information fitWithRest(Zone zone, Factory factoryAsParameter) throws Exception {
		Factory factory = copyFactory(factoryAsParameter);
		return checkForLargerZone(zone, factory);
	}

	private Information fitMovingNeighbourWithRest(Zone zone, Factory factoryAsParameter, int numberNeighbours)
			throws Exception {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Factory factory = copyFactory(factoryAsParameter);

		// for the ZoneToAllocate given as parameter iterate over the emptyZones.
		// Combine this emptyzone with 1 neigbour.
		// There are "numberNeighbours" possible combinations when moving 1 neighbour.
		// Check if there is a feasible solution.
		// Save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = zone;

		// list with all the combinations of neighbours for each empty Zones

		for (int j = 0; j < factory.getEmptyZones().size(); j++) {

			Zone freeZoneAlone = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneAlone.name, freeZoneAlone.amountRasterRow1,
					freeZoneAlone.amountRasterRow2, freeZoneAlone.locationInFactory[0],
					freeZoneAlone.locationInFactory[1]);
			freeZone.setDimensionTrainStationRow1(freeZoneAlone.dimensionTrainStationRow1);
			freeZone.setDimensionTrainStationRow2(freeZoneAlone.dimensionTrainStationRow2);
			freeZone.calculateAmounts();

			// create all combinations between empty zone and neigbours
			// iterate over the empty zone
			int numberNeighbour = numberNeighbours;
			int locationInFactoryRow = freeZoneAlone.locationInFactory[0];
			int locationInFactoryColumn = freeZoneAlone.locationInFactory[1];

			ArrayList<Zone> neighboursToTakeIntoConsideration = new ArrayList<Zone>();
			// for this one specific empty zone one list at the time will be created. in the
			// first iteration the list will contain one left neighbour and 0 right
			// neighbours. In the second iteration the list will contain 0 left neighbours
			// and 1 right neighbour. in every iteration a possible allocation will be
			// checked. Here the recursion will be used.

			for (int i = numberNeighbour; i < 0; i--) {
				int right = i;
				int left = numberNeighbour - right;

				while (left > 0) {
					if (locationInFactoryRow - left >= 0) {
						Zone neighbourOnTheLeft = factory
								.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn - left];
						if (neighbourOnTheLeft != null)
							neighboursToTakeIntoConsideration.add(neighbourOnTheLeft);
					}
					left--;
				}
				while (right > 0) {
					if (locationInFactoryColumn + right < 7) {
						Zone neighbourOnTheRight = factory
								.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn + right];
						if (neighbourOnTheRight != null)
							neighboursToTakeIntoConsideration
									.add(factory.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn
											+ right]);
					}
				}

				int totalNumberRasterIncludingNeighbours = freeZoneAlone.totalNumberRaster;
				for (int k = 0; k < neighboursToTakeIntoConsideration.size(); k++) {
					totalNumberRasterIncludingNeighbours = +neighboursToTakeIntoConsideration.get(k).totalNumberRaster;
				}
				if (toAllocate.totalNumberRaster < totalNumberRasterIncludingNeighbours) {
					int cost = calculateCost((EmptyZone) freeZoneAlone, neighboursToTakeIntoConsideration, toAllocate);
					Factory toReturn = copyFactory(factory);
					toReturn.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn] = null; // set the
																											// emptyZone
																											// on null;
					Information information = allocateInLargerZoneWithNeighbours(toReturn, (EmptyZone) freeZoneAlone,
							neighboursToTakeIntoConsideration, toAllocate);
					information.costs = +cost;
					allocationOptions.add(information);
				}
			}
		}

		// check if there is any feasible solution.
		// If there is more than one, chosse the cheapest allocation.
		if (allocationOptions.size() == 0) {
			// qui ce da vedere se e quando usare la seconda hirarchy
			return new Information(false, null, 0);
		} else if (allocationOptions.size() == 1) {
			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
		} else {
			Information[] allocationOptionsArray = (Information[]) allocationOptions.toArray();
			int counter = 0;
			double minCost = allocationOptionsArray[counter].costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > allocationOptionsArray[counter].costs) {
					minCost = allocationOptionsArray[counter].costs;
					counter = j;
				}
			}
			return new Information(true, allocationOptions.get(counter).modifiedStructure,
					allocationOptions.get(counter).costs);
		}

	}

	// COST

	/*
	 * calculateCost() of allocation of a zone into an empty zone
	 */
	public int calculateCost(Zone freeZone, Zone toAllocate) {
		int cost = 0;
		for (int i = 0; i < freeZone.getLogisticEquipment().size(); i++) {
			int freeZoneLE = freeZone.getLogisticEquipment().get(i).anzahl;
			int toAllocateLE = toAllocate.getLogisticEquipment().get(i).anzahl;
			if (freeZoneLE >= toAllocateLE) {
				cost += freeZoneLE - toAllocateLE;
			} else {
				cost += toAllocateLE - freeZoneLE;
			}
		}
		return cost;
	}

	/*
	 * calculateCost() of allocation of a zone into an empty zone + neighbours
	 */
	private int calculateCost(EmptyZone freeZoneAlone, ArrayList<Zone> neighboursToTakeIntoConsideration,
			Zone toAllocate) {
		int cost = 0;
		for (int i = 0; i < freeZoneAlone.getLogisticEquipment().size(); i++) {
			int freeZoneLE = freeZoneAlone.getLogisticEquipment().get(i).anzahl;
			for (int j = 0; j < neighboursToTakeIntoConsideration.size(); j++) {
				freeZoneLE = +neighboursToTakeIntoConsideration.get(j).getLogisticEquipment().get(i).anzahl;
			}
			int toAllocateLE = toAllocate.getLogisticEquipment().get(i).anzahl;
			if (freeZoneLE >= toAllocateLE) {
				cost = +freeZoneLE - toAllocateLE;
			} else {
				cost = +toAllocateLE - freeZoneLE;
			}
		}
		return cost;
	}

	/**
	 * returns true if the given logistic equipment is effectively contained in the
	 * list: anzahl must be > 0
	 * 
	 * @param string
	 * @return
	 * @throws Exception
	 */
	public boolean logEquipRemaining(String string, ArrayList<LogisticEquipment> logisticEquipmentToAllocate,
			ArrayList<LogisticEquipment> logisticEquipmentEmpty) throws Exception {
		for (int i = 0; i < logisticEquipmentEmpty.size(); i++) {
			if (logisticEquipmentEmpty.get(i).getName().equals(string)
					&& logisticEquipmentEmpty.get(i).getAnzahl() > logisticEquipmentToAllocate.get(i).getAnzahl())
				return true;
		}
		return false;
	}

	// add logistic equipment to the newEmptyZone. For method allocateInLargerZone.
	public ArrayList<LogisticEquipment> updateLogisticEquipmentNewEmptyZone(
			ArrayList<LogisticEquipment> logEquipEmptyZone, ArrayList<LogisticEquipment> logEquipZoneToAllocate,
			ArrayList<LogisticEquipment> logEquipNewEmptyZone, int differenceRaster) throws Exception {
		int real = differenceRaster;
		for (int i = differenceRaster; i > 0;) {
			if (real > 0) {
				boolean found = false;
				for (int j = 0; j < logEquipEmptyZone.size(); j++) {
					LogisticEquipment lg = logEquipEmptyZone.get(j);
					int anzahl = lg.getAnzahl();
					int dimension = lg.getDimension();
					boolean remaining = logEquipRemaining(lg.getName(), logEquipZoneToAllocate, logEquipEmptyZone);
					if (dimension == real && anzahl > 0 && remaining) {
						logEquipNewEmptyZone.get(j).anzahlSteigern();
						real -= dimension;
						found = true;
						i = real;
						break;
					}
				}
				if (!found)
					i--;
			} else
				break;
		}
		return logEquipNewEmptyZone;
	}

	// ALLOCATION

	/*
	 * allocationInLargerZone()
	 */
	public Information allocateInLargerZone(Factory factoryAsParamter, EmptyZone emptyZone, Zone toAllocate)
			throws Exception {
		Factory factory = copyFactory(factoryAsParamter);

		Zone[][] factoryStructure = factory.getFactoryStructure();
		Zone[][] tempStructure = new Zone[factoryStructure.length][factoryStructure[0].length + 1];
		int iPos = emptyZone.locationInFactory[0];
		int jPos = emptyZone.locationInFactory[1];

		// calculate total dimension of train station for emptyZone and zoneToAllocate
		int dimensionTrainStationEmptyZone = emptyZone.getDimensionTrainStationRow1()
				+ emptyZone.getDimensionTrainStationRow2();
		int dimensionTrainStationZoneToAllocate = toAllocate.getDimensionTrainStationRow1()
				+ toAllocate.getDimensionTrainStationRow2();
		// calculate the difference
		int differenceTrainStation = dimensionTrainStationEmptyZone - dimensionTrainStationZoneToAllocate;

		// calculate the total amount of raster (without train station) for the
		// emptyZone and for the zoneToAllocate
		int amountOfRasterRow1AndRow2EmptyZone = emptyZone.amountRasterRow1 + emptyZone.amountRasterRow2;
		int amountOfRasterRow1AndRow2ZoneToAllocate = toAllocate.amountRasterRow1 + toAllocate.amountRasterRow2;
		// calculate the difference
		int differenceRaster = amountOfRasterRow1AndRow2EmptyZone - amountOfRasterRow1AndRow2ZoneToAllocate;

		// Because of the fact that the
		// emptyZone is larger than the zoneToAllocate, some raster of the emptyZone
		// remain and are added to the newEmptyZone.
		// If the difference of train stations is negative, the zoneToAllocate has a
		// larger trainStation than the emptyZone. Some of the reamining rasters are
		// used as train station.
		boolean noTrainStationInNewZone = false;
		if (differenceTrainStation < 0) {
			noTrainStationInNewZone = true;
			differenceRaster += differenceTrainStation;
		}

		Zone newEmptyZone = new Zone(emptyZone.name + "Empty", 0, 0, iPos, jPos + 1); // this zone has 0

		// amount of trainStation is positive
		if (!noTrainStationInNewZone) {
			// difference of trainStations is even (0 included)
			if (differenceTrainStation % 2 == 0) {
				newEmptyZone.dimensionTrainStationRow1 = differenceTrainStation / 2;
				newEmptyZone.dimensionTrainStationRow2 = differenceTrainStation / 2;

				// difference of rasters is even
				if (differenceRaster % 2 == 0) {
					newEmptyZone.amountRasterRow1 = differenceRaster / 2;
					newEmptyZone.amountRasterRow2 = differenceRaster / 2;
				}
				// difference of rasters is odd
				else {
					newEmptyZone.amountRasterRow1 = differenceRaster / 2 + 1;
					newEmptyZone.amountRasterRow2 = differenceRaster / 2;
				}

			}
			// difference of train station is odd
			else {
				newEmptyZone.dimensionTrainStationRow1 = differenceTrainStation / 2 + 1;
				newEmptyZone.dimensionTrainStationRow2 = differenceTrainStation / 2;

				if (differenceRaster % 2 == 0) {

					newEmptyZone.amountRasterRow1 = differenceRaster / 2;
					newEmptyZone.amountRasterRow2 = differenceRaster / 2;

				} else {
					newEmptyZone.amountRasterRow1 = differenceRaster / 2 + 1;
					newEmptyZone.amountRasterRow2 = differenceRaster / 2;
				}
			}
		}
		// amount of trainStation is negative. The newEmptyZone will have no
		// trainStation
		else {
			if (differenceRaster % 2 == 0) {

				newEmptyZone.amountRasterRow1 = differenceRaster / 2;
				newEmptyZone.amountRasterRow2 = differenceRaster / 2;
			} else {
				newEmptyZone.amountRasterRow1 = differenceRaster / 2 + 1;
				newEmptyZone.amountRasterRow2 = differenceRaster / 2;
			}
		}

		// calculate the amount of rasters and train stations and set zone as empty
		newEmptyZone.calculateAmounts();
		newEmptyZone.setEmpty(true);
		// add logistic equipment to the newEmptyZone
		newEmptyZone.setLogisticEquipment(updateLogisticEquipmentNewEmptyZone(emptyZone.getLogisticEquipment(),
				toAllocate.getLogisticEquipment(), newEmptyZone.getLogisticEquipment(), differenceRaster));

		// find out what is remaining in the empty zone and what is gone with the
		// allocation of the ZoneToBeAllocated
//		int remaindAmountRasterRow1 = emptyZone.amountRasterRow1 - toAllocate.amountRasterRow1;
//		int remaindAmountRasterRow2 = emptyZone.amountRasterRow2 - toAllocate.amountRasterRow2;
//		int totalDimensionTrainStationRow1 = emptyZone.dimensionTrainStationRow1 + toAllocate.dimensionTrainStationRow1;
//		int totalDimensionTrainStationRow2 = emptyZone.dimensionTrainStationRow2 + toAllocate.dimensionTrainStationRow2;
//
//		int toAllocateDimensionTrainStationRow1;
//		int newEmptyDimensionTrainStationRow1;
//		int toAllocateDimensionTrainStationRow2;
//		int newEmptyDimensionTrainStationRow2;
//		// totalDimensionTrainStationRow1 aufteilen auf die toAllcate und die
//		// newEmptyZone
//		if (totalDimensionTrainStationRow1 % 2 == 0) {
//			toAllocateDimensionTrainStationRow1 = totalDimensionTrainStationRow1 / 2;
//			newEmptyDimensionTrainStationRow1 = toAllocateDimensionTrainStationRow1;
//		} else {
//			newEmptyDimensionTrainStationRow1 = totalDimensionTrainStationRow1 / 2;
//			toAllocateDimensionTrainStationRow1 = totalDimensionTrainStationRow1 - newEmptyDimensionTrainStationRow1;
//		}
//		// totalDimensionTrainStationRow2 aufteilen auf die toAllcate und die
//		// newEmptyZone
//		if (totalDimensionTrainStationRow2 % 2 == 0) {
//			toAllocateDimensionTrainStationRow2 = totalDimensionTrainStationRow2 / 2;
//			newEmptyDimensionTrainStationRow2 = toAllocateDimensionTrainStationRow2;
//		} else {
//			newEmptyDimensionTrainStationRow2 = totalDimensionTrainStationRow2 / 2;
//			toAllocateDimensionTrainStationRow2 = totalDimensionTrainStationRow2 - newEmptyDimensionTrainStationRow2;
//		}
//
//		// assignment of sizes
//		toAllocate.dimensionTrainStationRow1 = toAllocateDimensionTrainStationRow1;
//		toAllocate.dimensionTrainStationRow2 = toAllocateDimensionTrainStationRow2;
//
//		Zone newEmptyZone = new Zone(emptyZone.name + "Empty", newEmptyDimensionTrainStationRow1,
//				newEmptyDimensionTrainStationRow2, iPos, jPos + 1); // this zone has 0 logistic equipment
//		newEmptyZone.amountRasterRow1 = remaindAmountRasterRow1;
//		newEmptyZone.amountRasterRow2 = remaindAmountRasterRow2;
//		newEmptyZone.dimensionTrainStationRow1 = newEmptyDimensionTrainStationRow1;
//		newEmptyZone.dimensionTrainStationRow2 = newEmptyDimensionTrainStationRow2;
//		newEmptyZone.calculateAmounts();
//		newEmptyZone.setEmpty(true);

		// error handling
//		while (newEmptyZone.totalNumberRaster + toAllocate.totalNumberRaster > emptyZone.totalNumberRaster) {
//			if () {
//				
//			} else if() {
//				
//			} else if() {
//				
//			} else {
//				
//			}
//		}

		// allocazione della ZoneToBeAllocated
		tempStructure[iPos][jPos] = toAllocate;
		tempStructure[iPos][jPos + 1] = newEmptyZone;

		// copying the initial factoryStructure into the new tempStructure, that will be
		// given back.
		// int i = row
		boolean passedOver_ToAllocate_and_NewEmptyZone = false;
		for (int row = 0; row < tempStructure.length; row++) {
			if (row == iPos) { // we are in the same row in which the ZoneToBeAllocate and the newEmptyzone
								// have been allocated.
				for (int column = 0; column < tempStructure[0].length; column++) {
					if (passedOver_ToAllocate_and_NewEmptyZone == false) {
						if (column != jPos) {
							// HERE
							Zone singleZoneToCopy = factoryStructure[row][column];
							if (singleZoneToCopy != null) {
								Zone singleZoneToReturn = new Zone(singleZoneToCopy.name,
										singleZoneToCopy.amountRasterRow1, singleZoneToCopy.amountRasterRow2,
										singleZoneToCopy.locationInFactory[0], singleZoneToCopy.locationInFactory[1]);
								singleZoneToReturn.dimensionTrainStationRow1 = singleZoneToCopy.dimensionTrainStationRow1;
								singleZoneToReturn.dimensionTrainStationRow2 = singleZoneToCopy.dimensionTrainStationRow2;
								singleZoneToReturn.calculateAmounts();
								singleZoneToReturn.setEmpty(singleZoneToCopy.isEmpty());
								// directly copied, as never changed
								singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
								tempStructure[row][column] = singleZoneToReturn;
							} else {
								tempStructure[row][column] = null;
							}
						} else {
							column++;
							passedOver_ToAllocate_and_NewEmptyZone = true;
						}
					} else { // passedOver == true
						// HERE
						Zone singleZoneToCopy = factoryStructure[row][column - 1];
						if (singleZoneToCopy != null) {
							Zone singleZoneToReturn = new Zone(singleZoneToCopy.name, singleZoneToCopy.amountRasterRow1,
									singleZoneToCopy.amountRasterRow2, singleZoneToCopy.locationInFactory[0],
									singleZoneToCopy.locationInFactory[1]);
							singleZoneToReturn.dimensionTrainStationRow1 = singleZoneToCopy.dimensionTrainStationRow1;
							singleZoneToReturn.dimensionTrainStationRow2 = singleZoneToCopy.dimensionTrainStationRow2;
							singleZoneToReturn.calculateAmounts();
							singleZoneToReturn.setEmpty(singleZoneToCopy.isEmpty());
							// directly copied, as never changed
							singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
							tempStructure[row][column] = singleZoneToReturn;
						} else {
							tempStructure[row][column] = null;
						}
					}
				}
			} else { // we are in a normal row, where we can copy the old Structure in the new one,
						// without any further problems
				for (int column = 1; column < tempStructure[0].length; column++) {
					// here
					Zone singleZoneToCopy = factoryStructure[row][column - 1]; // we shift one column to the right, as
																				// the tempStructure is 1 unit larger.
					if (singleZoneToCopy != null) {
						Zone singleZoneToReturn = new Zone(singleZoneToCopy.name, singleZoneToCopy.amountRasterRow1,
								singleZoneToCopy.amountRasterRow2, singleZoneToCopy.locationInFactory[0],
								singleZoneToCopy.locationInFactory[1]);
						singleZoneToReturn.dimensionTrainStationRow1 = singleZoneToCopy.dimensionTrainStationRow1;
						singleZoneToReturn.dimensionTrainStationRow2 = singleZoneToCopy.dimensionTrainStationRow2;
						singleZoneToReturn.calculateAmounts();
						singleZoneToReturn.setEmpty(singleZoneToCopy.isEmpty());
						// directly copied, as never changed
						singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
						tempStructure[row][column] = singleZoneToReturn;
					} else {
						tempStructure[row][column] = null;
					}
				}
			}
		}

		factory.setFactoryStructure(tempStructure);
		factory.setEmptyZones(factory.createEmptyZones(factory.getFactoryStructure()));
		for (int i = 0; i < factory.getZonesToAllocate().size(); i++) {
			if (factory.getZonesToAllocate().get(i).name.equals(toAllocate.name)) {
				factory.getZonesToAllocate().remove(i);
			}
		}
		return new Information(true, factory, 0);
	}

	/*
	 * allocatePerfectFit()
	 */
	public Information allocatePerfectFit(Factory factoryAsParameter, EmptyZone emptyZone, Zone toAllocate)
			throws Exception {
		Factory factory = copyFactory(factoryAsParameter);

		Zone[][] tempStructure = factory.getFactoryStructure();
		int i = emptyZone.locationInFactory[0];
		int j = emptyZone.locationInFactory[1];

		Zone singleZoneToReturn = new Zone(toAllocate.name, toAllocate.amountRasterRow1, toAllocate.amountRasterRow2,
				toAllocate.locationInFactory[0], toAllocate.locationInFactory[1]);
		singleZoneToReturn.dimensionTrainStationRow1 = toAllocate.dimensionTrainStationRow1;
		singleZoneToReturn.dimensionTrainStationRow2 = toAllocate.dimensionTrainStationRow2;
		singleZoneToReturn.calculateAmounts();
		singleZoneToReturn.setEmpty(toAllocate.isEmpty());
		// directly copied, as never changed
		singleZoneToReturn.setLogisticEquipment(toAllocate.getLogisticEquipment());
		tempStructure[i][j] = singleZoneToReturn;

		factory.setFactoryStructure(tempStructure);
		factory.setEmptyZones(factory.createEmptyZones(factory.getFactoryStructure()));
		for (int i1 = 0; i1 < factory.getZonesToAllocate().size(); i1++) {
			if (factory.getZonesToAllocate().get(i1).name.equals(toAllocate.name)) {
				factory.getZonesToAllocate().remove(i1);
			}
		}
		return new Information(true, factory, 0);
	}

	/*
	 * allocatePerfectFitWithNeighbours()
	 */
	private Information allocatePerfectFitWithNeighbours(Factory factoryAsParameter, EmptyZone freeZoneAlone,
			ArrayList<Zone> neighboursToTakeIntoConsideration, Zone toAllocate) throws Exception {
		Factory factory = copyFactory(factoryAsParameter);

		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		Factory modifiedStructure = factory;

		// put the zones that are taken out on null
		int row = 0;
		int column = 0;
		for (int i = 0; i < neighboursToTakeIntoConsideration.size(); i++) {
			Zone neighbour = neighboursToTakeIntoConsideration.get(i);
			row = neighbour.locationInFactory[0];
			column = neighbour.locationInFactory[1];
			modifiedStructure.getFactoryStructure()[row][column] = null;
		}
		// put the empty zone on null as well
		int locationInFactoryRowEmptyZone = freeZoneAlone.locationInFactory[0];
		int locationInFactoryColumnEmptyZone = freeZoneAlone.locationInFactory[1];
		modifiedStructure.getFactoryStructure()[locationInFactoryRowEmptyZone][locationInFactoryColumnEmptyZone] = null;

		// put in the zone toAllocate in the structure
		Zone singleZoneToReturn = new Zone(toAllocate.name, toAllocate.amountRasterRow1, toAllocate.amountRasterRow2,
				toAllocate.locationInFactory[0], toAllocate.locationInFactory[1]);
		singleZoneToReturn.dimensionTrainStationRow1 = toAllocate.dimensionTrainStationRow1;
		singleZoneToReturn.dimensionTrainStationRow2 = toAllocate.dimensionTrainStationRow2;
		singleZoneToReturn.calculateAmounts();
		singleZoneToReturn.setEmpty(toAllocate.isEmpty());
		// directly copied, as never changed
		singleZoneToReturn.setLogisticEquipment(toAllocate.getLogisticEquipment());
		// allocation in rowRight, columnRight

		/*
		 * next part: move remaining zones in the structure to the right spot, such that
		 * no gaps are left over
		 */
		// the location of the freeZoneAlone
		int rowEmpty = freeZoneAlone.locationInFactory[0];
		int columnEmpty = freeZoneAlone.locationInFactory[1];
		// the location in the factory of the last neighbour facing the right. NON
		// FUNZIONA SEMPRE
		Zone neighbourRight = neighboursToTakeIntoConsideration.get(0);
		int rowRight = neighbourRight.locationInFactory[0];
		int columnRight = neighbourRight.locationInFactory[1];
		boolean min1RightNeihgbourExists = true;
		if (columnRight <= columnEmpty) {
			min1RightNeihgbourExists = false;
		}
		// allocation singleZoneToReturn
		if (min1RightNeihgbourExists) {
			modifiedStructure.getFactoryStructure()[rowRight][columnRight] = singleZoneToReturn;
		} else {
			modifiedStructure.getFactoryStructure()[rowEmpty][columnEmpty] = singleZoneToReturn;
		}
		// the location in the factory of the last neighbour facing the left
		int indexLeft = neighboursToTakeIntoConsideration.size() - 1;
		Zone neighbourLeft = neighboursToTakeIntoConsideration.get(indexLeft);
		int rowLeft = neighbourLeft.locationInFactory[0];
		int columnLeft = neighbourLeft.locationInFactory[1];
		boolean min1LeftNeihgbourExists = true;
		if (columnLeft >= columnEmpty) {
			min1LeftNeihgbourExists = false;
		}

		if (min1LeftNeihgbourExists) {
			for (int i = 0; i < modifiedStructure.getFactoryStructure()[0].length; i++) {
				if (columnLeft - 1 - i >= 0) {
					Zone zoneToBeMoved = modifiedStructure.getFactoryStructure()[rowLeft][columnLeft - 1 - i];
					modifiedStructure.getFactoryStructure()[rowLeft][columnLeft - 1 - i] = null;
					modifiedStructure.getFactoryStructure()[rowLeft][columnLeft - 1 - i
							+ neighboursToTakeIntoConsideration.size()] = zoneToBeMoved;
				}
			}
		} else {
			for (int i = 0; i < modifiedStructure.getFactoryStructure()[0].length; i++) {
				if (columnEmpty - 1 - i >= 0) {
					Zone zoneToBeMoved = modifiedStructure.getFactoryStructure()[rowEmpty][columnEmpty - 1 - i];
					modifiedStructure.getFactoryStructure()[rowEmpty][columnEmpty - 1 - i] = null;
					modifiedStructure.getFactoryStructure()[rowEmpty][columnEmpty - 1 - i
							+ neighboursToTakeIntoConsideration.size()] = zoneToBeMoved;
				}
			}
		}

		// nest part: algorithm that creates all combinations of permutations.
		// we need these permutation because we want to let the neighbours reenter the
		// algoirhtm in every possible sequence. We know the sequence of how they enter
		// the factory does matter, as each of them changes the factoryStructure and the
		// neighboursfollowing the last one, will have to fit in the modified structure
		// and not in the inital one
		int length = neighboursToTakeIntoConsideration.size();
		int[] array = new int[length];
		for (int i1 = 0; i1 < length; i1++) {
			array[i1] = i1 + 1;
		}

		combinations.clear();

		combinations(array, length, length);

		int numberPermutations = combinations.size() / length;
		for (int j = 0; j < numberPermutations; j++) {
			Factory modifiedStructurePot = copyFactory(modifiedStructure);
			boolean alleTrue = true;
			int cost = 0;
			for (int j2 = 0; j2 < length; j2++) {
				int inAddition = j * length;
				int turn = (int) combinations.get(j2 + inAddition) - 1;
				Information information = calculate(neighboursToTakeIntoConsideration.get(turn), modifiedStructurePot);
				if (information.applicable == false) {
					alleTrue = false;
					break;
				} else {
					modifiedStructurePot = information.modifiedStructure;
					cost += information.costs;
				}
			}
			if (alleTrue == true) {
				allocationOptions.add(new Information(true, modifiedStructurePot, cost));
			}
		}

		// check if there is any feasible solution.
		// If there is more than one, chosse the cheapest allocation.
		if (allocationOptions.size() == 0) {
			// qui ce da vedere se e quando usare la seconda hirarchy
			return new Information(false, null, 0);
		} else if (allocationOptions.size() == 1) {
			Factory toReturn = allocationOptions.get(0).modifiedStructure;
			double costs = allocationOptions.get(0).costs;
			toReturn.setEmptyZones(toReturn.createEmptyZones(toReturn.getFactoryStructure()));
			for (int i = 0; i < toReturn.getZonesToAllocate().size(); i++) {
				if (toReturn.getZonesToAllocate().get(i).name.equals(toAllocate.name)) {
					toReturn.getZonesToAllocate().remove(i);
				}
			}
			return new Information(true, toReturn, costs);
		} else {
			Information[] allocationOptionsArray = (Information[]) allocationOptions.toArray();
			int counter = 0;
			double minCost = allocationOptionsArray[counter].costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > allocationOptionsArray[counter].costs) {
					minCost = allocationOptionsArray[counter].costs;
					counter = j;
				}
			}
			Factory toReturn = allocationOptions.get(counter).modifiedStructure;
			toReturn.setEmptyZones(toReturn.createEmptyZones(toReturn.getFactoryStructure()));
			for (int i = 0; i < toReturn.getZonesToAllocate().size(); i++) {
				if (toReturn.getZonesToAllocate().get(i).name.equals(toAllocate.name)) {
					toReturn.getZonesToAllocate().remove(i);
				}
			}
			return new Information(true, toReturn, allocationOptions.get(counter).costs);
		}
	}

	private Information allocateInLargerZoneWithNeighbours(Factory factoryAsParameter, EmptyZone freeZoneAlone,
			ArrayList<Zone> neighboursToTakeIntoConsideration, Zone toAllocate) throws Exception {
		Factory factory = copyFactory(factoryAsParameter);

		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Zone[][] factoryStructure = factory.getFactoryStructure();
//		Zone[][] tempStructure = new Zone[factoryStructure.length][factoryStructure[0].length + 1];
		int iPos = freeZoneAlone.locationInFactory[0];
		int jPos = freeZoneAlone.locationInFactory[1];

		// put the zones that are taken out on null
		int row = 0;
		int column = 0;
		for (int i = 0; i < neighboursToTakeIntoConsideration.size(); i++) {
			Zone neighbour = neighboursToTakeIntoConsideration.get(i);
			row = neighbour.locationInFactory[0];
			column = neighbour.locationInFactory[1];
			factoryStructure[row][column] = null;
		}

		// find out what is remaining in the empty zone and what is gone with the
		// allocation of the ZoneToBeAllocated
		int remaindAmountRasterRow1 = freeZoneAlone.amountRasterRow1 - toAllocate.amountRasterRow1;
		int remaindAmountRasterRow2 = freeZoneAlone.amountRasterRow2 - toAllocate.amountRasterRow2;
		int totalDimensionTrainStationRow1 = freeZoneAlone.dimensionTrainStationRow1
				+ toAllocate.dimensionTrainStationRow1;
		int totalDimensionTrainStationRow2 = freeZoneAlone.dimensionTrainStationRow2
				+ toAllocate.dimensionTrainStationRow2;

		int toAllocateDimensionTrainStationRow1;
		int newEmptyDimensionTrainStationRow1;
		int toAllocateDimensionTrainStationRow2;
		int newEmptyDimensionTrainStationRow2;
		// totalDimensionTrainStationRow1 aufteilen auf die toAllcate und die
		// newEmptyZone
		if (totalDimensionTrainStationRow1 % 2 == 0) {
			toAllocateDimensionTrainStationRow1 = totalDimensionTrainStationRow1 / 2;
			newEmptyDimensionTrainStationRow1 = toAllocateDimensionTrainStationRow1;
		} else {
			newEmptyDimensionTrainStationRow1 = totalDimensionTrainStationRow1 / 2;
			toAllocateDimensionTrainStationRow1 = totalDimensionTrainStationRow1 - newEmptyDimensionTrainStationRow1;
		}
		// totalDimensionTrainStationRow2 aufteilen auf die toAllcate und die
		// newEmptyZone
		if (totalDimensionTrainStationRow2 % 2 == 0) {
			toAllocateDimensionTrainStationRow2 = totalDimensionTrainStationRow2 / 2;
			newEmptyDimensionTrainStationRow2 = toAllocateDimensionTrainStationRow2;
		} else {
			newEmptyDimensionTrainStationRow2 = totalDimensionTrainStationRow2 / 2;
			toAllocateDimensionTrainStationRow2 = totalDimensionTrainStationRow2 - newEmptyDimensionTrainStationRow2;
		}

		// assignment of sizes
		toAllocate.dimensionTrainStationRow1 = toAllocateDimensionTrainStationRow1;
		toAllocate.dimensionTrainStationRow2 = toAllocateDimensionTrainStationRow2;

		Zone newEmptyZone = new Zone(freeZoneAlone.name + "Empty", newEmptyDimensionTrainStationRow1,
				newEmptyDimensionTrainStationRow2, iPos, jPos + 1); // this zone has 0 logistic equipment
		newEmptyZone.amountRasterRow1 = remaindAmountRasterRow1;
		newEmptyZone.amountRasterRow2 = remaindAmountRasterRow2;
		newEmptyZone.dimensionTrainStationRow1 = newEmptyDimensionTrainStationRow1;
		newEmptyZone.dimensionTrainStationRow2 = newEmptyDimensionTrainStationRow2;

		// put in the zone toAllocate in the structure
		factoryStructure[row][column] = newEmptyZone;
		factoryStructure[row][column - 1] = toAllocate;

		// move remaining zones in the structure to the right spot.
		Zone neighbour = neighboursToTakeIntoConsideration.get(0);
		row = neighbour.locationInFactory[0];
		column = neighbour.locationInFactory[1];
		for (int i = 1; i < 7; i++) {
			if (column - i >= 0) {
				Zone zoneToShift = factoryStructure[row][column - i];
				factoryStructure[row][column - i] = null;
				for (int j = 0; j < 7; j++) {
					if (factoryStructure[row][column - i + j] != null) {
						factoryStructure[row][column - i + j - 1] = zoneToShift;
					}
				}
			}
		}

		// algorithm that creates all combinations of permutations.
		int length = neighboursToTakeIntoConsideration.size();
		int[] array = new int[length];
		for (int i = 0; i < length; i++) {
			array[i] = i + 1;
		}

		combinations.clear();
		combinations(array, length, length);

		int numberPermutations = combinations.size() / length;
		for (int j = 0; j < numberPermutations; j++) {
			Zone[][] modifiedStructurePot = factoryStructure;
			boolean alleTrue = true;
			int cost = 0;
			for (int j2 = 0; j2 < length; j2++) {
				int inAddition = j * length;
				int turn = (int) combinations.get(j2 + inAddition);
				Factory asParameter = new Factory();
				asParameter.setFactoryStructure(factoryStructure);
				Information information = calculate(neighboursToTakeIntoConsideration.get(turn), asParameter);
				if (information.applicable == false) {
					alleTrue = false;
					break;
				} else {
					modifiedStructurePot = information.modifiedStructure.getFactoryStructure();
					cost += information.costs;
				}
			}
			if (alleTrue == true) {
				Factory toReturn = new Factory();
				toReturn.setFactoryStructure(modifiedStructurePot);
				allocationOptions.add(new Information(true, toReturn, cost));
			}
		}

		// check if there is any feasible solution.
		// If there is more than one, chosse the cheapest allocation.
		if (allocationOptions.size() == 0) {
			// qui ce da vedere se e quando usare la seconda hirarchy
			return new Information(false, null, 0);
		} else if (allocationOptions.size() == 1) {
			Factory toReturn = allocationOptions.get(0).modifiedStructure;
			double costs = allocationOptions.get(0).costs;
			toReturn.setEmptyZones(factory.createEmptyZones(factory.getFactoryStructure()));
			for (int i = 0; i < factory.getZonesToAllocate().size(); i++) {
				if (factory.getZonesToAllocate().get(i).name.equals(toAllocate.name)) {
					factory.getZonesToAllocate().remove(i);
				}
			}
			return new Information(true, toReturn, 0);
		} else {
			Information[] allocationOptionsArray = (Information[]) allocationOptions.toArray();
			int counter = 0;
			double minCost = allocationOptionsArray[counter].costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > allocationOptionsArray[counter].costs) {
					minCost = allocationOptionsArray[counter].costs;
					counter = j;
				}
			}
			Factory toReturn = allocationOptions.get(counter).modifiedStructure;
			toReturn.setEmptyZones(factory.createEmptyZones(factory.getFactoryStructure()));
			for (int i = 0; i < factory.getZonesToAllocate().size(); i++) {
				if (factory.getZonesToAllocate().get(i).name.equals(toAllocate.name)) {
					factory.getZonesToAllocate().remove(i);
				}
			}
			return new Information(true, toReturn, allocationOptions.get(counter).costs);
		}
	}

	/*
	 * combinations(): auxilliary method, that creates all permutations of numbers
	 * 1, 2, ... , array.length.
	 */
	private void combinations(int[] array, int length, int depth) {
		if (length == 1) {
			for (int i = 0; i < array.length; i++) {
				combinations.add(array[i]);
			}
		} else {
			for (int i = 0; i < length - 1; i++) {
				combinations(array, length - 1, depth);

				if (length % 2 == 1) {
					int tmp = array[0];
					array[0] = array[length - 1];
					array[length - 1] = tmp;
				} else {
					int tmp = array[i];
					array[i] = array[length - 1];
					array[length - 1] = tmp;
				}
			}
			combinations(array, length - 1, depth);
		}
	}

	private Factory copyFactory(Factory factoryAsParameter) {
		Factory toReturn = new Factory(true);

		// FactoryStructure
		Zone[][] zoneToCopy = factoryAsParameter.getFactoryStructure();
		int rows = zoneToCopy.length;
		int columns = zoneToCopy[0].length;
		Zone[][] zoneToReturn = new Zone[rows][columns];
		for (int row = 0; row < zoneToReturn.length; row++) {
			for (int column = 0; column < zoneToReturn[0].length; column++) {
				Zone singleZoneToCopy = zoneToCopy[row][column];
				if (singleZoneToCopy != null) {
					Zone singleZoneToReturn = new Zone(singleZoneToCopy.name, singleZoneToCopy.amountRasterRow1,
							singleZoneToCopy.amountRasterRow2, singleZoneToCopy.locationInFactory[0],
							singleZoneToCopy.locationInFactory[1]);
					singleZoneToReturn.dimensionTrainStationRow1 = singleZoneToCopy.dimensionTrainStationRow1;
					singleZoneToReturn.dimensionTrainStationRow2 = singleZoneToCopy.dimensionTrainStationRow2;
					singleZoneToReturn.calculateAmounts();
					singleZoneToReturn.setEmpty(singleZoneToCopy.isEmpty());
					// directly copied, as never changed
					singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
					zoneToReturn[row][column] = singleZoneToReturn;
				} else {
					zoneToReturn[row][column] = null;
				}
			}
		}
		toReturn.setFactoryStructure(zoneToReturn);

		// list emptyZones
		ArrayList<EmptyZone> listToCopyEmpty = factoryAsParameter.getEmptyZones();
		ArrayList<EmptyZone> listToReturnEmpty = new ArrayList<EmptyZone>();
		for (int i = 0; i < listToCopyEmpty.size(); i++) {
			Zone singleZoneToCopy = listToCopyEmpty.get(i);
			EmptyZone singleZoneToReturn = new EmptyZone(singleZoneToCopy.name, singleZoneToCopy.amountRasterRow1,
					singleZoneToCopy.amountRasterRow2, singleZoneToCopy.locationInFactory[0],
					singleZoneToCopy.locationInFactory[1]);
			singleZoneToReturn.dimensionTrainStationRow1 = singleZoneToCopy.dimensionTrainStationRow1;
			singleZoneToReturn.dimensionTrainStationRow2 = singleZoneToCopy.dimensionTrainStationRow2;
			singleZoneToReturn.calculateAmounts();
			singleZoneToReturn.setEmpty(singleZoneToCopy.isEmpty());
			// directly copied, as never changed
			singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
			listToReturnEmpty.add(singleZoneToReturn);
		}
		toReturn.setEmptyZones(listToReturnEmpty);

		// list zonesToAllocate
		ArrayList<Zone> listToCopyAllocate = factoryAsParameter.getZonesToAllocate();
		ArrayList<Zone> listToReturnAllocate = new ArrayList<Zone>();
		for (int i = 0; i < listToCopyAllocate.size(); i++) {
			Zone singleZoneToCopy = listToCopyAllocate.get(i);
			EmptyZone singleZoneToReturn = new EmptyZone(singleZoneToCopy.name, singleZoneToCopy.amountRasterRow1,
					singleZoneToCopy.amountRasterRow2, singleZoneToCopy.locationInFactory[0],
					singleZoneToCopy.locationInFactory[1]);
			singleZoneToReturn.dimensionTrainStationRow1 = singleZoneToCopy.dimensionTrainStationRow1;
			singleZoneToReturn.dimensionTrainStationRow2 = singleZoneToCopy.dimensionTrainStationRow2;
			singleZoneToReturn.calculateAmounts();
			singleZoneToReturn.setEmpty(singleZoneToCopy.isEmpty());
			// directly copied, as never changed
			singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
			listToReturnAllocate.add(singleZoneToReturn);
		}
		toReturn.setZonesToAllocate(listToReturnAllocate);

		return toReturn;
	}

	/*
	 * creates a copy of a matrix array //va cambiata la syntax. in parte presa da
	 * internet
	 */
	public Zone[][] copyOfFactoryModification(Zone[][] factory) {
		Zone[][] copyFactory = new Zone[factory.length][];
		for (int i = 0; i < factory.length; i++) {
			Zone[] row = factory[i];
			int rowLength = row.length;
			copyFactory[i] = new Zone[rowLength];
			System.arraycopy(row, 0, copyFactory[i], 0, rowLength);
		}
		return copyFactory;
	}

	/*
	 * After having computed the new factory layout, we reconvert the factory to
	 * excel
	 */
	public void convertToExcel(Factory factory) {

	}

	public static void demoFactory(Factory initial) {
		Zone[][] factoryStructure = initial.getFactoryStructure();
		for (int i = 0; i < factoryStructure.length; i++) {
			System.out.println("\n" + "NEW ROW" + "\n");
			for (int j = 0; j < factoryStructure[0].length; j++) {
				if (factoryStructure[i][6 - j] == null) {
					System.out.println("null");
				} else {
					System.out.println(factoryStructure[i][6 - j].toString());
					System.out.println(Arrays.deepToString(factoryStructure[i][6 - j].raster));
				}
			}
		}
	}

	public static void demoZonesToAllocate(Factory initial) {
		for (int i = 0; i < initial.getZonesToAllocate().size(); i++) {
			System.out.println(initial.getZonesToAllocate().get(i).toString());
		}
	}

	public static void main(String[] args) throws Exception {
		// Initialize objects and start algorithm

//		Import old = new Import(); 
//		old.demo();

		// calculating execution time
		final long start = System.currentTimeMillis();

		initial = new Factory();
		demoFactory(initial);
		System.out.println("\n\n\n\n\n\n\n\n");
		demoZonesToAllocate(initial);
		System.out.println("\n\n\n\n\n\n\n\n");

		Calculator calculator = new Calculator();
		calculator.performAlgorithm();

		// calculating execution time
		final long end = System.currentTimeMillis();
		System.out.println("n\n\n\n\"Total execution time: " + (end - start));

//		newFactoryStructure = calculator.performAlgorithm();
//		Factory newFactory = initial;
//		newFactory.setFactoryStructure(newFactoryStructure);
		// calculateCostBenefits(initial.getFactoryStructure, newFactoryStructure);

	}
}
