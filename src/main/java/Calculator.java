import java.io.IOException;
import java.util.ArrayList;

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

	public Zone[][] performAlgorithm(Factory factory) {
		/*	
		 */
		// this is the factory structure that we are going to modify in every step
		Zone[][] newFactory = copyOfFactoryModification(factory.getFactoryStructure());

		if (factory.getZonesToAllocate().isEmpty()) {
			return newFactory;
		}

		newFactory = calculate(factory);

		return newFactory;
	}

	public Information calculate(Factory factory) {
		 
		
		Information information; //information contains boolean applicable and the modified structure coming from the lower lvel in the recursion 
		
		//level 0
		information = checkForLargerZones();
		if (information.applicable) return information;
		
		//level 1
		information = fitPerfectly(factory); 
		if (information.applicable) return information; 
		
		//level 2
		information = fitPerfectlyWithList(); 
		fitMoving1Neighbour(); 
		fitMoving1NeighbourWithList(); 
		fitMoving2Neighbours(); 
		fitMoving2NeighboursWithList(); 
		fitMoving3Neighbours(); 
		fitMoving3NeighboursWithList();
		fitMoving4Neighbours()
		fitMoving4NeighboursWithList(); 
		fitMoving5Neighbours(); 
		fitMoving5NeighboursWithList(); 
		fitMoving6Neighbours(); 
		fitMoving6NeighboursWithList(); 
	}

	public Information fitPerfectly(Factory factory) {
		// prendi le zone da allocare
		// compara la prima zona con prima zona libera
		ArrayList<Information> allocationOptions = new ArrayList<Information>();

		// for every ZoneToAllocate iterate over the emptyZones and check if there is a
		// feasible solution.
		// save information (applicable, modifiedstructure, cost) for every feasible
		// solution
		for (int i = 0; i < factory.getZonesToAllocate().size(); i++) {
			Zone toAllocate = factory.getZonesToAllocate().get(i);
			for (int j = 0; j < factory.getEmptyZones().size(); j++) {
				EmptyZone freeZone = factory.getEmptyZones().get(j);
				if (toAllocate.totalNumberRaster == freeZone.totalNumberRaster) {
					int cost = calculateCost(freeZone, toAllocate);
					Factory modifiedStructure = allocate(factory, freeZone, toAllocate);
					allocationOptions.add(new Information(true, modifiedStructure, cost));
				}
			}

			// check if there is any feasible solution.
			// If there is more than one, chosse the cheapest allocation.
			if (allocationOptions.size() == 0) {
				// qui ce da vedere se e quando usare la seconda hirarchy 
				return new Information(false, null, 0);
			} else if (allocationOptions.size() == 1) {
				return new Information(true, allocationOptions.get(0).modifiedStructure,
						allocationOptions.get(0).costs);
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
		return null;
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
	public Factory allocate(Factory factory, EmptyZone emptyZone, Zone toAllocate) {
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
		Calculator calculator = new Calculator();
//		calculator.performAlgorithm(new Factory());
		newFactoryStructure = calculator.performAlgorithm(initial);
		Factory newFactory = initial;
		newFactory.setFactoryStructure(newFactoryStructure);
		// calculateCostBenefits(initial.getFactoryStructure, newFactoryStructure);
	}
}
