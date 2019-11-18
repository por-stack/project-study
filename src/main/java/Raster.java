

public class Raster {
	
	// if it is not a column or an hydrant
	boolean isUsable;
	int row; 
	int column; 
	Zone belongTo; 
	
	public Raster(int row, int column) {
		this.row = row; 
		this.column = column; 
	}

	@Override
	public String toString() {
		return "Raster [isUsable=" + isUsable + ", row=" + row + ", column=" + column + "]";
	}
}
