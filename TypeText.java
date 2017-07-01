import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;

public class TypeText extends JLabel {
	private String lettersRemaining;
	private String lettersTyped;
	private Panel[][] panels;
	private Panel currPanel;
	private Player player;
	private JLabel wordsTypedLabel;
	private JLabel livesLabel;
	
	public TypeText(String word, Panel[][] panels, Player player, JLabel wordsTypedLabel, JLabel livesLabel, List<TypeText> activeTypeTexts) {
		lettersRemaining = word;
		lettersTyped = "";	
		this.panels = panels;
		this.player = player;
		this.wordsTypedLabel = wordsTypedLabel;
		this.livesLabel = livesLabel;
		
		this.setText(lettersRemaining);
	}
	
	public void update(Iterator<TypeText> iterator) {
		// as long as the TypeText has not reached the bottom
		if(currPanel.getRow() < panels.length - 1) {
			currPanel.remove(this); // remove this TypeText from current panel
			currPanel.updateUI(); // update UI of current panel
			Panel nextPanel = panels[currPanel.getRow() + 1][currPanel.getCol()]; // next panel down
			nextPanel.add(this); // add this to nextPanel
			nextPanel.updateUI(); // update UI of nextPanel
			currPanel = nextPanel; // set currPanel to nextPanel
		} 
		// otherwise, if the TypeText reached the bottom
		else if(currPanel.getRow() == panels.length - 1) {
			player.takeDamage();
			livesLabel.setText("Lives: " + player.getLives());
			reachedBottomOrWordTyped(iterator);
		}
	
	}
	
	public Panel getCurrPanel() {
		return currPanel;
	}
	
	public void setCurrPanel(Panel currPanel) {
		this.currPanel = currPanel;
	}
	
	public String getLettersRemaining() {
		return lettersRemaining;
	}
	
	public String getLettersTyped() {
		return lettersTyped;
	}

	public void keyPressed(KeyEvent e, Iterator<TypeText> iterator) {
		// ignore if key pressed is shift (because user needs to be able to type capital letters)
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {}
		// if the key pressed is equal to the next letter, add the letter to lettersTyped and remove from lettersRemaining
		else if(e.getKeyChar() == lettersRemaining.charAt(0)) {
			lettersTyped += lettersRemaining.charAt(0);
			lettersRemaining = lettersRemaining.substring(1);
			this.setText("<html><font color='yellow'>" + lettersTyped + "</font>" + lettersRemaining + "</html>");
		} 
		// otherwise if the key pressed is not the next letter, reset the lettersTyped and lettersRemaining
		else if (e.getKeyChar() != lettersRemaining.charAt(0)) {
			resetText();
		}
		
		// if the user types the whole word
		if(lettersRemaining.length() == 0) {
			reachedBottomOrWordTyped(iterator);
			player.wordTyped();
			wordsTypedLabel.setText("Score: " + player.getWordsTyped());
		}
	}

	
	// USES ITERATOR TO AVOID CONCURRENTMODIFICATIONEXCEPTION
	// Reset the word (words are recycled), remove this from currPanel, set currPanel to null, remove this from activeTypeTexts
	public void reachedBottomOrWordTyped(Iterator<TypeText> iterator) {
		resetText();
		currPanel.remove(this);
		currPanel.updateUI();
		this.currPanel = null;
		iterator.remove(); // remove the current element from iterator and the activeTypeTexts list
	}
	
	// reset lettersRemaining, lettersTyped, and the text of the JLabel
	public void resetText() {
		lettersRemaining = lettersTyped + lettersRemaining;
		lettersTyped = "";
		this.setText(lettersRemaining);
	}
}
