
public class Information {
	
	boolean applicable; 
	
	Factory modifiedStructure;
	
	double costs; 
	
	int row; //row the information is about
	
	int lengthRow; //how many raster are occupied in the row	
	
	int amountLogisticsEquipment; //how many logisticEquipment are in the row
	
	int amountBhf; //how many trainstations
	
	int amountFreeZones; //How many free zones are in the row 
	Zone[] freeZone; //length, logistic equipment
	
	int amountZones; 
	Zone[] occupiedZones; //da dove a dove, ogni zona quanti Raster contiene
	
	public Information (boolean applicable, Factory modifiedStructure, double cost) {
		this.applicable = applicable; 
		this.modifiedStructure = modifiedStructure;
		this.costs = cost; 
	}
	
	
}
