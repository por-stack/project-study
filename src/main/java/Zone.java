

public class Zone {
	/*
	 * Represents the AREA of e.g. zone 47S, not its content. Some zones will be
	 * empty and we will need to fill them. We focus on the empty spaces: zones with
	 * isEmpty == True. We perform our algorithm using the neighbors. The algorithm
	 * is implemented in Class Calculator
	 * 
	 * EVEN THOUGH: Do we need to differentiate between the zone as "area" and the concrete "filled-up" zone?!
	 */

	String name;
	int numRast;
	boolean isEmpty;
	
	Raster[] raster;

	Zone neighbRight;
	Zone neighbleft;

	
}
