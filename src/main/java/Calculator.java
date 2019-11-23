import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.xmlbeans.impl.jam.internal.elements.ArrayClassImpl;

public class Calculator {

	/*
	 * input factory to work and list of zones to allocate
	 */
	private Factory initial;
	private ArrayList<Zone> emptyZones;
	private ArrayList<Zone> zonesToAllocate;

	/*
	 * The initial factory is created with the input data. The factory is analyzed
	 * and a list of the empty zones is created.
	 */
	public Calculator() throws InvalidFormatException, IOException {
		// qui ho levato la initialisieurung di factoy structure, perche viene gia fatta
		// nella main.
		// inoltre la methode createfactorystructure non esite piú!
		emptyZones = createEmptyZones(initial.getFactoryStructure());
		zonesToAllocate = createZonesToAllocate(initial.getFactoryStructure()); // to implement
		performAlgorithm(initial.getFactoryStructure());
	}

	public Zone[][] performAlgorithm(Zone[][] factory) {
		/*	
		 */
		// this is the factory structure that we are going to modify in every step
		Zone[][] newFactory = copyOfFactoryModification(factory);

		if (zonesToAllocate.isEmpty()) {
			return newFactory;
		}

		return newFactory;
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

	public ArrayList<Zone> createEmptyZones(Zone[][] factory) {
		ArrayList<Zone> emptyZones = new ArrayList<Zone>();
		for (int i = 0; i < factory.length; i++) {
			for (int j = 0; j < factory[0].length; j++) {
				if (factory[i][j].isEmpty) {
					emptyZones.add(factory[i][j]);
				}
			}
		}
		return emptyZones;
	}

	/*
	 * to implement
	 */
	public ArrayList<Zone> createZonesToAllocate(Zone[][] factory) {
		ArrayList<Zone> zonesToAllocate = new ArrayList<Zone>();

		return zonesToAllocate;
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
	}
}
