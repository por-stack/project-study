import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		 * factoryö.getFactoryStructure is really modified. This implies that when
		 * entering in calculate the structure needs to be copied.
		 */

		while (!zonesToBeAllocated.isEmpty()) {
			zonesToBeAllocated = initial.getZonesToAllocate(); //update

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
		information = checkForLargerZone(zone, factory);
		if (information.applicable) return information;
		
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

	public Information checkForLargerZone(Zone zone, Factory factory) {
		Zone toAllocate = zone;
		boolean allZonesLarger = true;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			EmptyZone freeZone = factory.getEmptyZones().get(j);
			if (toAllocate.totalNumberRaster > freeZone.totalNumberRaster) {
				allZonesLarger = false;
				return new Information(false, null, 0);
			}
		}
		// if all zones are larger, then we check wich of the emptyZones is the largest
		// one
		if (allZonesLarger) {
			int largestSize = factory.getEmptyZones().get(0).totalNumberRaster;
			int positionLargest = 0;
			for (int j = 1; j < factory.getEmptyZones().size(); j++) {
				if (factory.getEmptyZones().get(j).totalNumberRaster > largestSize) {
					positionLargest = j;
				}
			}
			//da vedere ancora come fare, dato che la zona piú grande nel programma é come se andasse spezzata in due zone nuove 
			int cost = calculateCost(factory.getEmptyZones().get(positionLargest),
					toAllocate);
			Factory modifiedStructure = allocateInLargerZone(factory, factory.getEmptyZones().get(positionLargest),
					toAllocate);
			
			return new Information(true, modifiedStructure, cost)
		} else {
			return new Information(false, null, 0);
		}
	}

	public Information fitPerfectly(Zone zone, Factory factory) {
		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		// for the ZoneToAllocate given as parameter iterate over the emptyZones and
		// check if there is a feasible solution.
		// save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		Zone toAllocate = zone;
		for (int j = 0; j < factory.getEmptyZones().size(); j++) {
			EmptyZone freeZone = factory.getEmptyZones().get(j);
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

	public Information fitPerfectlyWithList(Zone zone, Factory factory) {

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
				cost =+ freeZoneLE - toAllocateLE;
			} else {
				cost =+ toAllocateLE - freeZoneLE;
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
	 * creates a copy of a matrix array
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

	public static void main(String[] args) throws InvalidFormatException, IOException {
		// Initialize objects and start algorithm

//		Import old = new Import(); 
//		old.demo();

		initial = new Factory();
//		Calculator calculator = new Calculator();
//		calculator.performAlgorithm(new Factory());
//		newFactoryStructure = calculator.performAlgorithm(initial);
		Factory newFactory = initial;
//		newFactory.setFactoryStructure(newFactoryStructure);
		// calculateCostBenefits(initial.getFactoryStructure, newFactoryStructure);
	}
}
