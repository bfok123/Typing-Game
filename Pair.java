
public class Pair implements Comparable<Pair> {
	private String name;
	private int score;
	
	public Pair(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}

	// To allow for an ArrayList of Pairs to be sorted 
	public int compareTo(Pair nameScorePair) {
		return (this.score - nameScorePair.getScore());
	}
}
