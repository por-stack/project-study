
public class Zone {
	String name;
	
	int amountRastRow1;
	int amountRasterRow2;
	int[] rows = new int[2];
			
	boolean isEmpty; //in case of empty zone
	
	Raster[][] raster = new Raster[2][43];

	Zone neighbRight;
	Zone neighbLeft;

	
}
