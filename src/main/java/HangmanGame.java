import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HangmanGame {
    private HangLib hangLib;
    private List<JButton> letterButtons;

    private JPanel lettersPanel;
    private JLabel wordLabel;
    private JLabel attemptsLabel;
    private JLabel hangmanImageLabel;

    private int lettersPerRow = 8;

    private JPanel backgroundPanel;
    private JLabel backgroundLabel;

    public HangmanGame(HangLib hangLib) {
        this.hangLib = hangLib;
        this.letterButtons = new ArrayList<>();

        JFrame frame = new JFrame("Hangman Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(600, 400);

        backgroundPanel = new JPanel();
        backgroundPanel.setLayout(new BorderLayout());
        backgroundLabel = new JLabel();
        backgroundPanel.add(backgroundLabel, BorderLayout.CENTER);
        frame.setContentPane(backgroundPanel);

        setBackgroundImage("images/back.png");

        wordLabel = new JLabel("", SwingConstants.CENTER);
        attemptsLabel = new JLabel("Attempts left: " + hangLib.getAttemptsLeft(), SwingConstants.LEFT);

        hangmanImageLabel = new JLabel();
        hangmanImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateHangmanImage();

        JPanel alphabetPanel = new JPanel(new GridLayout(0, lettersPerRow, 10, 10));
        for (char letter = 'a'; letter <= 'z'; letter++) {
            final char finalLetter = letter;
            JButton letterButton = new JButton(String.valueOf(letter).toUpperCase());
            letterButton.addActionListener(e -> handleLetterButtonClick(finalLetter, letterButton));

            alphabetPanel.add(letterButton);
            letterButtons.add(letterButton);
        }

        lettersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        lettersPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);

        JPanel wordPanel = new JPanel(new BorderLayout());
        wordPanel.add(wordLabel, BorderLayout.CENTER);
        wordPanel.add(lettersPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(alphabetPanel, BorderLayout.NORTH);
        centerPanel.add(wordPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(attemptsLabel);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(hangmanImageLabel, BorderLayout.SOUTH);

        frame.setVisible(true);

        startNewGame();
    }

    private void startNewGame() {
        initializeGameFromWordsFile("words.txt");
        updateUI();
    }

    private void initializeGameFromWordsFile(String filename) {
        try {
            hangLib.initializeGameFromWordsFile(filename);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(1);
        }

        for (JButton button : letterButtons) {
            button.setEnabled(true);
        }

        updateHangmanImage();
    }

    private void handleLetterButtonClick(char letter, JButton button) {
        hangLib.makeGuess(letter);
        updateUI();

        button.setEnabled(false);

        if (hangLib.isGameOver()) {
            String message = hangLib.isWordGuessed() ? "You win!" : "Game over. The word was: " + hangLib.getSecretWord();
            int choice = JOptionPane.showConfirmDialog(null, message + "\nTry again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                startNewGame();
            } else {
                System.exit(0);
            }
        } else {
            updateHangmanImage();
        }
    }

    private void updateHangmanImage() {
        int attemptsLeft = hangLib.getAttemptsLeft();
        String imageName;

        switch (attemptsLeft) {
            case 6:
                imageName = "images/hangman0.png";
                break;
            case 5:
                imageName = "images/hangman1.png";
                break;
            case 4:
                imageName = "images/hangman2.png";
                break;
            case 3:
                imageName = "images/hangman3.png";
                break;
            case 2:
                imageName = "images/hangman4.png";
                break;
            case 1:
                imageName = "images/hangman5.png";
                break;
            default:
                imageName = "images/hangman6.png";
                break;
        }

        ImageIcon hangmanImage = new ImageIcon(getClass().getResource(imageName));
        hangmanImageLabel.setIcon(hangmanImage);
    }

    private void updateUI() {
        lettersPanel.removeAll();

        int lettersCount = 0;
        for (char letter : hangLib.getFormattedWordWithBlanks().toCharArray()) {
            JLabel spaceLabel = new JLabel(String.valueOf(letter).toUpperCase(), SwingConstants.CENTER); // Преобразование в верхний регистр
            spaceLabel.setFont(new Font("Times new Roman", Font.BOLD, 24));
            spaceLabel.setForeground(Color.BLACK);
            lettersPanel.add(spaceLabel);

            lettersCount++;
            if (lettersCount % lettersPerRow == 0) {
                lettersPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        }

        attemptsLabel.setText("Attempts left: " + hangLib.getAttemptsLeft());

        SwingUtilities.getWindowAncestor(lettersPanel).validate();
        SwingUtilities.getWindowAncestor(lettersPanel).repaint();
    }

    private void setBackgroundImage(String imageName) {
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource(imageName));
        backgroundLabel.setIcon(backgroundImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HangmanGame(new HangLib()));
    }
}
