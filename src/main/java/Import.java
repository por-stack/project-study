
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Import {

	// MODIFICAREEEEEEEEEEEEEEEEEEEEEE
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	public static final String path = "C:\\Users\\aless\\eclipse-workspace\\Porsche\\src\\main\\java\\Java.xlsx"; // non
																													// funziona

	private String matrix[][];
	private Workbook wb;
	private Sheet sh;
	private DataFormatter dtfrm;

	private int i = 0;
	private int j = 0;

	/*
	 * Constructor
	 */
	public Import() throws InvalidFormatException, IOException {
		this.init();
		this.create();
//		wb.close();			// Closing the window
	}

	/*
	 * Workbook, Sheet, Dataformatter
	 */
	public void init() throws IOException, InvalidFormatException {
		wb = WorkbookFactory.create(new File(path));
		sh = wb.getSheetAt(0);
		dtfrm = new DataFormatter();
	}

	/*
	 * Count size of Excel-Table
	 */
	public void create() {
		for (Row row : sh) {
			j = 0;
			for (Cell cell : row) {
				String cellValue = dtfrm.formatCellValue(cell);
				j++;
			}
			i++;
		}
//		System.out.println("rows: " + i + ", columns: " + j);

		/*
		 * Create Matrix based on the size of the excel Table
		 */
		matrix = new String[i][j];
		i = 0;
		j = 0;

		/*
		 * Create a to the Excel table equivalent matrix in java
		 */
		for (Row row : sh) {
			j = 0;
			for (Cell cell : row) {
				String cellValue = dtfrm.formatCellValue(cell);
				matrix[i][j] = cellValue;
				j++;
			}
			i++;
		}
	}

	public void demo() {
		/*
		 * printout matrix
		 */
		for (int k = 0; k < i - 1; k++) {
			for (int l = 0; l < j; l++) {
				String cellValue = matrix[k][l];
				System.out.print(cellValue + "| ");
			}
			System.out.println("\n");
		}
	}

	public String[][] getMatrix() {
		return matrix;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	private Information allocatePerfectFitWithNeighbours(Factory factory, EmptyZone freeZoneAlone,
			ArrayList<Zone> neighboursToTakeIntoConsideration, Zone toAllocate) {

		ArrayList<Information> allocationOptions = new ArrayList<Information>();
		Zone[][] factoryStructure = factory.getFactoryStructure();
		Zone[][] tempStructure = new Zone[factoryStructure.length][factoryStructure[0].length + 1];
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
		factoryStructure[row][column-1] = toAllocate;

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
				Information information = calculate(neighboursToTakeIntoConsideration.get(turn), modifiedStructure);
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
}
