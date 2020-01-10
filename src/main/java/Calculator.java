import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Calculator {
	/**
	 * global variables
	 */
	private static Factory initial;
	ArrayList<Integer> combinations = new ArrayList<Integer>();
	private static int cost = 0;

	/**
	 * The initial factory is created with the input data. The factory is analyzed
	 * and a list of the empty zones is created.
	 * 
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public Calculator() throws InvalidFormatException, IOException {
		// exported in main();
	}

	/**
	 * Performalgorithm manages the ZonesToBeAllocated. It puts them individually
	 * into the algorithm, compares the costs and in the right time allocates
	 * definitively the best zoneToBeAllocated
	 * 
	 * @return
	 * @throws Exception
	 */
	public Factory performAlgorithm() throws Exception {
		try {
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
					int toPrint = i + 1;
					System.out.println(" ");
					System.out.println("zonesToBeAllocated nr." + toPrint + ": " + zonesToBeAllocated.get(i).name);
					zonesToBeAllocated.get(i).information = calculate(zonesToBeAllocated.get(i), initial);
					testOfDimensionsOfEmptyZonesVsZonesToAllocate(zonesToBeAllocated.get(i).information);
				}

				if (zonesToBeAllocated.isEmpty()) {
					return initial;
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
				System.out.println(" ");
				System.out.println("--> " + zonesToBeAllocated.get(j).name + " allocated definitively");
				initial = zonesToBeAllocated.get(j).information.modifiedStructure;
				cost += zonesToBeAllocated.get(j).information.costs;
				System.out.println("");
				System.out.println("------------------------------------------------------------");
				demoFactoryShort(initial);
			}

			// the stackoverflow is catched. here the algorithm enters into the third
			// hierarchy.
		} catch (StackOverflowError e) {
			System.out.println("");
			System.out.println("");
			System.out.println("**Algorithm enters third hierarchy**");
			demoFactoryShort(initial);

			// BAUSTELLE
			ArrayList<Zone> listToAllocate = initial.getZonesToAllocate();
			ArrayList<EmptyZone> listEmptyZones = initial.getEmptyZones();

			for (int i = 0; i < listToAllocate.size(); i++) {
				for (int j = 0; j < listEmptyZones.size(); j++) {
					Zone toAllocate = listToAllocate.get(i);

					EmptyZone emptyZone = listEmptyZones.get(j);

					// preparation
					Factory factory = copyFactory(initial);
					Zone[][] factoryStructure = factory.getFactoryStructure();
					int iPos = emptyZone.locationInFactory[0];
					int jPos = emptyZone.locationInFactory[1];

					// if the size is the same, allocate
					// todo

					// calculate total dimension of train station for emptyZone and zoneToAllocate
					int dimensionTrainStationEmptyZone = emptyZone.getDimensionTrainStationRow1()
							+ emptyZone.getDimensionTrainStationRow2();
					int dimensionTrainStationZoneToAllocate = toAllocate.getDimensionTrainStationRow1()
							+ toAllocate.getDimensionTrainStationRow2();
					// calculate the difference: largeer - smaller
					int differenceTrainStation = dimensionTrainStationZoneToAllocate - dimensionTrainStationEmptyZone;

					// calculate the total amount of raster (without train station) for the
					// emptyZone and for the zoneToAllocate
					int amountOfRasterRow1AndRow2EmptyZone = emptyZone.amountRasterRow1 + emptyZone.amountRasterRow2;
					int amountOfRasterRow1AndRow2ZoneToAllocate = toAllocate.amountRasterRow1
							+ toAllocate.amountRasterRow2;
					// calculate the difference: larger - smaller
					int differenceRaster = amountOfRasterRow1AndRow2ZoneToAllocate - amountOfRasterRow1AndRow2EmptyZone;

					// Because of the fact that the
					// toAllocate is larger than the emptyZone, some raster of the toAllocate
					// remain and are added to the newZoneToAllocate.
					// If the difference of train stations is negative, the emptyZone has a
					// larger trainStation than the zoneToAllocate. Some of the reamining rasters
					// are
					// used as train station.
					boolean noTrainStationInNewZone = false;
					if (differenceTrainStation < 0) {
						noTrainStationInNewZone = true;
						differenceRaster += differenceTrainStation;
					}

					Zone newToAllocate = new Zone(toAllocate.name + "Splitted", 0, 0, iPos, jPos);

					// difference of trainStation is positive
					if (!noTrainStationInNewZone) {
						// difference of trainStations is even (0 included)
						if (differenceTrainStation % 2 == 0) {
							newToAllocate.dimensionTrainStationRow1 = differenceTrainStation / 2;
							newToAllocate.dimensionTrainStationRow2 = differenceTrainStation / 2;

							// difference of rasters is even
							if (differenceRaster % 2 == 0) {
								newToAllocate.amountRasterRow1 = differenceRaster / 2;
								newToAllocate.amountRasterRow2 = differenceRaster / 2;
							}
							// difference of rasters is odd
							else {
								newToAllocate.amountRasterRow1 = differenceRaster / 2 + 1;
								newToAllocate.amountRasterRow2 = differenceRaster / 2;
							}

						}
						// difference of train station is odd
						else {
							newToAllocate.dimensionTrainStationRow1 = differenceTrainStation / 2 + 1;
							newToAllocate.dimensionTrainStationRow2 = differenceTrainStation / 2;

							if (differenceRaster % 2 == 0) {

								newToAllocate.amountRasterRow1 = differenceRaster / 2;
								newToAllocate.amountRasterRow2 = differenceRaster / 2;

							} else {
								newToAllocate.amountRasterRow1 = differenceRaster / 2 + 1;
								newToAllocate.amountRasterRow2 = differenceRaster / 2;
							}
						}
					}
					// amount of trainStation is negative. The newZoneToAllocate will have no
					// trainStation
					else {
						if (differenceRaster % 2 == 0) {

							newToAllocate.amountRasterRow1 = differenceRaster / 2;
							newToAllocate.amountRasterRow2 = differenceRaster / 2;
						} else {
							newToAllocate.amountRasterRow1 = differenceRaster / 2 + 1;
							newToAllocate.amountRasterRow2 = differenceRaster / 2;
						}
					}

					// difference of trainStation is positive
					if (!noTrainStationInNewZone) {
						// difference of trainStations is even (0 included)
						if (differenceTrainStation % 2 == 0) {
							newToAllocate.dimensionTrainStationRow1 = differenceTrainStation / 2;
							newToAllocate.dimensionTrainStationRow2 = differenceTrainStation / 2;

							// difference of rasters is even
							if (differenceRaster % 2 == 0) {
								newToAllocate.amountRasterRow1 = differenceRaster / 2;
								newToAllocate.amountRasterRow2 = differenceRaster / 2;
							}
							// difference of rasters is odd
							else {
								newToAllocate.amountRasterRow1 = differenceRaster / 2 + 1;
								newToAllocate.amountRasterRow2 = differenceRaster / 2;
							}

						}
						// difference of train station is odd
						else {
							newToAllocate.dimensionTrainStationRow1 = differenceTrainStation / 2 + 1;
							newToAllocate.dimensionTrainStationRow2 = differenceTrainStation / 2;

							if (differenceRaster % 2 == 0) {

								newToAllocate.amountRasterRow1 = differenceRaster / 2;
								newToAllocate.amountRasterRow2 = differenceRaster / 2;

							} else {
								newToAllocate.amountRasterRow1 = differenceRaster / 2 + 1;
								newToAllocate.amountRasterRow2 = differenceRaster / 2;
							}
						}
					}
					// amount of trainStation is negative. The newEmptyZone will have no
					// trainStation
					else {
						if (differenceRaster % 2 == 0) {

							newToAllocate.amountRasterRow1 = differenceRaster / 2;
							newToAllocate.amountRasterRow2 = differenceRaster / 2;
						} else {
							newToAllocate.amountRasterRow1 = differenceRaster / 2 + 1;
							newToAllocate.amountRasterRow2 = differenceRaster / 2;
						}
					}

					// calculate the amount of rasters and train stations and set zone as empty
					newToAllocate.calculateAmounts();
					newToAllocate.setEmpty(false);
					// add logistic equipment to the newEmptyZone
					newToAllocate.setLogisticEquipment(updateLogisticEquipmentNewEmptyZone(
							toAllocate.getLogisticEquipment(), emptyZone.getLogisticEquipment(),
							newToAllocate.getLogisticEquipment(), differenceRaster));

					// find out what is remaining in the empty zone and what is gone with the
					// allocation of the ZoneToBeAllocated

					// Copying the emptyZone in order to not modify the original.
					String name = toAllocate.name + "Splitted";
					Zone emptyZoneToReturn = new Zone(name, emptyZone.amountRasterRow1, emptyZone.amountRasterRow2,
							iPos, jPos);
					emptyZoneToReturn.dimensionTrainStationRow1 = emptyZone.dimensionTrainStationRow1;
					emptyZoneToReturn.dimensionTrainStationRow2 = emptyZone.dimensionTrainStationRow2;
					emptyZoneToReturn.calculateAmounts();
					emptyZoneToReturn.setEmpty(toAllocate.isEmpty());
					// directly copied, as never changed
					emptyZoneToReturn.setLogisticEquipment(emptyZone.getLogisticEquipment());

					// allocazione della ZoneToBeAllocated
					factoryStructure[iPos][jPos] = emptyZoneToReturn;
					
					// PENALTY FOR SPLITTING A ZONE
					cost += 50;

					initial.setFactoryStructure(factoryStructure);
					initial.setEmptyZones(initial.createEmptyZones(initial.getFactoryStructure()));

					for (int i1 = 0; i1 < factory.getZonesToAllocate().size(); i1++) {
						if (factory.getZonesToAllocate().get(i1).name.equals(toAllocate.name)) {
							factory.getZonesToAllocate().set(i1, newToAllocate);
						}
					}
				}
			}
			initial.getZonesToAllocate().remove(0);
		}

		// BAUSTELLE
		System.out.println("");
		System.out.println();
		demoFactoryShort(initial);
		return initial;
	}

	/**
	 * calculate contains the first and second hierarchy. each level has a seperate
	 * method which will be called when the level is entered
	 * 
	 * @param zone
	 * @param factoryAsParameter
	 * @return
	 * @throws Exception
	 */
	public Information calculate(Zone zone, Factory factoryAsParameter) throws Exception {
		if (zone.isEmpty()) {
			return new Information(true, factoryAsParameter, 0);
		}
		System.out.println("**" + zone.name + " enters in first hierarchy");
		Information information; // information (boolean applicable, Zone[][] modifiedStructure, double cost)
		Factory factory = copyFactory(factoryAsParameter);
		// FIRST HIERARCHY BASED ON PERFECT FIT

		// level 0: Edge-case: checkForLargerZone
		if (zone.equals(initial.getZonesToAllocate().get(0))) {
			information = checkForLargerZone(zone, factory);
			if (information.applicable) {
				System.out.println("solution for " + zone.name + " found in level checkForLargerZone");
				information.costs = -1.0;
				return information;
			}
		}

		// level 1: fitPerfectly
		information = fitPerfectly(zone, factory);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitPerfectly ");
			return information;
		}

		// level 2: fitMoving1Neighbour
		information = fitMovingNeighbour(zone, factory, 1);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbour: 1");
			return information;
		}

		// level 3: fitMoving2Neighbour
		information = fitMovingNeighbour(zone, factory, 2);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbour: 2");
			return information;
		}

		// level 4: fitMoving3Neighbours
		information = fitMovingNeighbour(zone, factory, 3);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbour: 3");
			return information;
		}

		// level 5: fitMoving4Neighbour
		information = fitMovingNeighbour(zone, factory, 4);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbour: 4");
			return information;
		}

		// level 6: fitMoving5Neighbours
		information = fitMovingNeighbour(zone, factory, 5);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbour: 5");
			return information;
		}

		// level 7: fitMoving6Neighbours
		information = fitMovingNeighbour(zone, factory, 6);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbour: 6");
			return information;
		}

		// SECOND HIERARCHY BASED ON FINDING A BIGGER ZONE. "WITH REST".
		System.out.println("**" + zone.name + " enters in second hierarchy");

		// level 1: fitWithRest()
		information = fitWithRest(zone, factory);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in fitWithRest");
			return information;
		}

		// level 2: fitMoving1NeighbourWithRest()
		information = fitMovingNeighbourWithRest(zone, factory, 1);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbourWithRest: 1 ");
			return information;
		}

		// level 3: fitMoving2eighbourWithRest()
		information = fitMovingNeighbourWithRest(zone, factory, 2);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbourWithRest: 2 ");
			return information;
		}

		// level 4: fitMoving3NeighbourWithRest()
		information = fitMovingNeighbourWithRest(zone, factory, 3);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbourWithRest: 3 ");
			return information;
		}

		// level 5: fitMoving4NeighbourWithRest()
		information = fitMovingNeighbourWithRest(zone, factory, 4);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbourWithRest: 4 ");
			return information;
		}

		// level 6: fitMoving5NeighbourWithRest
		information = fitMovingNeighbourWithRest(zone, factory, 5);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbourWithRest: 5 ");
			return information;
		}

		// level 7: fitMoving6NeighbourWithRest()
		information = fitMovingNeighbourWithRest(zone, factory, 6);
		if (information.applicable) {
			System.out.println("solution for " + zone.name + " found in level fitMovingNeighbourWithRest: 6");
			return information;
		}

		System.out.println("no solution found");
		return new Information(true, factory, 0);
	}

	/**
	 * CheckForLargerZone()n
	 * 
	 * @param zone
	 * @param factoryAsParameter
	 * @return
	 * @throws Exception
	 */
	public Information checkForLargerZone(Zone zone, Factory factoryAsParameter) throws Exception {
		System.out.println(zone.name + " enters checkForLargerZone");
		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Factory factory = copyFactory(factoryAsParameter);

		Zone toAllocate = zone;
		int counter = 1;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			Zone freeZoneTemp = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneTemp.name, freeZoneTemp.amountRasterRow1,
					freeZoneTemp.amountRasterRow2, freeZoneTemp.locationInFactory[0],
					freeZoneTemp.locationInFactory[1]);
			freeZone.setDimensionTrainStationRow1(freeZoneTemp.dimensionTrainStationRow1);
			freeZone.setDimensionTrainStationRow2(freeZoneTemp.dimensionTrainStationRow2);
			freeZone.setLogisticEquipment(freeZoneTemp.getLogisticEquipment());
			freeZone.calculateAmounts();
			if (toAllocate.totalNumberRaster < freeZone.totalNumberRaster) {
				System.out.println(zone.name + " in level checkForLargerZone is looking at freeZone " + freeZone.name
						+ ". (option nr. " + counter + ")");
				counter++;
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
			System.out.println(zone.name + " in level checkForLargerZone chooses its option nr.1");
			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
		} else {
			// create an array out of the list
			Information[] allocationOptionsArray = new Information[allocationOptions.size()];
			for (int i = 0; i < allocationOptionsArray.length; i++) {
				allocationOptionsArray[i] = allocationOptions.get(i);
			}
			counter = 0;
			double minCost = allocationOptionsArray[counter].costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > allocationOptionsArray[counter].costs) {
					minCost = allocationOptionsArray[counter].costs;
					counter = j;
				}
			}
			int output = counter + 1;
			System.out.println(zone.name + " in level checkForLargerZone chooses its option nr." + output);
			return new Information(true, allocationOptions.get(counter).modifiedStructure,
					allocationOptions.get(counter).costs);
		}
	}

	/**
	 * fitPerfectly()
	 * 
	 * @param zone
	 * @param factoryAsParameter
	 * @return
	 * @throws Exception
	 */
	public Information fitPerfectly(Zone zone, Factory factoryAsParameter) throws Exception {
		System.out.println(zone.name + " enters fitPerfectly");
		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Factory factory = copyFactory(factoryAsParameter);

		// for the ZoneToAllocate given as parameter iterate over the emptyZones and
		// check if there is a feasible solution.
		// save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = zone;
		int counter = 1;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			// copy the empty zone j into the variable freeZone
			Zone freeZoneTemp = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneTemp.name, freeZoneTemp.amountRasterRow1,
					freeZoneTemp.amountRasterRow2, freeZoneTemp.locationInFactory[0],
					freeZoneTemp.locationInFactory[1]);
			freeZone.setDimensionTrainStationRow1(freeZoneTemp.dimensionTrainStationRow1);
			freeZone.setDimensionTrainStationRow2(freeZoneTemp.dimensionTrainStationRow2);
			freeZone.setLogisticEquipment(freeZoneTemp.getLogisticEquipment());
			freeZone.calculateAmounts();

			// check if the zone to allocate fits perfectly
			if (toAllocate.totalNumberRaster == freeZone.totalNumberRaster) {
				System.out.println(zone.name + " in level fitPerfectly is looking at freeZone " + freeZone.name
						+ ". (option nr. " + counter + ")");
				counter++;
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
			System.out.println(zone.name + " in level fitPerfectly chooses its option nr.1");
			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
		} else {
			Information[] allocationOptionsArray = new Information[allocationOptions.size()];
			for (int i = 0; i < allocationOptionsArray.length; i++) {
				allocationOptionsArray[i] = allocationOptions.get(i);
			}
			counter = 0;
			double minCost = allocationOptionsArray[counter].costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > allocationOptionsArray[counter].costs) {
					minCost = allocationOptionsArray[counter].costs;
					counter = j;
				}
			}
			int output = counter + 1;
			System.out.println(zone.name + " in level fitPerfectly chooses its option nr." + output);
			return new Information(true, allocationOptions.get(counter).modifiedStructure,
					allocationOptions.get(counter).costs);
		}
	}

	/**
	 * fitMovingNeighbour: this method is called for every case of neighbours
	 * between 1 and 6. Dynamically the method will adapt to the number of
	 * neihgbours and execute the task correctly
	 * 
	 * @param zone
	 * @param factoryAsParameter
	 * @param numberNeighbours
	 * @return
	 * @throws Exception
	 */
	public Information fitMovingNeighbour(Zone zone, Factory factoryAsParameter, int numberNeighbours)
			throws Exception {
		System.out.println(zone.name + " enters fitMovingNeighbour: " + numberNeighbours);
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
		toAllocate.setLogisticEquipment(zone.getLogisticEquipment());
		toAllocate.calculateAmounts();
		toAllocate.setEmpty(zone.isEmpty());

		// list with all the combinations of neighbours for each empty Zones
		int counter = 1; // just needed for the system.out.println
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			Zone freeZoneAlone = factory.getEmptyZones().get(j);
			// copy the empty zone into the variable freeZone
			EmptyZone freeZone = new EmptyZone(freeZoneAlone.name, freeZoneAlone.amountRasterRow1,
					freeZoneAlone.amountRasterRow2, freeZoneAlone.locationInFactory[0],
					freeZoneAlone.locationInFactory[1]);
			freeZone.setDimensionTrainStationRow1(freeZoneAlone.dimensionTrainStationRow1);
			freeZone.setDimensionTrainStationRow2(freeZoneAlone.dimensionTrainStationRow2);
			freeZone.setLogisticEquipment(freeZoneAlone.getLogisticEquipment());
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
					int output = i + 1;
					System.out.println(
							zone.name + "in level fitMovingNeighbour " + numberNeighbours + " is looking at option nr."
									+ counter + " (freeZone nr. " + j + ", combination nr. " + output + ")");
					int cost = calculateCost((EmptyZone) freeZoneAlone, neighboursToTakeIntoConsideration, toAllocate);
					Factory toReturn = copyFactory(factoryAsParameter);
					toReturn.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn] = null;
					// set the emptyZone on null;
					System.out.print(zone.name + " --> ");
					Information information = allocatePerfectFitWithNeighbours(toReturn, (EmptyZone) freeZoneAlone,
							neighboursToTakeIntoConsideration, toAllocate);
					information.costs += cost;
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
			System.out.println(
					zone.name + " in level fitMovingNeighbour " + numberNeighbours + " chooses its option nr.1");
			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
		} else {
			Object[] allocationOptionsArray = allocationOptions.toArray();
			counter = 0;
			double minCost = ((Information) allocationOptionsArray[counter]).costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > ((Information) allocationOptionsArray[counter]).costs) {
					minCost = ((Information) allocationOptionsArray[counter]).costs;
					counter = j;
				}
			}
			int output = counter + 1;
			System.out.println(
					zone.name + "in level fitMovingNeighbour " + numberNeighbours + "chooses his option nr." + output);
			return new Information(true, allocationOptions.get(counter).modifiedStructure,
					allocationOptions.get(counter).costs);

		}
	}

	/**
	 * fitWithRest: is mainly the same as checkForLargerZone. Thus the workload is
	 * shifted and the method checkForLargerZone is called
	 * 
	 * @param zone
	 * @param factoryAsParameter
	 * @return
	 * @throws Exception
	 */
	private Information fitWithRest(Zone zone, Factory factoryAsParameter) throws Exception {
		System.out.println(zone.name + " enters fitWithRest");
		Factory factory = copyFactory(factoryAsParameter);
		Information toReturn = checkForLargerZone(zone, factory);
		System.out.println(zone.name + " found a solution in fitWithRest");
		return toReturn;
	}

	/**
	 * fitMovingNeighbourWithRest()
	 * 
	 * @param zone
	 * @param factoryAsParameter
	 * @param numberNeighbours
	 * @return
	 * @throws Exception
	 */
	private Information fitMovingNeighbourWithRest(Zone zone, Factory factoryAsParameter, int numberNeighbours)
			throws Exception {
		System.out.println(zone.name + " enters fitMovingNeighbourWithRest: " + numberNeighbours);
		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Factory factory = copyFactory(factoryAsParameter);

		/*
		 * for the ZoneToAllocate given as parameter iterate over the emptyZones.
		 * Combine this emptyzone with 1 neigbour. There are "numberNeighbours" possible
		 * combinations when moving 1 neighbour. Check if there is a feasible solution.
		 * Save information (applicable, modifiedstructure, cost) for every feasible
		 * solution
		 */
		Zone toAllocate = zone;

		// list with all the combinations of neighbours for each empty Zones
		int counter = 1; // just needed for the system.out.println
		outer: for (int j = 0; j < factory.getEmptyZones().size(); j++) {

			Zone freeZoneAlone = factory.getEmptyZones().get(j);
			EmptyZone freeZone = new EmptyZone(freeZoneAlone.name, freeZoneAlone.amountRasterRow1,
					freeZoneAlone.amountRasterRow2, freeZoneAlone.locationInFactory[0],
					freeZoneAlone.locationInFactory[1]);
			freeZone.setDimensionTrainStationRow1(freeZoneAlone.dimensionTrainStationRow1);
			freeZone.setDimensionTrainStationRow2(freeZoneAlone.dimensionTrainStationRow2);
			freeZone.setLogisticEquipment(freeZoneAlone.getLogisticEquipment());
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

			for (int i = numberNeighbour; i >= 0; i--) { // CAMBIATO = con >=
				int right = i;
				int left = numberNeighbour - right;

				while (right > 0) {
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
				if (toAllocate.totalNumberRaster < totalNumberRasterIncludingNeighbours) {

					if (toAllocate.locationInFactory[1] == freeZoneAlone.locationInFactory[1]) {
						continue outer;
					}

					int output = i + 1;
					System.out.println(
							zone.name + "in level fitMovingNeighbour " + numberNeighbours + " is looking at option nr."
									+ counter + " (freeZone nr. " + j + ", combination nr." + output);
					int cost = calculateCost((EmptyZone) freeZoneAlone, neighboursToTakeIntoConsideration, toAllocate);
					Factory toReturn = copyFactory(factory);
					toReturn.getFactoryStructure()[locationInFactoryRow][locationInFactoryColumn] = null; // set the
																											// emptyZone
																											// on null;
					System.out.print(zone.name + " --> ");
					Information information = allocateInLargerZoneWithNeighbours(toReturn, (EmptyZone) freeZoneAlone,
							neighboursToTakeIntoConsideration, toAllocate);
					information.costs += cost;
					allocationOptions.add(information);
					break outer;
				}
			}
		}

		// check if there is any feasible solution.
		// If there is more than one, chosse the cheapest allocation.
		if (allocationOptions.size() == 0) {
			// qui ce da vedere se e quando usare la seconda hirarchy
			return new Information(false, null, 0);
		} else if (allocationOptions.size() == 1) {
			System.out.println(zone.name + " in level fitMovingNeighbourWithRest " + numberNeighbours
					+ " chooses its option nr.1");
			return new Information(true, allocationOptions.get(0).modifiedStructure, allocationOptions.get(0).costs);
		} else {
			Object[] allocationOptionsArray = allocationOptions.toArray();
			counter = 0;
			double minCost = ((Information) allocationOptionsArray[counter]).costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > ((Information) allocationOptionsArray[counter]).costs) {
					minCost = ((Information) allocationOptionsArray[counter]).costs;
					counter = j;
				}
			}
			int output = counter + 1;
			System.out.println(zone.name + "in level fitMovingNeighbourWithRest " + numberNeighbours
					+ "chooses his option nr." + output);
			return new Information(true, allocationOptions.get(counter).modifiedStructure,
					allocationOptions.get(counter).costs);
		}

	}

	// COST

	/**
	 * calculateCost() of allocation of a zone into an empty zone
	 * 
	 * @param freeZone
	 * @param toAllocate
	 * @return
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

	/**
	 * calculateCost() of allocation of a zone into an empty zone + neighbours
	 *
	 * @param freeZoneAlone
	 * @param neighboursToTakeIntoConsideration
	 * @param toAllocate
	 * @return
	 */
	private int calculateCost(EmptyZone freeZoneAlone, ArrayList<Zone> neighboursToTakeIntoConsideration,
			Zone toAllocate) {
		int cost = 0;
		for (int i = 0; i < freeZoneAlone.getLogisticEquipment().size(); i++) {
			int freeZoneLE = freeZoneAlone.getLogisticEquipment().get(i).anzahl;
			for (int j = 0; j < neighboursToTakeIntoConsideration.size(); j++) {
				freeZoneLE += neighboursToTakeIntoConsideration.get(j).getLogisticEquipment().get(i).anzahl;
			}
			int toAllocateLE = toAllocate.getLogisticEquipment().get(i).anzahl;
			if (freeZoneLE >= toAllocateLE) {
				cost += freeZoneLE - toAllocateLE;
			} else {
				cost += toAllocateLE - freeZoneLE;
			}
		}
		return cost;
	}

	// ALLOCATION

	/**
	 * allocationInLargerZone()
	 * 
	 * @param factoryAsParamter
	 * @param emptyZone
	 * @param toAllocate
	 * @return
	 * @throws Exception
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

		Zone newEmptyZone = new Zone(emptyZone.name + "Empty", 0, 0, iPos, jPos + 1);

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

		// Copying the zoneToAllocate in order to not modify the original.
		Zone toAllocateToReturn = new Zone(toAllocate.name, toAllocate.amountRasterRow1, toAllocate.amountRasterRow2,
				iPos, jPos);
		toAllocateToReturn.dimensionTrainStationRow1 = toAllocate.dimensionTrainStationRow1;
		toAllocateToReturn.dimensionTrainStationRow2 = toAllocate.dimensionTrainStationRow2;
		toAllocateToReturn.calculateAmounts();
		toAllocateToReturn.setEmpty(toAllocate.isEmpty());
		// directly copied, as never changed
		toAllocateToReturn.setLogisticEquipment(toAllocate.getLogisticEquipment());

		// allocazione della ZoneToBeAllocated
		tempStructure[iPos][jPos] = toAllocateToReturn;
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
								singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
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
							// the zones on the right of the just allocated zoneToAllocate, more precisely
							// the zones on the right of the newEmptyZone, must consider the fact that one
							// additional zone has entered the structure when handling their
							// locationInFactory. Therefore the +1.
							Zone singleZoneToReturn = new Zone(singleZoneToCopy.name, singleZoneToCopy.amountRasterRow1,
									singleZoneToCopy.amountRasterRow2, singleZoneToCopy.locationInFactory[0],
									singleZoneToCopy.locationInFactory[1] + 1);
							singleZoneToReturn.dimensionTrainStationRow1 = singleZoneToCopy.dimensionTrainStationRow1;
							singleZoneToReturn.dimensionTrainStationRow2 = singleZoneToCopy.dimensionTrainStationRow2;
							singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
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
								singleZoneToCopy.amountRasterRow2, singleZoneToCopy.locationInFactory[0], column);
						singleZoneToReturn.dimensionTrainStationRow1 = singleZoneToCopy.dimensionTrainStationRow1;
						singleZoneToReturn.dimensionTrainStationRow2 = singleZoneToCopy.dimensionTrainStationRow2;
						singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
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

	/**
	 * allocatePerfectFit()
	 * 
	 * @param factoryAsParameter
	 * @param emptyZone
	 * @param toAllocate
	 * @return
	 * @throws Exception
	 */
	public Information allocatePerfectFit(Factory factoryAsParameter, EmptyZone emptyZone, Zone toAllocate)
			throws Exception {
		Factory factory = copyFactory(factoryAsParameter);

		Zone[][] tempStructure = factory.getFactoryStructure();
		int i = emptyZone.locationInFactory[0];
		int j = emptyZone.locationInFactory[1];

		Zone singleZoneToReturn = new Zone(toAllocate.name, toAllocate.amountRasterRow1, toAllocate.amountRasterRow2, i,
				j);
		singleZoneToReturn.dimensionTrainStationRow1 = toAllocate.dimensionTrainStationRow1;
		singleZoneToReturn.dimensionTrainStationRow2 = toAllocate.dimensionTrainStationRow2;
		singleZoneToReturn.setLogisticEquipment(toAllocate.getLogisticEquipment());
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

	/**
	 * allocatePerfectFitWithNeighbours()
	 * 
	 * @param factoryAsParameter
	 * @param freeZoneAlone
	 * @param neighboursToTakeIntoConsideration
	 * @param toAllocate
	 * @return
	 * @throws Exception
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
		singleZoneToReturn.setLogisticEquipment(toAllocate.getLogisticEquipment());
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
		// allocation singleZoneToReturn. Setting of locationInFactory of the
		// zoneToAllocate.
		if (min1RightNeihgbourExists) {
			singleZoneToReturn.locationInFactory[0] = rowRight;
			singleZoneToReturn.locationInFactory[1] = columnRight;
			modifiedStructure.getFactoryStructure()[rowRight][columnRight] = singleZoneToReturn;
		} else {
			singleZoneToReturn.locationInFactory[0] = rowEmpty;
			singleZoneToReturn.locationInFactory[1] = columnEmpty;
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

		// check at which position of the row to start moving the zones on the right. If
		// some neighbors where moved from the left of the empty zones, one starts
		// moving zones departing from the first zone on the left of the neigbors. If
		// no neighbor was moved from the left, one starts directly from the first zone
		// on the left of the empty zone.
		if (min1LeftNeihgbourExists) {
			for (int i = 0; i < modifiedStructure.getFactoryStructure()[0].length; i++) {
				if (columnLeft - 1 - i >= 0) {
					Zone zoneToBeMoved = modifiedStructure.getFactoryStructure()[rowLeft][columnLeft - 1 - i];
					modifiedStructure.getFactoryStructure()[rowLeft][columnLeft - 1 - i] = null;
					if (zoneToBeMoved != null) {
						zoneToBeMoved.locationInFactory[1] = columnLeft - 1 - i
								+ neighboursToTakeIntoConsideration.size();
					}
					modifiedStructure.getFactoryStructure()[rowLeft][columnLeft - 1 - i
							+ neighboursToTakeIntoConsideration.size()] = zoneToBeMoved;
				}
			}
		} else {
			for (int i = 0; i < modifiedStructure.getFactoryStructure()[0].length; i++) {
				if (columnEmpty - 1 - i >= 0) {
					Zone zoneToBeMoved = modifiedStructure.getFactoryStructure()[rowEmpty][columnEmpty - 1 - i];
					modifiedStructure.getFactoryStructure()[rowEmpty][columnEmpty - 1 - i] = null;
					if (zoneToBeMoved != null) {
						zoneToBeMoved.locationInFactory[1] = columnEmpty - 1 - i
								+ neighboursToTakeIntoConsideration.size();
					}
					modifiedStructure.getFactoryStructure()[rowEmpty][columnEmpty - 1 - i
							+ neighboursToTakeIntoConsideration.size()] = zoneToBeMoved;
				}
			}
		}

		// next part: algorithm that creates all combinations of permutations.
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
			modifiedStructurePot
					.setEmptyZones(modifiedStructurePot.createEmptyZones(modifiedStructurePot.getFactoryStructure()));
			for (int i = 0; i < modifiedStructurePot.getZonesToAllocate().size(); i++) {
				if (modifiedStructurePot.getZonesToAllocate().get(i).name.equals(toAllocate.name)) {
					modifiedStructurePot.getZonesToAllocate().remove(i);
				}
			}
			boolean alleTrue = true;
			int cost = 0;
			for (int j2 = 0; j2 < length; j2++) {
				int inAddition = j * length;
				int turn = (int) combinations.get(j2 + inAddition) - 1;
				System.out.println(neighboursToTakeIntoConsideration.get(turn).name);
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
			Object[] allocationOptionsArray = allocationOptions.toArray();
			int counter = 0;
			double minCost = ((Information) allocationOptionsArray[counter]).costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > ((Information) allocationOptionsArray[counter]).costs) {
					minCost = ((Information) allocationOptionsArray[counter]).costs;
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

	/**
	 * allocateInLargerZoneWithNeighbours()
	 * 
	 * @param factoryAsParameter
	 * @param freeZoneAlone
	 * @param neighboursToTakeIntoConsideration
	 * @param toAllocate
	 * @return
	 * @throws Exception
	 */
	private Information allocateInLargerZoneWithNeighbours(Factory factoryAsParameter, EmptyZone freeZoneAlone,
			ArrayList<Zone> neighboursToTakeIntoConsideration, Zone toAllocate) throws Exception {
		Factory factory = copyFactory(factoryAsParameter);

		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Zone[][] factoryStructure = factory.getFactoryStructure();
		int iPos = freeZoneAlone.locationInFactory[0];
		int jPos = freeZoneAlone.locationInFactory[1];

		// put the zones that are taken out on null and count dimension of neighbours
		int row = 0;
		int column = 0;
		int totalNumberRasterMovedNeighbours = 0;
		for (int i = 0; i < neighboursToTakeIntoConsideration.size(); i++) {
			Zone neighbour = neighboursToTakeIntoConsideration.get(i);
			row = neighbour.locationInFactory[0];
			column = neighbour.locationInFactory[1];
			factoryStructure[row][column] = null;
			totalNumberRasterMovedNeighbours += neighboursToTakeIntoConsideration.get(i).totalNumberRaster;
		}

		String name = neighboursToTakeIntoConsideration.get(0).name;
		// The newEmptyZone for this level represents the "rest" and will be a
		// completely empty zone: the neighbour-zone is moved and no logistic equipment
		// remains
		Zone newEmptyZone = new Zone("RestOf" + name, 0, 0, row, column); // this zone has 0 logistic equipment
		int difference = freeZoneAlone.totalNumberRaster + totalNumberRasterMovedNeighbours
				- toAllocate.totalNumberRaster;
		if (difference % 2 == 0) {
			newEmptyZone.amountRasterRow1 = difference / 2;
			newEmptyZone.amountRasterRow2 = difference / 2;
		} else {
			newEmptyZone.amountRasterRow1 = difference / 2 + 1;
			newEmptyZone.amountRasterRow2 = difference / 2;
		}
		newEmptyZone.calculateAmounts();
		newEmptyZone.setEmpty(true);

		Zone toAllocateToReturn = new Zone(toAllocate.name, toAllocate.amountRasterRow1, toAllocate.amountRasterRow2,
				iPos, jPos);
		toAllocateToReturn.dimensionTrainStationRow1 = toAllocate.dimensionTrainStationRow1;
		toAllocateToReturn.dimensionTrainStationRow2 = toAllocate.dimensionTrainStationRow2;
		toAllocateToReturn.calculateAmounts();
		toAllocateToReturn.setEmpty(toAllocate.isEmpty());
		// directly copied, as never changed
		toAllocateToReturn.setLogisticEquipment(toAllocate.getLogisticEquipment());

		// put in the newEmptyzone and the zoneToAllocate in the structure. The
		// newEmptyZone representing the "rest" space remaining will replace the moving
		// neighbour in the structure, whereas the zoneToAllocate will replace the
		// emptyZone
		factoryStructure[row][column] = newEmptyZone;
		factoryStructure[row][jPos] = toAllocateToReturn;

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
			Factory modifiedStructurePot = copyFactory(factory);

			modifiedStructurePot
					.setEmptyZones(modifiedStructurePot.createEmptyZones(modifiedStructurePot.getFactoryStructure()));
			for (int i = 0; i < modifiedStructurePot.getZonesToAllocate().size(); i++) {
				if (modifiedStructurePot.getZonesToAllocate().get(i).name.equals(toAllocate.name)) {
					modifiedStructurePot.getZonesToAllocate().remove(i);
				}
			}
			boolean alleTrue = true;
			int cost = 0;
			for (int j2 = 0; j2 < length; j2++) {
				int inAddition = j * length;
				int turn = (int) combinations.get(j2 + inAddition) - 1;
				System.out.println(neighboursToTakeIntoConsideration.get(turn).name);
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
			toReturn.setEmptyZones(factory.createEmptyZones(factory.getFactoryStructure()));
			for (int i = 0; i < factory.getZonesToAllocate().size(); i++) {
				if (factory.getZonesToAllocate().get(i).name.equals(toAllocate.name)) {
					factory.getZonesToAllocate().remove(i);
				}
			}
			return new Information(true, toReturn, costs);
		} else {
			Object[] allocationOptionsArray = allocationOptions.toArray();
			int counter = 0;
			double minCost = ((Information) allocationOptionsArray[counter]).costs;
			for (int j = counter; j < allocationOptions.size(); j++) {
				if (minCost > ((Information) allocationOptionsArray[counter]).costs) {
					minCost = ((Information) allocationOptionsArray[counter]).costs;
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

	/**
	 * combinations(): auxilliary method, that creates all permutations of numbers
	 * 1, 2, ... , array.length.
	 * 
	 * @param array
	 * @param length
	 * @param depth
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

	/**
	 * copyFactory: auxilliary method to get loose of the reference on objects, we
	 * wanted to copy and change, without changing the initial object
	 * 
	 * @param factoryAsParameter
	 * @return
	 */
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
					singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
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
			singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
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
			singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
			singleZoneToReturn.calculateAmounts();
			singleZoneToReturn.setEmpty(singleZoneToCopy.isEmpty());
			// directly copied, as never changed
			singleZoneToReturn.setLogisticEquipment(singleZoneToCopy.getLogisticEquipment());
			listToReturnAllocate.add(singleZoneToReturn);
		}
		toReturn.setZonesToAllocate(listToReturnAllocate);

		return toReturn;
	}

	/**
	 * creates a copy of a matrix array //va cambiata la syntax. in parte presa da
	 * internet
	 * 
	 * @param factory
	 * @return
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

	/**
	 * returns true if the difference of amounts of the given logistic equipment
	 * between the second ArrayList given as parameter and the first is larger than
	 * 0. Method used in AllocateLargerZone.
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

	/**
	 * Adds logistic equipment to the newEmptyZone, according to the
	 * logisticEquipment contained in the emptyZone and the zoneToAllocate. For
	 * method allocateInLargerZone.
	 * 
	 * @param logEquipEmptyZone
	 * @param logEquipZoneToAllocate
	 * @param logEquipNewEmptyZone
	 * @param differenceRaster
	 * @return
	 * @throws Exception
	 */
	public ArrayList<LogisticEquipment> updateLogisticEquipmentNewEmptyZone(
			ArrayList<LogisticEquipment> logEquipEmptyZone, ArrayList<LogisticEquipment> logEquipZoneToAllocate,
			ArrayList<LogisticEquipment> logEquipNewEmptyZone, int differenceRaster) throws Exception {

		ArrayList<LogisticEquipment> tempLogEquipEmptyZone = copyLogisticEquipments(logEquipEmptyZone);
		ArrayList<LogisticEquipment> tempLogEquipZoneToAllocate = copyLogisticEquipments(logEquipZoneToAllocate);

		int real = differenceRaster;
		for (int i = differenceRaster; i > 0;) {
			if (real > 0) {
				boolean found = false;
				for (int j = 0; j < tempLogEquipEmptyZone.size(); j++) {
					LogisticEquipment lg = tempLogEquipEmptyZone.get(j);
					int anzahl = lg.getAnzahl();
					int dimension = lg.getDimension();
					boolean remaining = logEquipRemaining(lg.getName(), tempLogEquipZoneToAllocate,
							tempLogEquipEmptyZone);
					// in order for a logistic equipment to be added to the newEmptyZone, it has to
					// be "remaining".
					if (dimension == i && anzahl > 0 && remaining) {
						logEquipNewEmptyZone.get(j).anzahlSteigern();
						tempLogEquipEmptyZone.get(j).anzahlMindern();
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

	/**
	 * returns a copy of the given list of logistic equipments
	 * 
	 * @param logisticEquipmentToCopy
	 * @return
	 */
	public ArrayList<LogisticEquipment> copyLogisticEquipments(ArrayList<LogisticEquipment> logisticEquipmentToCopy) {
		ArrayList<LogisticEquipment> toReturn = new ArrayList<LogisticEquipment>();

		for (int i = 0; i < logisticEquipmentToCopy.size(); i++) {
			toReturn.add(new LogisticEquipment(logisticEquipmentToCopy.get(i).getName(),
					logisticEquipmentToCopy.get(i).getAnzahl(), logisticEquipmentToCopy.get(i).getDimension()));
		}
		return toReturn;
	}

	/**
	 * Checks for each potential allocation of a zoneToAllocate, if in the returned
	 * modifiedStructure the dimension of EmptyZones really equals to the dimension
	 * of the ZonesToAllocate. If it is not the case, some bug is contained in the
	 * algorithm beacuse some space is lost or, vice-versa, some inexistent space is
	 * generated.
	 * 
	 * @param information
	 */
	public void testOfDimensionsOfEmptyZonesVsZonesToAllocate(Information information) {
		int lengthEmpty = 0;
		int lengthToAllocate = 0;
		for (int j = 0; j < information.modifiedStructure.getEmptyZones().size(); j++) {
			lengthEmpty += information.modifiedStructure.getEmptyZones().get(j).totalNumberRaster;
		}

		for (int j = 0; j < information.modifiedStructure.getZonesToAllocate().size(); j++) {
			lengthToAllocate += information.modifiedStructure.getZonesToAllocate().get(j).totalNumberRaster;
		}

		System.out.println("CHECK(Dimension emptyZones and ZoneToAllocate is equal in returned factory: "
				+ (boolean) (lengthEmpty == lengthToAllocate) + ")");
	}

	/**
	 * 
	 * @param initial
	 */
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

	/**
	 * 
	 * @param initial
	 */
	public static void demoFactoryShort(Factory initial) {
		System.out.println("factoryStructure:");
		Zone[][] factoryStructure = initial.getFactoryStructure();
		for (int i = 0; i < factoryStructure.length; i++) {
			if (i != 0)
				System.out.print("\n");
			for (int j = 0; j < factoryStructure[0].length; j++) {
				if (factoryStructure[i][j] == null) {
					System.out.print("null ");
				} else {
					String string = "";
					if (factoryStructure[i][j].isEmpty())
						string = "t";
					else
						string = "f";
					System.out.print(factoryStructure[i][j].name + "(" + factoryStructure[i][j].totalNumberRaster + ", "
							+ string + ") ");
				}
			}
		}
		System.out.println("\n\nzones to allocate:");
		if (initial.getZonesToAllocate().size() == 0) {
			System.out.println("There are no further zoneToAllocate");
		} else {
			for (int i = 0; i < initial.getZonesToAllocate().size(); i++) {
				String string = "";
				if (initial.getZonesToAllocate().get(i).isEmpty())
					string = "t";
				else
					string = "f";
				System.out.print(initial.getZonesToAllocate().get(i).name + "("
						+ initial.getZonesToAllocate().get(i).totalNumberRaster + ", " + string + ")\n");
			}
		}
	}

	/**
	 * 
	 * @param initial
	 */
	public static void demoFactoryShortNoNull(Factory initial) {
		System.out.println("\n\n\nFinal factoryStructure:");
		Zone[][] factoryStructure = initial.getFactoryStructure();
		for (int i = 0; i < factoryStructure.length; i++) {
			if (i != 0)
				System.out.print("\n");
			for (int j = 0; j < factoryStructure[0].length; j++) {
				if (factoryStructure[i][j] == null) {
				} else {
					String string = "";
					if (factoryStructure[i][j].isEmpty())
						string = "t";
					else
						string = "f";
					System.out.print(factoryStructure[i][j].name + "(" + factoryStructure[i][j].totalNumberRaster + ", "
							+ string + ") ");
				}
			}
		}
		System.out.println("\n\nzones to allocate:");
		if (initial.getZonesToAllocate().size() == 0) {
			System.out.println("There are no further zoneToAllocate!");
		} else {
			for (int i = 0; i < initial.getZonesToAllocate().size(); i++) {
				String string = "";
				if (initial.getZonesToAllocate().get(i).isEmpty())
					string = "t";
				else
					string = "f";
				System.out.print(initial.getZonesToAllocate().get(i).name + "("
						+ initial.getZonesToAllocate().get(i).totalNumberRaster + ", " + string + ")\n");
			}
		}
	}

	/**
	 * calculates the upper bound of costs
	 * 
	 * @param initial
	 */
	public static void totalNumberLogisticEquipment(Factory initial) {
		int totalNumber = 0;
		for (int i = 0; i < initial.getFactoryStructure().length; i++) {
			for (int j = 0; j < initial.getFactoryStructure()[0].length; j++) {
				if (initial.getFactoryStructure()[i][j] != null) {
					ArrayList<LogisticEquipment> logisticEquipment = initial.getFactoryStructure()[i][j]
							.getLogisticEquipment();
					for (int k = 0; k < logisticEquipment.size(); k++) {
						totalNumber += logisticEquipment.get(k).getAnzahl();
					}
				}

			}
		}
		System.out.println("Upper bound: " + totalNumber + " logistic equipment movements");
	}

	/**
	 * 
	 * @param initial
	 */
	public static void demoZonesToAllocate(Factory initial) {
		for (int i = 0; i < initial.getZonesToAllocate().size(); i++) {
			System.out.println(initial.getZonesToAllocate().get(i).toString());
		}
	}

	public static void main(String[] args) throws Exception {
		// Initialize objects and start algorithm

//		initial = new Factory();
//		demoFactory(initial);

//		Import old = new Import(); 
//		old.demo();

		// calculating execution time
		final long start = System.currentTimeMillis();

		initial = new Factory();
		totalNumberLogisticEquipment(initial);
//		demoFactory(initial);
//		System.out.println("\n\n\n\n");
//		demoZonesToAllocate(initial);
//		System.out.println("\n\n\n\n");
		demoFactoryShort(initial);

		Calculator calculator = new Calculator();
		@SuppressWarnings("unused")
		Factory newFactory = calculator.performAlgorithm();
		demoFactoryShortNoNull(newFactory);

		// calculating execution time
		// use this breakPoint to debug and see the result: newFactory
		final long end = System.currentTimeMillis();
		System.out.println("");
		System.out.println("END");
		System.out.println("------------------------------------------------");
		System.out.println("Total execution time: " + ((double) (end - start) / 1000) + " s");
		System.out.println("Total costs: " + cost + " logistic equipment movements");
		totalNumberLogisticEquipment(initial);
		System.out.println("------------------------------------------------");

	}
}
