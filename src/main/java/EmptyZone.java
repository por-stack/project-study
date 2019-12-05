

public class EmptyZone extends Zone {
	int[] locationInFactory = new int[2]; 

	public EmptyZone(String name, int row1, int row2, int i, int j) {
		super(name, row1, row2); 
		locationInFactory[0] = i; 
		locationInFactory[1] = j; 
	}

}
