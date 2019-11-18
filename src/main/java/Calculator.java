import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Calculator {


	/*
	 * As parameter we insert the assignment factory. We find a way to extract the
	 * data directly from the excel sheet. 
	 * We then perform the algorithm on the factory. 
	 */
	public Import performAlgorithm(Import old) throws InvalidFormatException, IOException {
		/*
		 * For Each row, for each zone inside the column, find the empty zone, etc. etc. etc.
		 */
		
		// our result at the end of the day
		return new Import();
	}
	
	/*
	 * After having computed the new factory layout, we reconvert the factory to excel
	 */
	public void convertToExcel(Factory factory) {
		
	}

	public static void main(String[] args) throws InvalidFormatException, IOException {
		// Initialize objects and start algorithm

//		Import old = new Import(); 
//		old.demo();
		
		Factory factory = new Factory(); 
		factory.createStructureFactory();
		
//		Calculator calculator = new Calculator();
//		calculator.performAlgorithm(new Factory());
	}
}
