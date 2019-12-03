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
//
//	public Zone[][] calculate(Factory factory)) {
//		Object information = false; //infromation contains boolean applicable and the modified structure coming from the lower lvel in the recursion 
//		information = fitPerfectly(factory); 
//		(if information.applicable) {
//			return information; 
//		}
//		fitPerfectlyWithList(); 
//		fitMoving1Neighbour(); 
//		fitMoving1NeighbourWithList(); 
//		fitMoving2Neighbours(); 
//		fitMoving2NeighboursWithList(); 
//		fitMoving3Neighbours(); 
//		fitMoving3NeighboursWithList();
//		fitMoving4Neighbours()
//		fitMoving4NeighboursWithList(); 
//		fitMoving5Neighbours(); 
//		fitMoving5NeighboursWithList(); 
//		fitMoving6Neighbours(); 
//		fitMoving6NeighboursWithList(); 
//	}

	public void fitPerfectly(Factory factory) {
		// prendi le zone da allocare
		// compara la prima zona con prima zona libera
		for (int i = 0; i < factory.getZonesToAllocate().size(); i++) {
			Zone toAllocate = factory.getZonesToAllocate().get(i);
			for (int j = 0; j < factory.getEmptyZones().size(); j++) {
				Zone freeZone = factory.getEmptyZones().get(j);
				if (toAllocate.totalNumberRaster == freeZone.totalNumberRaster) {
					// ottimo
				}

			}
		}
		// ci sta? (totale raster uguale)
		// se si, calcola costo e ricordatelo, salva la struttura modificata
		// se no, segna che non é possibile allocare
		// compara con prossima zona ...
		// esistono zone che possono allocate?
		// se si, compara costi di tutti i si
		// scegli quella con i costi minori: applica la struttura nuova e metti trovato
		// = true --> return costi e struttura nuova
		// se no, lascia la struttura com'é e ridai trovato false;
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

		Factory factory = new Factory();
//		Calculator calculator = new Calculator();
//		calculator.performAlgorithm(new Factory());
//		newFactoryStructure = calculator.performAlgorithm(initial);
		// calculateCostBenefits(initial.getFactoryStructure, newFactoryStructure);
	}
}
