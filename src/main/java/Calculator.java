import java.io.IOException;
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

	public Zone[][] performAlgorithm() {

		System.out.println("ciao padrone");

		ArrayList<Zone> zonesToBeAllocated = initial.getZonesToAllocate();
		System.out.println("zonesToBeAllocated.size: " + zonesToBeAllocated.size());

		// in case the list of zonesToBeAllocated is empty
		if (zonesToBeAllocated.isEmpty()) {
			return initial.getFactoryStructure();
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
//			System.out.println("zonesToBeAllocated.size: " + zonesToBeAllocated.size());
			zonesToBeAllocated = initial.getZonesToAllocate(); // after

			for (int i = 0; i < zonesToBeAllocated.size(); i++) {
				zonesToBeAllocated.get(i).information = calculate(zonesToBeAllocated.get(i), initial);
			}

			// choose the cheapest for this iteration
			// error: guarda se applicable é true
			int i = 0, j = 0;
			Information informationOfBestZone = zonesToBeAllocated.get(i).information;
			for (i = 1; i < zonesToBeAllocated.size(); i++) {
				if (zonesToBeAllocated.get(i).information.applicable) {
					if (zonesToBeAllocated.get(i).information.costs < informationOfBestZone.costs) {
						informationOfBestZone = zonesToBeAllocated.get(i).information;
						j = i;
					}
				}

				// apply the modifications to real factoryStructure. take zone out of
				// zonesToBeAllocated
				initial.setFactoryStructure(
						zonesToBeAllocated.get(j).information.modifiedStructure.getFactoryStructure());
				zonesToBeAllocated.remove(j);
			}
		}
		return initial.getFactoryStructure();

	}

	public Information calculate(Zone zone, Factory factory) {
//		System.out.println("Zone: " + zone.name);
		// prima with list

		Information information; // information (boolean applicable, Zone[][] modifiedStructure, double cost)

		// level 0: Edge-case: checkForLargerZone
		if (zone.equals(initial.getZonesToAllocate().get(0))) {
			information = checkForLargerZone(zone, factory);
			if (information.applicable)
				return information;
		}

		// level 1: fitPerfectly
		information = fitPerfectly(zone, factory);
		if (information.applicable)
			return information;

		// level 2: fitPerfectlyWithList
//		information = fitPerfectlyWithList(zone, factory);
//		if (information.applicable) return information;

		// level 3: fitMoving1Neighbour
		information = fitMovingNeighbour(zone, factory, 1);
		if (information.applicable)
			return information;

		// level 4: fitMoving1NeighbourWithList
//		information = fitMovingNeighbourWithList(); 
//		if (information.applicable) return information;

		// level 5: fitMoving2Neighbour
		information = fitMovingNeighbour(zone, factory, 2);
		if (information.applicable)
			return information;

		// level 6: fitMoving2NeighbourWithList
//		information = fitMoving2NeighboursWithList(); 
//		if (information.applicable) return information;

		// level 7: fitMoving3Neighbours
		information = fitMovingNeighbour(zone, factory, 3);
		if (information.applicable)
			return information;

		// level8: fitMoving3NeighboursWithList
//		fitMoving3NeighboursWithList();
//		if (information.applicable) return information;

		// level9: fitMoving4Neighbour
		information = fitMovingNeighbour(zone, factory, 4);
		if (information.applicable)
			return information;

		// level10: fitMoving4NeighbourWithList
//		fitMoving4NeighboursWithList(); 
//		if (information.applicable) return information;

		// level 11: fitMoving5Neighbours
		information = fitMovingNeighbour(zone, factory, 5);
		if (information.applicable)
			return information;

		// level 12: fitMoving5NeighboursWithList
//		fitMoving5NeighboursWithList(); 
//		if (information.applicable) return information;

		// level 13: fitMoving6Neighbours
		information = fitMovingNeighbour(zone, factory, 6);
		if (information.applicable)
			return information;

		// level 14: fitMoving6NeighboursWithList
//		fitMoving6NeighboursWithList(); 
//		if (information.applicable) return information;

		// level 15: fitWithRestWithList();
//		fitWithRestWithList(); 

		// level 16: fitWithRest();
//		information = fitWithRest(zone, factory);
//		if (information.applicable)
//			return information;

//		fitMoving1NeighbourWithRestWithList();
//		fitMoving1NeighbourWithRest()
//		fitMoving2NeighbourWithRestWithList();
//		fitMoving2eighbourWithRest()
//		fitMoving3NeighbourWithRestWithList();
//		fitMoving3NeighbourWithRest()
//		fitMoving4NeighbourWithRestWithList();
//		fitMoving4NeighbourWithRest()
//		fitMoving5NeighbourWithRestWithList();
//		fitMoving5NeighbourWithRest()
//		fitMoving6NeighbourWithRestWithList();
//		fitMoving6NeighbourWithRest()

		return new Information(false, factory, 0);
	}

	/*
	 * checkForLargerZone()
	 */
	public Information checkForLargerZone(Zone zone, Factory factory) {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		Zone toAllocate = zone;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			Zone freeZoneTemp = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneTemp.name, freeZoneTemp.amountRasterRow1,
					freeZoneTemp.amountRasterRow2, freeZoneTemp.dimensionTrainStationRow1,
					freeZoneTemp.dimensionTrainStationRow2);
			if (toAllocate.totalNumberRaster < freeZone.totalNumberRaster) {
				// metti qui l'algoritmo
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

	public Information fitPerfectly(Zone zone, Factory factory) {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		// for the ZoneToAllocate given as parameter iterate over the emptyZones and
		// check if there is a feasible solution.
		// save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = zone;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			Zone freeZoneTemp = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneTemp.name, freeZoneTemp.amountRasterRow1,
					freeZoneTemp.amountRasterRow2, freeZoneTemp.dimensionTrainStationRow1,
					freeZoneTemp.dimensionTrainStationRow2);
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

	public Information fitMovingNeighbour(Zone zone, Factory factory, int numberNeighbours) {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		// for the ZoneToAllocate given as parameter iterate over the emptyZones.
		// Combine this emptyzone with 1 neigbour.
		// There are 2 possible combinations when moving 1 neighbour.
		// Check if there is a feasible solution.
		// Save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = zone;

		// list with all the combinations of neighbours for each empty Zones

		for (int j = 0; j < factory.getEmptyZones().size(); j++) {

			Zone freeZoneAlone = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneAlone.name, freeZoneAlone.amountRasterRow1,
					freeZoneAlone.amountRasterRow2, freeZoneAlone.dimensionTrainStationRow1,
					freeZoneAlone.dimensionTrainStationRow2);

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

			// ERRORE
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
				if (toAllocate.totalNumberRaster == totalNumberRasterIncludingNeighbours) {
					int cost = calculateCost((EmptyZone) freeZoneAlone, neighboursToTakeIntoConsideration, toAllocate);
					Information information = allocatePerfectFitWithNeighbours(factory, (EmptyZone) freeZoneAlone,
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

	private Information fitWithRest(Zone zone, Factory factory) {

		return null;
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

	// ALLOCATION

	/*
	 * allocationInLargerZone()
	 */
	public Information allocateInLargerZone(Factory factory, EmptyZone emptyZone, Zone toAllocate) {
		Zone[][] factoryStructure = factory.getFactoryStructure();
		Zone[][] tempStructure = new Zone[factoryStructure.length][factoryStructure[0].length + 1];
		int i = emptyZone.locationInFactory[0];
		int j = emptyZone.locationInFactory[1];

		// forse non serve piú
//		// porto tempStructure tutto su null per averlo come counter;
//		for (int j2 = 0; j2 < tempStructure.length; j2++) {
//			for (int k = 0; k < tempStructure[0].length; k++) {
//				tempStructure[j2][k] = null;
//			}
//		}

		// find out what is remaining in the empty zone and what is gone with the
		// allocation of the ZoneToBeAllocated
		int remaindAmountRasterRow1 = emptyZone.amountRasterRow1 - toAllocate.amountRasterRow1;
		int remaindAmountRasterRow2 = emptyZone.amountRasterRow2 - toAllocate.amountRasterRow2;
		int totalDimensionTrainStationRow1 = emptyZone.dimensionTrainStationRow1 + toAllocate.dimensionTrainStationRow1;
		int totalDimensionTrainStationRow2 = emptyZone.dimensionTrainStationRow2 + toAllocate.dimensionTrainStationRow2;

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

		Zone newEmptyZone = new Zone(emptyZone.name + "Empty", newEmptyDimensionTrainStationRow1,
				newEmptyDimensionTrainStationRow2, i, j + 1); // this zone has 0 logistic equipment
		newEmptyZone.amountRasterRow1 = remaindAmountRasterRow1;
		newEmptyZone.amountRasterRow2 = remaindAmountRasterRow2;
		newEmptyZone.dimensionTrainStationRow1 = newEmptyDimensionTrainStationRow1;
		newEmptyZone.dimensionTrainStationRow2 = newEmptyDimensionTrainStationRow2;

		// allocazione della ZoneToBeAllocated
		tempStructure[i][j] = toAllocate;
		tempStructure[i][j + 1] = newEmptyZone;

		// copying the initial factoryStructure into the new tempStructure, that will be
		// given back.
		// int i = row
		boolean passedOver_ToAllocate_and_NewEmptyZone = false;
		for (int it = 0; it < tempStructure.length; it++) {
			if (passedOver_ToAllocate_and_NewEmptyZone == false) {
				if (it != j) {
					tempStructure[i][it] = factoryStructure[i][it];
				} else {
					it++;
					passedOver_ToAllocate_and_NewEmptyZone = true;
				}
			} else {
				tempStructure[i][it] = factoryStructure[i][it - 1];
			}
		}

		factory.setFactoryStructure(tempStructure);
		return new Information(true, factory, 0);
	}

	/*
	 * allocatePerfectFit()
	 */
	public Information allocatePerfectFit(Factory factory, EmptyZone emptyZone, Zone toAllocate) {
		Zone[][] tempStructure = factory.getFactoryStructure();
		int i = emptyZone.locationInFactory[0];
		int j = emptyZone.locationInFactory[1];
		tempStructure[i][j] = toAllocate;
		Factory tempFactory = factory;
		tempFactory.setFactoryStructure(tempStructure);
		return new Information(true, tempFactory, 0);
	}

	/*
	 * allocatePerfectFitWithNeighbours()
	 */
	private Information allocatePerfectFitWithNeighbours(Factory factory, EmptyZone freeZoneAlone,
			ArrayList<Zone> neighboursToTakeIntoConsideration, Zone toAllocate) {

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
		// put in the zone toAllocate in the structure
		modifiedStructure.getFactoryStructure()[row][column] = toAllocate;

		// move remaining zones in the structure to the right spot.
		Zone neighbour = neighboursToTakeIntoConsideration.get(0);
		row = neighbour.locationInFactory[0];
		column = neighbour.locationInFactory[1];
		for (int i = 1; i < 7; i++) {
			if (column - i >= 0) {
				Zone zoneToShift = modifiedStructure.getFactoryStructure()[row][column - i];
				modifiedStructure.getFactoryStructure()[row][column - i] = null;
				for (int j = 0; j < 7; j++) {
					if (modifiedStructure.getFactoryStructure()[row][column - i + j] != null) {
						modifiedStructure.getFactoryStructure()[row][column - i + j - 1] = zoneToShift;
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
			Factory modifiedStructurePot = modifiedStructure;
			boolean alleTrue = true;
			int cost = 0;
			for (int j2 = 0; j2 < length; j2++) {
				int inAddition = j * length;
				int turn = (int) combinations.get(j2 + inAddition);
				Information information = calculate(neighboursToTakeIntoConsideration.get(turn), modifiedStructure);
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

		initial = new Factory();
//		demoFactory(initial);
		demoZonesToAllocate(initial);

		Calculator calculator = new Calculator();
		calculator.performAlgorithm();
//		newFactoryStructure = calculator.performAlgorithm();
		Factory newFactory = initial;
		newFactory.setFactoryStructure(newFactoryStructure);
		// calculateCostBenefits(initial.getFactoryStructure, newFactoryStructure);
	}
}
