import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Factory {

	final int LENGTH_OF_ROWS_IN_RASTER = 43;
	int numRows;

	private Import mport;
	private Zone zone;
	private Raster raster;
	private String[][] matrix;

	public Factory() throws InvalidFormatException, IOException {
		mport = new Import();
		this.matrix = mport.getMatrix();
	}
	
	public void createStructureFactory() {
		int[][] counter = new int [mport.getJ()][43];
		
		for (int i = 0; i < mport.getI()-1; i++) { //j=5
			String fullPosition = matrix[i][5]; 
			
			System.out.println(fullPosition);
			
//			int row = Integer.parseInt(fullPosition.substring(0, 3));
//			
//			boolean rowAlreadyIn = false;  
//			for (int j = 0; j < counter.length; j++) {
//				if (counter[j][0] == 0) {
//					break; 
//				} else {
//					if (row == counter [j][0]) {
//						rowAlreadyIn = true; 
//						break; 
//					}
//				}
//			}
//			
//			if (rowAlreadyIn == false) {
//				//aggiungila, mettendo nella stessa row in counter il numero di raster
//			} else {
//				//non aggiungerlo, in counter vai alla altezza dove trovi lo stesso int row, e aggiungi una nuova column
//				//(andando a destra) e aggiungi il numero di raster in counter. 
//			}
		}
		
		//dopo tutta questa parte serve solamente contare in counter il numero di rows e di columns 
	}

}
