import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TypingGame extends JFrame implements KeyListener {
	private List<String> allWords;
	private List<TypeText> activeTypeTexts;
	private JPanel gamePanel;
	private Timer updateTimer;
	private int index;
	private Panel[][] panels;
	private GridLayout panelsLayout;
	private Player player;
	
	private JLabel livesLabel;
	private JLabel wordsTypedLabel;
	private JLabel currWaveLabel;
	private JButton pauseButton;
	
	private int wordsReleasedInCurrWave;
	private int waveWordCount;
	private int currWave;
	
	private boolean paused;
	
	public TypingGame() {
		this.setTitle("Typing Game");
		this.setSize(1000, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(this);
		
		paused = false;
		currWave = 1;
		index = 0;
		wordsReleasedInCurrWave = 0;
		waveWordCount = 10; // first wave is 10 words
		player = new Player("ding");
		allWords = new ArrayList<String>();
		activeTypeTexts = new ArrayList<TypeText>();
		gamePanel = new JPanel();
		panelsLayout = new GridLayout(35, 10);
		panels = new Panel[panelsLayout.getRows()][panelsLayout.getColumns()];
		player = new Player(JOptionPane.showInputDialog(gamePanel, "Enter your name: "));
		
		livesLabel = new JLabel("Lives: " + player.getLives());
		wordsTypedLabel = new JLabel("Score: " + player.getWordsTyped());
		currWaveLabel = new JLabel("Wave: " + currWave);
		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(makePauseListener());
		
		gamePanel.setBackground(Color.WHITE);
		gamePanel.setLayout(panelsLayout);

		addPanelsToGamePanel();
		
		panels[0][0].setLayout(new GridLayout(0, 1)); // so that the button will be aligned to the top left corner
		panels[0][0].add(pauseButton);
		panels[0][2].add(currWaveLabel);
		panels[0][4].add(livesLabel);
		panels[0][6].add(wordsTypedLabel);

		readFile();

		this.add(gamePanel);
		
		this.setVisible(true);
		
		startGame();
		
	}
	
	// starts the game
	public void startGame() {
		updateTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// add a TypeText to a random Panel in gamePanel every second, as long as max words for the wave has
				// not been reached
				if(wordsReleasedInCurrWave < waveWordCount) {
					int randomCol = (int) (Math.random() * panelsLayout.getColumns()); // random column in first row
					
					Panel parentPanel = panels[0][randomCol];
					TypeText currTypeText = new TypeText(allWords.get(index), panels, player, wordsTypedLabel, livesLabel, activeTypeTexts);
					
					parentPanel.add(currTypeText); // add the TypeText to the Panel
					activeTypeTexts.add(currTypeText); // add the TypeText to activeTypeTexts
					parentPanel.updateUI(); // update the UI of the Panel
					
					currTypeText.setCurrPanel(parentPanel); // set the currPanel of the TypeText to the Panel it was added to
					index++;
					wordsReleasedInCurrWave++;
				// if waveCount has been reached, decrease time between each TypeText/increase their speed,
				// increase waveCount, and reset wordsReleasedInCurrWave to 0
				} else if (wordsReleasedInCurrWave == waveWordCount && activeTypeTexts.isEmpty()) {
					updateTimer.setInitialDelay(updateTimer.getInitialDelay() - 50);
					waveWordCount += 10;
					currWave++;
					currWaveLabel.setText("Wave: " + currWave);
					wordsReleasedInCurrWave = 0;
				}
				
				// updates all active TypeTexts
				for(Iterator<TypeText> iterator = activeTypeTexts.iterator(); iterator.hasNext();) {
					TypeText currTypeText = iterator.next();
					currTypeText.update(iterator);
				}
				
				// if index reached the last word, reset it to 0 so words don't run out and reshuffle the words
				if(index == allWords.size() - 1) {
					index = 0;
					Collections.shuffle(allWords);
				}
				
				updateTimer.restart();
				
				// if user paused the game, stop the timer
				if(paused) {
					updateTimer.stop();
				}
				
				// if player ran out of lives, end the game
				if(player.getLives() == 0) {
					endGame();
				}
			}
		});
		updateTimer.start();
	}
	
	// read file and add the words to the array list of TypeTexts, also randomize the array list so that words
	// will not always be in the same order
	public void readFile() {
		Scanner scanner;
		try {
			scanner = new Scanner(new File("words.txt"));
			while(scanner.hasNextLine()) {
				String word = scanner.nextLine();
				allWords.add(word);
			}
			Collections.shuffle(allWords);
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// adds a Panel (JPanel) to each box in GridLayout of gamePanel
	public void addPanelsToGamePanel() {
		for(int r = 0; r < panelsLayout.getRows(); r++) {
			for(int c = 0; c < panelsLayout.getColumns(); c++) {
				Panel panel = new Panel(r, c);
				panels[r][c] = panel;
				gamePanel.add(panel);
			}
		}
	}
	
	public void endGame() {
		updateTimer.stop();
		
		// Write name and score to the scores.txt file
		try {
			PrintWriter writer = new PrintWriter(new FileWriter("scores.txt", true));
			writer.println(player.getName() + " " + player.getWordsTyped());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Pair> nameScorePairs = new ArrayList<Pair>();
		
		// read the names and scores, add to nameScorePairs ArrayList
		try {
			Scanner scoreScanner = new Scanner(new File("scores.txt"));
				while(scoreScanner.hasNextLine()) {
				String line = scoreScanner.nextLine();
				String[] lineSplit = line.split(" ");
				
				String name = lineSplit[0];
				int score = Integer.parseInt(lineSplit[1]);
				nameScorePairs.add(new Pair(name, score));
			}
			scoreScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// sort the scores list in descending order
		Collections.sort(nameScorePairs, Collections.reverseOrder());
		
		// Heading for high scores
		panels[7][4].setLayout(new FlowLayout(FlowLayout.RIGHT));
		panels[7][4].add(new JLabel("High"));
		panels[7][5].setLayout(new FlowLayout(FlowLayout.LEFT));
		panels[7][5].add(new JLabel("Scores"));
		panels[9][4].add(new JLabel("Name"));
		panels[9][5].add(new JLabel("Score"));
		
		// Show top 10 scores or however many scores there are in scores.txt if less than 10
		for(int i = 0; i < 10 && i < nameScorePairs.size(); i++) {
			panels[10 + i][4].add(new JLabel(nameScorePairs.get(i).getName()));
			panels[10 + i][5].add(new JLabel("" + nameScorePairs.get(i).getScore()));
		}
	}
	
	public ActionListener makePauseListener() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(paused == true) {
					paused = false;
					updateTimer.start();
				} else if(paused == false) {
					paused = true;
				}
				
			}
		};
		return listener;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Have to use iterator because removing while iterating causes ConcurrentModificationException
		for(Iterator<TypeText> iterator = activeTypeTexts.iterator(); iterator.hasNext();) {
			TypeText currTypeText = iterator.next();
			currTypeText.keyPressed(e, iterator);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) {
		new TypingGame();
	}
}
