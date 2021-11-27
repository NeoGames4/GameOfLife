import java.util.ArrayList;

public class GameOfLife {
	
	ArrayList<Cell> cells = new ArrayList<>();
	
	public void add(int x, int y) {
		if(!isSet(x, y, cells)) cells.add(new Cell(x, y));
	}
	
	public void remove(int x, int y) {
		cells.removeIf(c -> c.x == x && c.y == y);
	}
	
	public static boolean isSet(int x, int y, ArrayList<Cell> cells) {
		for(Cell c : cells) {
			if(c.x == x && c.y == y) return true;
		} return false;
	}
	
	public static int nearbyCount(int x, int y, ArrayList<Cell> cells) {
		int c = 0;
		for(int i = -1; i<2; i++) {
			for(int j = -1; j<2; j++) {
				if((i != 0 || j != 0) && isSet(x+i, y+j, cells)) c++;
			}
		} return c;
	}
	
	public void update() {
		ArrayList<Cell> cellsCopy = new ArrayList<>();
		cellsCopy.addAll(cells);
		for(Cell c : cellsCopy) {
			int nearby = nearbyCount(c.x, c.y, cellsCopy);
			if(nearby < 2 || nearby > 3) cells.removeIf(cell -> cell.x == c.x && cell.y == c.y);
			for(int j = -1; j<2; j++) {
				for(int k = -1; k<2; k++) {
					if((j != 0 || k != 0) && nearbyCount(c.x+j, c.y+k, cellsCopy) == 3 && !isSet(c.x+j, c.y+k, cells)) cells.add(new Cell(c.x+j, c.y+k));
				}
			}
		}
	}
	
	class Cell {
		
		public final int x;
		public final int y;
		
		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}

}
