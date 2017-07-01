
public class Player {
	private int wordsTyped, lives;
	private String name;
	
	public Player(String name) {
		lives = 20;
		wordsTyped = 0;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void takeDamage() {
		lives--;
	}
	
	public void wordTyped() {
		wordsTyped++;
	}
	
	public int getWordsTyped() {
		return wordsTyped;
	}
	
	public int getLives() {
		return lives;
	}
}
