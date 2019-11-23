import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

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
		initial = new Factory();
		initial.createFactoryStructure();
		emptyZones = createEmptyZones(initial.getFactoryStructure());
	}

	public Zone[][] performAlgorithm() {
		/*	
		 */
		if (zonesToAllocate.isEmpty())
			return initial.getFactoryStructure();

		return initial.getFactoryStructure();
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
	 * After having computed the new factory layout, we reconvert the factory to
	 * excel
	 */
	public void convertToExcel(Factory factory) {

	}

	public static void main(String[] args) throws InvalidFormatException, IOException {
		Calculator calc = new Calculator();

//		Import old = new Import(); 
//		old.demo();

		 
//		Calculator calculator = new Calculator();
//		calculator.performAlgorithm(new Factory());
	}
}
