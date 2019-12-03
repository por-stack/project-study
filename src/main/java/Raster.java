
public class Raster {

	// if it is not a column or an hydrant
	private boolean isUsable;
	private boolean isTrainStat;
	private int row;
	private int column;
	private Zone belongTo;

	public Raster(int row, int column, boolean isTrainStat) {
		this.row = row;
		this.column = column;
		this.isTrainStat = isTrainStat;
	}

	@Override
	public String toString() {
		return "Raster [isUsable=" + isUsable + ", row=" + row + ", column=" + column + "]";
	}
}
