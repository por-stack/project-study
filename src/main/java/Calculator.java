import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//cancella

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.xmlbeans.impl.jam.internal.elements.ArrayClassImpl;

public class Calculator {

	/*
	 * input factory to work and list of zones to allocate
	 */
	private static Factory initial;
	private static Zone[][] newFactoryStructure;

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

		ArrayList<Zone> zonesToBeAllocated = initial.getZonesToAllocate();

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
			zonesToBeAllocated = initial.getZonesToAllocate(); // after

			for (int i = 0; i < zonesToBeAllocated.size(); i++) {
				zonesToBeAllocated.get(i).information = calculate(zonesToBeAllocated.get(i), initial);
			}

			// choose the cheapest for this iteration
			// error: guarda se applicable é true
			int i = 0, j = 0;
			Information informationOfBestZone = zonesToBeAllocated.get(i).information;
			for (i = 1; i < zonesToBeAllocated.size(); i++) {
				if (zonesToBeAllocated.get(i).information.costs < informationOfBestZone.costs) {
					informationOfBestZone = zonesToBeAllocated.get(i).information;
					j = i;
				}
			}

			// apply the modifications to real factoryStructure. take zone out of
			// zonesToBeAllocated
			initial.setFactoryStructure(zonesToBeAllocated.get(j).information.modifiedStructure.getFactoryStructure());
			zonesToBeAllocated.remove(j);
		}

		return initial.getFactoryStructure();
	}

	public Information calculate(Zone zone, Factory factory) {
		//cambia ordine:prima withlist poi senza 
		
		Information information; //information (boolean applicable, Zone[][] modifiedStructure, double cost) 
		
		//level 0
		if (zone.equals(initial.getZonesToAllocate().get(0))) {
			information = checkForLargerZone(zone, factory);
			if (information.applicable) 
				return information;
		} 
		
		//level 1
		information = fitPerfectly(zone, factory); 
		if (information.applicable) return information; 
		
//		//level 2
//		information = fitPerfectlyWithList(zone, factory); 
//		if (information.applicable) return information;
		
		//level 3
		information = fitMoving1Neighbour(zone, factory); 
		if (information.applicable) return information;
		
//		//level 4
//		information = fitMoving1NeighbourWithList(); 
//		if (information.applicable) return information;
		
		//level 5
		information = fitMoving2Neighbours(); 
		if (information.applicable) return information;
		
//		//level 6
//		information = fitMoving2NeighboursWithList(); 
//		if (information.applicable) return information;
		
		fitMoving3Neighbours(); 
//		fitMoving3NeighboursWithList();
		fitMoving4Neighbours()
//		fitMoving4NeighboursWithList(); 
		fitMoving5Neighbours(); 
//		fitMoving5NeighboursWithList(); 
		fitMoving6Neighbours(); 
//		fitMoving6NeighboursWithList(); 
		
		return null; 
	}

	/*
	 * The Zone we are looking at
	 */
	public Information checkForLargerZone(Zone zone, Factory factory) {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		Zone toAllocate = zone;
		// falscher ansatz
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			EmptyZone freeZone = (EmptyZone) factory.getEmptyZones().get(j);
			if (toAllocate.totalNumberRaster < freeZone.totalNumberRaster) {
				// metti qui l'algoritmo
				int cost = calculateCost(freeZone, toAllocate);
				Factory modifiedStructure = allocateInLargerZone(factory, freeZone, toAllocate);
				allocationOptions.add(new Information(true, modifiedStructure, cost));
			}
		}

		double cost = allocationOptions.get(0).costs;
		int j = 0;
		for (int i = 1; i < allocationOptions.size(); i++) {
			if (allocationOptions.get(i).costs < cost) {
				cost = allocationOptions.get(i).costs;
				j = i;
			}
		}
		return allocationOptions.get(j);
	}

	public Information fitPerfectly(Zone zone, Factory factory) {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		// for the ZoneToAllocate given as parameter iterate over the emptyZones and
		// check if there is a feasible solution.
		// save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = zone;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			EmptyZone freeZone = (EmptyZone) factory.getEmptyZones().get(j);
			if (toAllocate.totalNumberRaster == freeZone.totalNumberRaster) {
				int cost = calculateCost(freeZone, toAllocate);
				Factory modifiedStructure = allocatePerfectFit(factory, freeZone, toAllocate);
				allocationOptions.add(new Information(true, modifiedStructure, cost));
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

	public Information fitMoving1Neighbour(Zone zone, Factory factory) {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		// for the ZoneToAllocate given as parameter iterate over the emptyZones.
		// Combine this emptyzone with 1 neigbour.
		// There are 2 possible combinations when moving 1 neighbour.
		// Check if there is a feasible solution.
		// Save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = zone;

		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			EmptyZone freeZoneAlone = (EmptyZone) factory.getEmptyZones().get(j);
			// create all combinations between empty zone and neigbours
			// iterate over the empty zone
			int numberNeighbour = 1;
			int locationInFactory[] = freeZoneAlone.locationInFactory;
			
			for (int i = numberNeighbour; i < 0; i--) {
				int left = i;
				int right = numberNeighbour - left; 
				
			}
			
					if (toAllocate.totalNumberRaster == freeZone.totalNumberRaster) {
						int cost = calculateCost(freeZone, toAllocate);
						Factory modifiedStructure = allocatePerfectFit(factory, freeZone, toAllocate);
						allocationOptions.add(new Information(true, modifiedStructure, cost));
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
	 * calculateCost() of allocation
	 */
	public int calculateCost(Zone freeZone, Zone toAllocate) {
		int cost = 0;
		for (int i = 0; i < freeZone.getLogisticEquipment().size(); i++) {
			int freeZoneLE = freeZone.getLogisticEquipment().get(i).anzahl;
			int toAllocateLE = toAllocate.getLogisticEquipment().get(i).anzahl;
			if (freeZoneLE >= toAllocateLE) {
				cost = +freeZoneLE - toAllocateLE;
			} else {
				cost = +toAllocateLE - freeZoneLE;
			}
		}
		return cost;
	}

	/*
	 * allocate()
	 */
	public Factory allocatePerfectFit(Factory factory, EmptyZone emptyZone, Zone toAllocate) {
		Zone[][] tempStructure = factory.getFactoryStructure();
		int i = emptyZone.locationInFactory[0];
		int j = emptyZone.locationInFactory[1];
		tempStructure[i][j] = toAllocate;
		Factory tempFactory = factory;
		tempFactory.setFactoryStructure(tempStructure);
		return tempFactory;
	}

	/*
	 * allocationInLargerZone()
	 */
	public Factory allocateInLargerZone(Factory factory, EmptyZone emptyZone, Zone toAllocate) {
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
		return factory;
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
					System.out.println(factoryStructure[i][6 - j].name);
					System.out.println(Arrays.deepToString(factoryStructure[i][6 - j].raster));
				}
			}
		}
	}

	public static void main(String[] args) throws InvalidFormatException, IOException {
		// Initialize objects and start algorithm

//		Import old = new Import(); 
//		old.demo();

		initial = new Factory();
		demoFactory(initial);

		Calculator calculator = new Calculator();
//		calculator.performAlgorithm(new Factory());
//		newFactoryStructure = calculator.performAlgorithm();
//		Factory newFactory = initial;
//		newFactory.setFactoryStructure(newFactoryStructure);
		// calculateCostBenefits(initial.getFactoryStructure, newFactoryStructure);
	}
}
