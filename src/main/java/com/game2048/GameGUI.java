package com.game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class GameGUI extends JFrame {
    private GameBoard board;
    private final JPanel gridPanel;
    private final JLabel scoreLabel;
    private final JLabel statusLabel;
    private final Map<Integer, Color> colorMap;
    private final int tileSize = 100; // Increased tile size
    private final int boardSize;

    public GameGUI(int size) {
        this.boardSize = size;
        this.board = GameBoard.createInitialBoard(size);
        this.gridPanel = new JPanel();
        this.scoreLabel = new JLabel("Score: 0");
        this.statusLabel = new JLabel("Game Started");
        this.colorMap = new HashMap<>();
        
        initializeColorMap();
        initializeUI();
        updateDisplay();
    }

    private void initializeColorMap() {
        colorMap.put(0, new Color(0xCDC1B4));
        colorMap.put(2, new Color(0xEEE4DA));
        colorMap.put(4, new Color(0xEDE0C8));
        colorMap.put(8, new Color(0xF2B179));
        colorMap.put(16, new Color(0xF59563));
        colorMap.put(32, new Color(0xF67C5F));
        colorMap.put(64, new Color(0xF65E3B));
        colorMap.put(128, new Color(0xEDCF72));
        colorMap.put(256, new Color(0xEDCC61));
        colorMap.put(512, new Color(0xEDC850));
        colorMap.put(1024, new Color(0xEDC53F));
        colorMap.put(2048, new Color(0xEDC22E));
    }

    private void initializeUI() {
        setTitle("2048 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Calculate window size based on board size
        int windowWidth = (tileSize * boardSize) + 100; // Extra space for padding
        int windowHeight = (tileSize * boardSize) + 150; // Extra space for header
        
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setResizable(true); // Allow resizing

        // Header panel with better styling
        JPanel headerPanel = new JPanel(new FlowLayout());
        headerPanel.setBackground(new Color(0xFAF8EF));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Style the score label
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(0x776E65));
        
        // Style the status label
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel.setForeground(new Color(0x776E65));
        
        headerPanel.add(scoreLabel);
        headerPanel.add(Box.createHorizontalStrut(20)); // Add spacing
        headerPanel.add(statusLabel);
        headerPanel.add(Box.createHorizontalStrut(20)); // Add spacing
        
        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.setBackground(new Color(0x8F7A66));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.addActionListener(e -> restartGame());
        headerPanel.add(restartButton);

        add(headerPanel, BorderLayout.NORTH);

        // Main game panel with background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xFAF8EF));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Grid panel
        gridPanel.setLayout(new GridLayout(boardSize, boardSize, 10, 10));
        gridPanel.setBackground(new Color(0xBBADA0));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);

        // Instructions panel
        JLabel instructions = new JLabel(
            "Use Arrow Keys to move tiles • R to restart • Combine tiles to reach 2048!",
            SwingConstants.CENTER
        );
        instructions.setFont(new Font("Arial", Font.PLAIN, 12));
        instructions.setForeground(new Color(0x776E65));
        instructions.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(instructions, BorderLayout.SOUTH);

        // Keyboard controls
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        setFocusable(true);
        pack();
        setLocationRelativeTo(null); // Center on screen
        
        // Make sure window is visible
        setVisible(true);
        
        // Request focus for keyboard input
        requestFocusInWindow();
        
        System.out.println("Game window initialized. Size: " + getSize());
    }

    private void handleKeyPress(KeyEvent e) {
        if (board.isGameOver() || board.isWon()) {
            return;
        }

        Direction direction = null;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: direction = Direction.UP; break;
            case KeyEvent.VK_DOWN: direction = Direction.DOWN; break;
            case KeyEvent.VK_LEFT: direction = Direction.LEFT; break;
            case KeyEvent.VK_RIGHT: direction = Direction.RIGHT; break;
            case KeyEvent.VK_R: 
                restartGame(); 
                return;
        }

        if (direction != null) {
            board = board.move(direction);
            updateDisplay();
            checkGameStatus();
        }
    }

    private void updateDisplay() {
        scoreLabel.setText("Score: " + board.getScore());
        gridPanel.removeAll();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Tile tile = board.getTile(i, j);
                JLabel tileLabel = createTileLabel(tile);
                gridPanel.add(tileLabel);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
        
        // Debug info
        System.out.println("Display updated. Score: " + board.getScore());
        System.out.println("Board state:\n" + board.toString());
    }

    private JLabel createTileLabel(Tile tile) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(tileSize, tileSize));

        int value = tile.getValue();
        Color backgroundColor = colorMap.getOrDefault(value, new Color(0x3C3A32));
        Color foregroundColor = value < 16 ? new Color(0x776E65) : Color.WHITE;

        // Adjust font size based on value
        Font font;
        if (value == 0) {
            font = new Font("Arial", Font.BOLD, 24);
        } else if (value < 100) {
            font = new Font("Arial", Font.BOLD, 36);
        } else if (value < 1000) {
            font = new Font("Arial", Font.BOLD, 32);
        } else {
            font = new Font("Arial", Font.BOLD, 24);
        }

        label.setFont(font);
        label.setBackground(backgroundColor);
        label.setForeground(foregroundColor);
        label.setBorder(BorderFactory.createLineBorder(new Color(0xBBADA0), 2));

        if (value != 0) {
            label.setText(String.valueOf(value));
        }

        return label;
    }

    private void checkGameStatus() {
        if (board.isWon()) {
            statusLabel.setText("You Win! 🎉 Press R to restart");
            statusLabel.setForeground(new Color(0xEDC22E));
            JOptionPane.showMessageDialog(this, 
                "Congratulations! You reached 2048!", 
                "You Win!", 
                JOptionPane.INFORMATION_MESSAGE);
        } else if (board.isGameOver()) {
            statusLabel.setText("Game Over! 😞 Press R to restart");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Game Over! No more moves possible.", 
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setText("Playing... Use arrow keys to move");
            statusLabel.setForeground(new Color(0x776E65));
        }
    }

    private void restartGame() {
        this.board = GameBoard.createInitialBoard(boardSize);
        updateDisplay();
        statusLabel.setText("Game Restarted");
        statusLabel.setForeground(new Color(0x776E65));
        requestFocusInWindow(); // Regain focus for keyboard input
    }

    public static void main(String[] args) {
        // Use System.out to debug startup
        System.out.println("Starting 2048 Game...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                int size = 4; // Configurable board size
                System.out.println("Creating game with board size: " + size);
                GameGUI game = new GameGUI(size);
                game.setVisible(true);
                System.out.println("Game window should be visible now");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting game: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}