// MazeCrawler.java — single-file Java Swing maze game
// Compile:  javac MazeCrawler.java
// Run:      java MazeCrawler

import java.awt.*;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// ===== Difficulty =====
enum Difficulty {
    EASY("Easy", 3, 1.0),
    MEDIUM("Medium", 4, 1.5),
    HARD("Hard", 5, 2.0);

    public final String label;
    public final int totalStages;
    public final double multiplier;

    Difficulty(String label, int totalStages, double multiplier) {
        this.label = label;
        this.totalStages = totalStages;
        this.multiplier = multiplier;
    }
}

final class UiColors {
    private UiColors() {}

    static Color readableTextOn(Color bg) {
        double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255.0;
        return luminance > 0.62 ? new Color(0x1B2030) : Color.WHITE;
    }
}

// ===== Maze =====
class Maze {
    public static final char WALL = '#';
    public static final char PATH = ' ';
    public static final char START = 'S';
    public static final char EXIT = 'E';

    private final char[][] grid;
    private final int rows;
    private final int cols;
    private final Point start;
    private final Point exit;

    public Maze(String[] rowsData) {
        this.rows = rowsData.length;
        this.cols = rowsData[0].length();
        this.grid = new char[rows][cols];
        Point s = null, e = null;
        for (int r = 0; r < rows; r++) {
            String line = rowsData[r];
            if (line.length() != cols) {
                throw new IllegalArgumentException("Maze row " + r + " has wrong width");
            }
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                grid[r][c] = ch;
                if (ch == START) s = new Point(c, r);
                else if (ch == EXIT) e = new Point(c, r);
            }
        }
        if (s == null || e == null) {
            throw new IllegalArgumentException("Maze must contain S and E");
        }
        this.start = s;
        this.exit = e;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Point getStart() { return new Point(start); }
    public Point getExit() { return new Point(exit); }

    public char cellAt(int col, int row) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return WALL;
        return grid[row][col];
    }

    public boolean isWalkable(int col, int row) {
        char ch = cellAt(col, row);
        return ch != WALL;
    }

    public boolean isExit(int col, int row) {
        return cellAt(col, row) == EXIT;
    }
}

// ===== MazeLibrary =====
final class MazeLibrary {
    private MazeLibrary() {}

    public static List<Maze> forDifficulty(Difficulty d) {
        switch (d) {
            case EASY:   return easyMazes();
            case MEDIUM: return mediumMazes();
            case HARD:   return hardMazes();
            default: throw new IllegalArgumentException();
        }
    }

    private static List<Maze> easyMazes() {
        List<Maze> list = new ArrayList<>();
        list.add(new Maze(new String[] {
            "###########",
            "#S        #",
            "# ### ### #",
            "#   #   # #",
            "### # ### #",
            "#   #     #",
            "# ####### #",
            "#       # #",
            "####### # #",
            "#        E#",
            "###########"
        }));
        list.add(new Maze(new String[] {
            "#############",
            "#S  #       #",
            "### # ### # #",
            "#   #   # # #",
            "# ##### # # #",
            "#     # # # #",
            "##### # # # #",
            "#   # #   # #",
            "# # # ### # #",
            "# #     # # #",
            "# ##### #   #",
            "#          E#",
            "#############"
        }));
        list.add(new Maze(new String[] {
            "###############",
            "#S    #       #",
            "##### # ##### #",
            "#   # #     # #",
            "# # # ##### # #",
            "# # #     # # #",
            "# # ##### # # #",
            "# #     # # # #",
            "# ##### # # # #",
            "#     # # #   #",
            "##### # # ### #",
            "#     #     # #",
            "# ######### # #",
            "#            E#",
            "###############"
        }));
        return list;
    }

    private static List<Maze> mediumMazes() {
        List<Maze> list = new ArrayList<>();
        list.add(new Maze(new String[] {
            "#################",
            "#S  #     #     #",
            "# # # ### # ### #",
            "# # #   # #   # #",
            "# # ### # ### # #",
            "# #   # #   # # #",
            "# ### # ### # # #",
            "#   # # #   # # #",
            "### # # # ### # #",
            "#   # #   #   # #",
            "# ### ##### ### #",
            "# #     #     # #",
            "# # ### # ##### #",
            "# # #   #     # #",
            "# # # ##### # # #",
            "# #         #  E#",
            "#################"
        }));
        list.add(new Maze(new String[] {
            "###################",
            "#S      #         #",
            "# ##### # ####### #",
            "#     # #       # #",
            "##### # ####### # #",
            "#   # #       # # #",
            "# # # ####### # # #",
            "# # #       # # # #",
            "# # ####### # # # #",
            "# #       # # # # #",
            "# ####### # # # # #",
            "#       # # # # # #",
            "####### # # # # # #",
            "#     # # # # # # #",
            "# ### # # # # # # #",
            "# #   # #   # #   #",
            "# # ### ##### ### #",
            "# #              E#",
            "###################"
        }));
        list.add(new Maze(new String[] {
            "###################",
            "#S    #     #     #",
            "### # # ### # ### #",
            "#   # #   # #   # #",
            "# ##### ### ### # #",
            "#     #   # #   # #",
            "##### ### # # ### #",
            "#   #   # # #   # #",
            "# # ### # # ### # #",
            "# #   # #   #   # #",
            "# ### # ##### ### #",
            "# #   #     #     #",
            "# # ####### ##### #",
            "# #       #     # #",
            "# ####### ##### # #",
            "#       #     # # #",
            "####### ##### # # #",
            "#                E#",
            "###################"
        }));
        list.add(new Maze(new String[] {
            "#####################",
            "#S    #       #     #",
            "##### # ##### # ### #",
            "#   # #   # # # #   #",
            "# # # ### # # # # ###",
            "# # #   # # #   #   #",
            "# # ### # # ##### # #",
            "# #   # #     #   # #",
            "# ### # ##### # ### #",
            "#   # #     # # #   #",
            "### # ##### # # # ###",
            "#   #     # # # #   #",
            "# ####### # # # ### #",
            "#       # # # #   # #",
            "####### # # # ### # #",
            "#     # # #   #   # #",
            "# ### # # ### # ### #",
            "# #   #     # #     #",
            "# # ######### #######",
            "#                  E#",
            "#####################"
        }));
        return list;
    }

    private static List<Maze> hardMazes() {
        List<Maze> list = new ArrayList<>();
        list.add(new Maze(new String[] {
            "#######################",
            "#S  #       #         #",
            "### # ##### # ####### #",
            "#   # #   # #       # #",
            "# ### # # # ####### # #",
            "# #   # # #       # # #",
            "# # ### # ####### # # #",
            "# #   # #       # # # #",
            "# ### # ####### # # # #",
            "#   # #       # # # # #",
            "### # ####### # # # # #",
            "#   #       # # # # # #",
            "# ######### # # # # # #",
            "#         # # # # # # #",
            "######### # # # # # # #",
            "#       # # # # # # # #",
            "# ##### # # # # # # # #",
            "#   #   # # # # # # # #",
            "### # ### # # # # # # #",
            "#   #     # #   #   # #",
            "# ######### ##### ### #",
            "#                    E#",
            "#######################"
        }));
        list.add(new Maze(new String[] {
            "#########################",
            "#S    #       #         #",
            "##### # ##### # ####### #",
            "#   # #     # # #     # #",
            "# # # ##### # # # ### # #",
            "# # #     # # # # # # # #",
            "# # ##### # # # # # # # #",
            "# #     # # # # # # # # #",
            "# ##### # # # # # # # # #",
            "#     # # # # # # # # # #",
            "##### # # # # # # # # # #",
            "#   # # # # # # # # # # #",
            "# # # # # # # # # # # # #",
            "# # # # #   #   #   # # #",
            "# # # # ##### ### ### # #",
            "# # # #             # # #",
            "# # # ############### # #",
            "# # #                 # #",
            "# # ################### #",
            "# #                     #",
            "# ##################### #",
            "#                      E#",
            "#########################"
        }));
        list.add(new Maze(new String[] {
            "###########################",
            "#S      #         #       #",
            "##### # # ####### # ##### #",
            "#   # # #       # # #   # #",
            "# # # # ####### # # # # # #",
            "# # # #       # # # # # # #",
            "# # # ####### # # # # # # #",
            "# # #       # # # # # # # #",
            "# # ####### # # # # # # # #",
            "# #       # # # # # # # # #",
            "# ####### # # # # # # # # #",
            "#       # # # # # # # # # #",
            "####### # # # # # # # # # #",
            "#     # # # # # # # # # # #",
            "# ### # # # # # # # # # # #",
            "# #   # # # # # # # # # # #",
            "# # ### # # # # # # # # # #",
            "# #     # # # # # # # # # #",
            "# ####### # # # # # # # # #",
            "#         # # # # # # # # #",
            "########### # # # # # # # #",
            "#           # # # # # #   #",
            "############# # # # # ### #",
            "#             #   #       #",
            "############# ##### #######",
            "#                        E#",
            "###########################"
        }));
        list.add(new Maze(new String[] {
            "#############################",
            "#S  #     #       #         #",
            "### # ### # ##### # ####### #",
            "#   # #   #     # # #     # #",
            "# ### # ####### # # # ### # #",
            "# #   #       # # # # # # # #",
            "# # ######### # # # # # # # #",
            "# #         # # # # # # # # #",
            "# ######### # # # # # # # # #",
            "#         # # # # # # # # # #",
            "######### # # # # # # # # # #",
            "#       # # # # # # # # # # #",
            "# ##### # # # # # # # # # # #",
            "# #   # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # # # # # # # # # # # #",
            "# # # # #   #   #   #   # # #",
            "# # # # ### ### ### ### # # #",
            "# # # #                 # # #",
            "# # # ################### # #",
            "# # #                     # #",
            "# # ####################### #",
            "# #                         #",
            "# ###########################",
            "#                          E#",
            "#############################"
        }));
        list.add(new Maze(new String[] {
            "###############################",
            "#S      #         #           #",
            "##### # # ####### # ######### #",
            "#   # # #       # # #       # #",
            "# # # # ####### # # # ##### # #",
            "# # # #       # # # #     # # #",
            "# # # ####### # # # ##### # # #",
            "# # #       # # # #     # # # #",
            "# # ####### # # # ##### # # # #",
            "# #       # # # #     # # # # #",
            "# ####### # # # ##### # # # # #",
            "#       # # # #     # # # # # #",
            "####### # # # ##### # # # # # #",
            "#     # # # #     # # # # # # #",
            "# ### # # # ##### # # # # # # #",
            "# #   # # #     # # # # # # # #",
            "# # ### # ##### # # # # # # # #",
            "# #     #     # # # # # # # # #",
            "# ####### ### # # # # # # # # #",
            "#       #   # # # # # # # # # #",
            "####### ### # # # # # # # # # #",
            "#     #   # # # # # # # # # # #",
            "# ### ### # # # # # # # # # # #",
            "# #     # # # # # # # # # # # #",
            "# ##### # # # # # # # # # # # #",
            "#     # # # # # # # # # # # # #",
            "##### # # # # # # # # # # # # #",
            "#     # # #   #   #   #   # # #",
            "# ##### # ### ### ### ### # # #",
            "# #     #                 # # #",
            "# # ####################### # #",
            "# #                         # #",
            "# ########################### #",
            "#                            E#",
            "###############################"
        }));
        return list;
    }
}

// ===== Player =====
class Player {
    private int col;
    private int row;
    private int facing = 0;

    public Player(Point start) {
        this.col = start.x;
        this.row = start.y;
    }

    public int getCol() { return col; }
    public int getRow() { return row; }
    public int getFacing() { return facing; }

    public boolean tryMove(int dx, int dy, Maze maze) {
        if (dx == 0 && dy == 0) return false;
        if (dx > 0) facing = 2;
        else if (dx < 0) facing = 1;
        else if (dy > 0) facing = 0;
        else facing = 3;

        int nc = col + dx;
        int nr = row + dy;
        if (!maze.isWalkable(nc, nr)) return false;
        col = nc;
        row = nr;
        return true;
    }

    public void resetTo(Point p) {
        this.col = p.x;
        this.row = p.y;
    }
}

// ===== SoundManager =====
class SoundManager {
    private boolean enabled = true;
    private float volume = 0.6f;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public float getVolume() { return volume; }
    public void setVolume(float v) {
        if (v < 0f) v = 0f;
        if (v > 1f) v = 1f;
        this.volume = v;
    }

    public void playStep()  { tone(220, 60); }
    public void playBump()  { tone(110, 90); }
    public void playClear() { tone(660, 120); tone(880, 160); }
    public void playDeath() { tone(180, 220); tone(120, 260); }
    public void playClick() { tone(520, 50); }

    private void tone(double freq, int millis) {
        if (!enabled || volume <= 0f) return;
        new Thread(() -> {
            try {
                float sampleRate = 44100f;
                int frames = (int) (sampleRate * millis / 1000.0);
                byte[] buf = new byte[frames * 2];
                double amp = 0.4 * volume * 32767;
                for (int i = 0; i < frames; i++) {
                    double env = Math.min(1.0, Math.min(i, frames - i) / (sampleRate * 0.01));
                    short s = (short) (Math.sin(2 * Math.PI * freq * i / sampleRate) * amp * env);
                    buf[2 * i] = (byte) (s & 0xff);
                    buf[2 * i + 1] = (byte) ((s >> 8) & 0xff);
                }
                AudioFormat fmt = new AudioFormat(sampleRate, 16, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(fmt)) {
                    line.open(fmt);
                    line.start();
                    line.write(buf, 0, buf.length);
                    line.drain();
                }
            } catch (Exception ignored) { }
        }, "SoundManager-tone").start();
    }
}

// ===== LeaderboardManager =====
class LeaderboardManager {
    public static final int MAX_ENTRIES = 50;
    private final File file;
    private final List<Entry> entries = new ArrayList<>();

    public LeaderboardManager() {
        File dir = new File(System.getProperty("user.home"), ".mazecrawler");
        if (!dir.exists()) dir.mkdirs();
        this.file = new File(dir, "leaderboard.dat");
        load();
    }

    public synchronized List<Entry> getEntries() {
        List<Entry> copy = new ArrayList<>(entries);
        copy.sort((a, b) -> {
            if (a.score != b.score) return Integer.compare(b.score, a.score);
            return Long.compare(a.timeMillis, b.timeMillis);
        });
        return copy;
    }

    public synchronized void addEntry(Entry e) {
        entries.add(e);
        List<Entry> sorted = getEntries();
        if (sorted.size() > MAX_ENTRIES) {
            sorted = sorted.subList(0, MAX_ENTRIES);
        }
        entries.clear();
        entries.addAll(sorted);
        save();
    }

    public synchronized void clear() {
        entries.clear();
        save();
    }

    private void load() {
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Entry e = Entry.parse(line);
                if (e != null) entries.add(e);
            }
        } catch (IOException ignored) {}
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Entry e : entries) {
                bw.write(e.serialize());
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }

    public static class Entry {
        public final String username;
        public final String difficulty;
        public final int score;
        public final int stagesCleared;
        public final long timeMillis;
        public final long epochMillis;

        public Entry(String username, String difficulty, int score,
                     int stagesCleared, long timeMillis, long epochMillis) {
            this.username = username;
            this.difficulty = difficulty;
            this.score = score;
            this.stagesCleared = stagesCleared;
            this.timeMillis = timeMillis;
            this.epochMillis = epochMillis;
        }

        public String serialize() {
            return escape(username) + "|" + escape(difficulty) + "|" + score
                + "|" + stagesCleared + "|" + timeMillis + "|" + epochMillis;
        }

        public static Entry parse(String line) {
            try {
                String[] p = line.split("\\|", -1);
                if (p.length < 6) return null;
                return new Entry(unescape(p[0]), unescape(p[1]),
                    Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                    Long.parseLong(p[4]), Long.parseLong(p[5]));
            } catch (Exception e) {
                return null;
            }
        }

        private static String escape(String s) {
            return s.replace("\\", "\\\\").replace("|", "\\p").replace("\n", " ");
        }
        private static String unescape(String s) {
            return s.replace("\\p", "|").replace("\\\\", "\\");
        }
    }
}

// ===== MainMenuPanel =====
class MainMenuPanel extends JPanel {
    private final GameFrame frame;
    private final JTextField usernameField = new JTextField(14);
    private final JComboBox<Difficulty> difficultyBox = new JComboBox<>(Difficulty.values());
    private float animPhase = 0f;
    private final Timer animTimer;

    public MainMenuPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        setBackground(new Color(0x1B2030));

        animTimer = new Timer(33, e -> {
            animPhase += 0.015f;
            if (animPhase > 1000f) animPhase = 0f;
            repaint();
        });
        animTimer.start();

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("MAZE  CRAWLER");
        title.setForeground(new Color(0xE8EAF2));
        title.setFont(loadFont(56f).deriveFont(Font.BOLD));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Find the exit. Don't touch the walls.");
        subtitle.setForeground(new Color(0xA8B0C8));
        subtitle.setFont(loadFont(16f));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(8, 0, 32, 0));

        JLabel userLbl = new JLabel("Username");
        userLbl.setForeground(new Color(0xC8CDDC));
        userLbl.setFont(loadFont(14f));
        userLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField.setMaximumSize(new Dimension(280, 38));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setFont(loadFont(16f));
        usernameField.setOpaque(true);
        usernameField.setBackground(new Color(0x2A3142));
        usernameField.setForeground(new Color(0xE8EAF2));
        usernameField.setCaretColor(new Color(0xE8EAF2));
        usernameField.setSelectedTextColor(new Color(0x1B2030));
        usernameField.setSelectionColor(new Color(0xE8B57A));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x4A5169), 1, true),
            new EmptyBorder(6, 10, 6, 10)));

        JLabel diffLbl = new JLabel("Difficulty");
        diffLbl.setForeground(new Color(0xC8CDDC));
        diffLbl.setFont(loadFont(14f));
        diffLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        diffLbl.setBorder(new EmptyBorder(16, 0, 4, 0));

        difficultyBox.setMaximumSize(new Dimension(280, 38));
        difficultyBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyBox.setFont(loadFont(15f));
        difficultyBox.setOpaque(true);
        difficultyBox.setBackground(new Color(0xF1E9DB));
        difficultyBox.setForeground(new Color(0x1B2030));
        difficultyBox.setBorder(BorderFactory.createLineBorder(new Color(0x4A5169), 1, true));
        difficultyBox.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                                                                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Difficulty) setText(((Difficulty) value).label);
                c.setFont(loadFont(15f));
                if (isSelected) {
                    c.setBackground(new Color(0xE8B57A));
                    c.setForeground(new Color(0x1B2030));
                } else {
                    c.setBackground(new Color(0xF1E9DB));
                    c.setForeground(new Color(0x1B2030));
                }
                ((JLabel) c).setBorder(new EmptyBorder(6, 10, 6, 10));
                ((JLabel) c).setOpaque(true);
                return c;
            }
        });
        difficultyBox.setSelectedItem(Difficulty.EASY);

        JButton startBtn = createMenuButton("START GAME", new Color(0x5B9FB5));
        startBtn.addActionListener(this::onStart);

        JButton settingsBtn = createMenuButton("SETTINGS", new Color(0x4A5169));
        settingsBtn.addActionListener(e -> {
            frame.getSound().playClick();
            frame.showCard(GameFrame.CARD_SETTINGS);
        });

        JButton lbBtn = createMenuButton("LEADERBOARD", new Color(0x4A5169));
        lbBtn.addActionListener(e -> {
            frame.getSound().playClick();
            frame.showCard(GameFrame.CARD_LEADERBOARD);
        });

        JButton quitBtn = createMenuButton("QUIT", new Color(0x9E5560));
        quitBtn.addActionListener(e -> System.exit(0));

        card.add(title);
        card.add(subtitle);
        card.add(userLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(diffLbl);
        card.add(difficultyBox);
        card.add(Box.createVerticalStrut(28));
        card.add(startBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(settingsBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(lbBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(quitBtn);

        add(card);
    }

    private JButton createMenuButton(String text, Color base) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(280, 44));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setForeground(UiColors.readableTextOn(base));
        b.setFont(loadFont(15f).deriveFont(Font.BOLD));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setBackground(base);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void onStart(ActionEvent e) {
        String name = usernameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a username before starting.",
                "Username required", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocusInWindow();
            return;
        }
        if (name.length() > 16) name = name.substring(0, 16);
        Difficulty d = (Difficulty) difficultyBox.getSelectedItem();
        frame.getSound().playClick();
        frame.getGamePanel().startNewGame(name, d);
        frame.showCard(GameFrame.CARD_GAME);
    }

    private static Font loadFont(float size) {
        String[] candidates = { "Menlo", "Consolas", "Monaco", "Courier New", "Monospaced" };
        for (String name : candidates) {
            Font f = new Font(name, Font.PLAIN, (int) size);
            if (f.getFamily().equalsIgnoreCase(name) || name.equals("Monospaced")) {
                return f.deriveFont(size);
            }
        }
        return new Font(Font.MONOSPACED, Font.PLAIN, (int) size);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        float t = animPhase;
        Color a = Color.getHSBColor(0.58f + 0.03f * (float) Math.sin(t), 0.30f, 0.22f);
        Color b = Color.getHSBColor(0.68f + 0.03f * (float) Math.cos(t * 0.8f), 0.35f, 0.14f);
        g2.setPaint(new GradientPaint(0, 0, a, w, h, b));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 255, 255, 14));
        int step = 28;
        int offset = (int) ((animPhase * 30) % step);
        for (int x = -step + offset; x < w; x += step) g2.drawLine(x, 0, x, h);
        for (int y = -step + offset; y < h; y += step) g2.drawLine(0, y, w, y);

        g2.setPaint(new RadialGradientPaint(
            new Point(w / 2, h / 2), Math.max(w, h) * 0.7f,
            new float[]{0f, 1f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 140)}));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }
}

// ===== GameOverPanel =====
class GameOverPanel extends JPanel {
    private final GameFrame frame;
    private final JLabel headline = new JLabel("GAME OVER", SwingConstants.CENTER);
    private final JLabel detail = new JLabel("", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);
    private String lastUsername;
    private Difficulty lastDifficulty;
    private boolean recorded = false;

    public GameOverPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        setBackground(new Color(0x1B2030));

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 60, 40, 60));

        headline.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        headline.setForeground(new Color(0xD17878));
        headline.setAlignmentX(Component.CENTER_ALIGNMENT);

        detail.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        detail.setForeground(new Color(0xC8CDDC));
        detail.setAlignmentX(Component.CENTER_ALIGNMENT);
        detail.setBorder(new EmptyBorder(12, 0, 4, 0));

        scoreLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
        scoreLabel.setForeground(new Color(0xE8EAF2));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setBorder(new EmptyBorder(8, 0, 28, 0));

        JButton again = makeBtn("PLAY AGAIN", new Color(0xFE8B68));
        again.addActionListener(e -> {
            frame.getSound().playClick();
            if (lastUsername != null && lastDifficulty != null) {
                frame.getGamePanel().startNewGame(lastUsername, lastDifficulty);
                frame.showCard(GameFrame.CARD_GAME);
            } else {
                frame.showCard(GameFrame.CARD_MENU);
            }
        });

        JButton menu = makeBtn("MAIN MENU", new Color(0x4A5169));
        menu.addActionListener(e -> {
            frame.getSound().playClick();
            frame.showCard(GameFrame.CARD_MENU);
        });

        JButton lb = makeBtn("LEADERBOARD", new Color(0x4A5169));
        lb.addActionListener(e -> {
            frame.getSound().playClick();
            frame.showCard(GameFrame.CARD_LEADERBOARD);
        });

        card.add(headline);
        card.add(detail);
        card.add(scoreLabel);
        card.add(again);
        card.add(Box.createVerticalStrut(10));
        card.add(menu);
        card.add(Box.createVerticalStrut(10));
        card.add(lb);

        add(card);
    }

    private JButton makeBtn(String text, Color base) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(280, 44));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setForeground(UiColors.readableTextOn(base));
        b.setBackground(base);
        b.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    public void showResult(String username, Difficulty difficulty,
                           int score, int stagesCleared, long elapsedMillis, boolean won) {
        this.lastUsername = username;
        this.lastDifficulty = difficulty;

        if (won) {
            headline.setText("YOU ESCAPED!");
            headline.setForeground(new Color(0x8FBF9F));
        } else {
            headline.setText("GAME OVER");
            headline.setForeground(new Color(0xD17878));
        }
        String t = String.format("%02d:%02d.%01d",
            (elapsedMillis / 60000), (elapsedMillis / 1000) % 60, (elapsedMillis / 100) % 10);
        detail.setText(username + " — " + difficulty.label
            + "  •  Stages: " + stagesCleared + " / " + difficulty.totalStages
            + "  •  Time: " + t);
        scoreLabel.setText("SCORE: " + score);

        if (!recorded) {
            frame.getLeaderboard().addEntry(new LeaderboardManager.Entry(
                username, difficulty.label, score, stagesCleared,
                elapsedMillis, System.currentTimeMillis()));
        }
        recorded = true;
        SwingUtilities.invokeLater(() -> recorded = false);
    }
}

// ===== LeaderboardPanel =====
class LeaderboardPanel extends JPanel {
    private final GameFrame frame;
    private final DefaultTableModel model;
    private final JTable table;

    public LeaderboardPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(0x1B2030));
        setBorder(new EmptyBorder(28, 40, 28, 40));

        JLabel title = new JLabel("LEADERBOARD");
        title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 32));
        title.setForeground(new Color(0xE8C871));
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new Object[] { "#", "Player", "Difficulty", "Score", "Stages", "Time", "Date" }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        table.setForeground(new Color(0xE8EAF2));
        table.setBackground(new Color(0x232838));
        table.setGridColor(new Color(0x394056));
        table.setSelectionBackground(new Color(0x4A5169));
        table.setSelectionForeground(new Color(0xE8EAF2));
        table.getTableHeader().setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        table.getTableHeader().setForeground(new Color(0xC8CDDC));
        table.getTableHeader().setBackground(new Color(0x2A3142));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(0x232838));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0x4A5169)));
        add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 12));

        JButton back = makeBtn("BACK", new Color(0x4A5169));
        back.addActionListener(e -> {
            frame.getSound().playClick();
            frame.showCard(GameFrame.CARD_MENU);
        });

        JButton clear = makeBtn("CLEAR ALL", new Color(0x9E5560));
        clear.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                "Permanently clear the leaderboard?", "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                frame.getLeaderboard().clear();
                refresh();
            }
        });

        south.add(back);
        south.add(clear);
        add(south, BorderLayout.SOUTH);
    }

    private JButton makeBtn(String text, Color base) {
        JButton b = new JButton(text);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setForeground(UiColors.readableTextOn(base));
        b.setBackground(base);
        b.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    public void refresh() {
        model.setRowCount(0);
        List<LeaderboardManager.Entry> entries = frame.getLeaderboard().getEntries();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int rank = 1;
        for (LeaderboardManager.Entry e : entries) {
            String t = String.format("%02d:%02d.%01d",
                (e.timeMillis / 60000), (e.timeMillis / 1000) % 60, (e.timeMillis / 100) % 10);
            model.addRow(new Object[] {
                rank++, e.username, e.difficulty, e.score,
                e.stagesCleared, t, sdf.format(new Date(e.epochMillis))
            });
        }
        if (entries.isEmpty()) {
            model.addRow(new Object[]{ "-", "No scores yet", "-", "-", "-", "-", "-" });
        }
    }
}

// ===== SettingsPanel =====
class SettingsPanel extends JPanel {
    public SettingsPanel(GameFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(0x1B2030));

        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("SETTINGS");
        title.setForeground(new Color(0xC8CDDC));
        title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 24, 0));

        JCheckBox soundBox = new JCheckBox("Sound enabled");
        soundBox.setSelected(frame.getSound().isEnabled());
        soundBox.setOpaque(false);
        soundBox.setForeground(new Color(0xE8EAF2));
        soundBox.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        soundBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundBox.addActionListener(e -> frame.getSound().setEnabled(soundBox.isSelected()));

        JLabel volLbl = new JLabel("Volume");
        volLbl.setForeground(new Color(0xC8CDDC));
        volLbl.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        volLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        volLbl.setBorder(new EmptyBorder(20, 0, 4, 0));

        JSlider vol = new JSlider(0, 100, (int) (frame.getSound().getVolume() * 100));
        vol.setMaximumSize(new Dimension(280, 40));
        vol.setOpaque(false);
        vol.setForeground(new Color(0xE8EAF2));
        vol.setAlignmentX(Component.CENTER_ALIGNMENT);
        vol.addChangeListener(e -> frame.getSound().setVolume(vol.getValue() / 100f));
        vol.addChangeListener(e -> {
            if (!vol.getValueIsAdjusting()) frame.getSound().playClick();
        });

        JButton back = makeBtn("BACK", new Color(0x5B9FB5));
        back.addActionListener(e -> {
            frame.getSound().playClick();
            frame.showCard(GameFrame.CARD_MENU);
        });

        JButton quit = makeBtn("QUIT GAME", new Color(0x9E5560));
        quit.addActionListener(e -> System.exit(0));

        card.add(title);
        card.add(soundBox);
        card.add(volLbl);
        card.add(vol);
        card.add(Box.createVerticalStrut(28));
        card.add(back);
        card.add(Box.createVerticalStrut(10));
        card.add(quit);

        add(card);
    }

    private JButton makeBtn(String text, Color base) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(280, 44));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setForeground(UiColors.readableTextOn(base));
        b.setBackground(base);
        b.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }
}

// ===== GamePanel =====
class GamePanel extends JPanel {
    private static final int BASE_CELL = 28;
    private final GameFrame frame;
    private String username = "";
    private Difficulty difficulty = Difficulty.EASY;
    private List<Maze> stages;
    private int stageIndex;
    private Maze maze;
    private Player player;
    private int score;
    private long stageStartMillis;
    private long runStartMillis;
    private boolean alive = true;
    private float exitPulse = 0f;
    private final Timer animTimer;
    private float deathFlash = 0f;

    public GamePanel(GameFrame frame) {
        this.frame = frame;
        setBackground(new Color(0x1B2030));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { onKey(e); }
        });

        animTimer = new Timer(33, e -> {
            exitPulse += 0.08f;
            if (deathFlash > 0f) deathFlash = Math.max(0f, deathFlash - 0.06f);
            repaint();
        });
        animTimer.start();
    }

    public void startNewGame(String username, Difficulty difficulty) {
        this.username = username;
        this.difficulty = difficulty;
        this.stages = MazeLibrary.forDifficulty(difficulty);
        this.stageIndex = 0;
        this.score = 0;
        this.runStartMillis = System.currentTimeMillis();
        loadStage();
    }

    private void loadStage() {
        this.maze = stages.get(stageIndex);
        this.player = new Player(maze.getStart());
        this.stageStartMillis = System.currentTimeMillis();
        this.alive = true;
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    private void onKey(KeyEvent e) {
        if (!alive) return;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            frame.showCard(GameFrame.CARD_MENU);
            return;
        }

        int dx = 0, dy = 0;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    case KeyEvent.VK_W: dy = -1; break;
            case KeyEvent.VK_DOWN:  case KeyEvent.VK_S: dy =  1; break;
            case KeyEvent.VK_LEFT:  case KeyEvent.VK_A: dx = -1; break;
            case KeyEvent.VK_RIGHT: case KeyEvent.VK_D: dx =  1; break;
            default: return;
        }

        int nc = player.getCol() + dx;
        int nr = player.getRow() + dy;
        if (!maze.isWalkable(nc, nr)) {
            killPlayer();
            return;
        }

        player.tryMove(dx, dy, maze);
        frame.getSound().playStep();

        if (maze.isExit(player.getCol(), player.getRow())) {
            onStageCleared();
        }
    }

    private void killPlayer() {
        alive = false;
        deathFlash = 1f;
        frame.getSound().playDeath();
        long elapsed = System.currentTimeMillis() - runStartMillis;
        frame.getGameOverPanel().showResult(username, difficulty,
            score, stageIndex, elapsed, false);
        Timer t = new Timer(450, e -> frame.showCard(GameFrame.CARD_GAMEOVER));
        t.setRepeats(false);
        t.start();
    }

    private void onStageCleared() {
        long stageMillis = System.currentTimeMillis() - stageStartMillis;
        int base = 500 * (stageIndex + 1);
        int timeBonus = Math.max(0, 2000 - (int)(stageMillis / 10));
        int gain = (int) Math.round((base + timeBonus) * difficulty.multiplier);
        score += gain;
        frame.getSound().playClear();

        stageIndex++;
        if (stageIndex >= stages.size()) {
            long elapsed = System.currentTimeMillis() - runStartMillis;
            int finalBonus = (int) Math.round(1000 * difficulty.multiplier);
            score += finalBonus;
            frame.getGameOverPanel().showResult(username, difficulty,
                score, stageIndex, elapsed, true);
            frame.showCard(GameFrame.CARD_GAMEOVER);
        } else {
            loadStage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int hudHeight = 56;
        int availW = getWidth();
        int availH = getHeight() - hudHeight;
        int cell = Math.min(availW / maze.getCols(), availH / maze.getRows());
        if (cell < 8) cell = 8;
        int mazeW = cell * maze.getCols();
        int mazeH = cell * maze.getRows();
        int ox = (availW - mazeW) / 2;
        int oy = hudHeight + (availH - mazeH) / 2;

        g2.setPaint(new GradientPaint(0, 0, new Color(0x232838), 0, getHeight(), new Color(0x141826)));
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawHud(g2);

        g2.setColor(new Color(0x2A3142));
        g2.fillRoundRect(ox - 8, oy - 8, mazeW + 16, mazeH + 16, 12, 12);

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                int x = ox + c * cell;
                int y = oy + r * cell;
                char ch = maze.cellAt(c, r);
                if (ch == Maze.WALL) {
                    g2.setPaint(new GradientPaint(x, y, new Color(0x5B9FB5),
                        x, y + cell, new Color(0x3D7A8C)));
                    g2.fillRect(x, y, cell, cell);
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.drawLine(x, y, x + cell, y);
                } else {
                    g2.setColor(new Color(0x141826));
                    g2.fillRect(x, y, cell, cell);
                    if (ch == Maze.START) {
                        g2.setColor(new Color(143, 191, 159, 110));
                        g2.fillOval(x + cell / 4, y + cell / 4, cell / 2, cell / 2);
                    } else if (ch == Maze.EXIT) {
                        float pulse = (float)(0.5 + 0.5 * Math.sin(exitPulse));
                        Color core = new Color(255, 200, 80);
                        g2.setColor(new Color(core.getRed(), core.getGreen(), core.getBlue(),
                            (int)(120 + 120 * pulse)));
                        int pad = (int)(cell * (0.15 + 0.1 * (1f - pulse)));
                        g2.fillOval(x + pad, y + pad, cell - 2 * pad, cell - 2 * pad);
                    }
                }
            }
        }

        int px = ox + player.getCol() * cell;
        int py = oy + player.getRow() * cell;
        int pad = Math.max(2, cell / 6);
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillOval(px + pad + 2, py + pad + 4, cell - 2 * pad, cell - 2 * pad);
        g2.setPaint(new GradientPaint(px, py, new Color(0xE8B57A),
            px, py + cell, new Color(0xD88A55)));
        g2.fillOval(px + pad, py + pad, cell - 2 * pad, cell - 2 * pad);
        g2.setColor(new Color(255, 255, 255, 140));
        g2.fillOval(px + pad + 2, py + pad + 2, (cell - 2 * pad) / 3, (cell - 2 * pad) / 3);

        if (deathFlash > 0f) {
            g2.setColor(new Color(209, 120, 120, (int)(170 * deathFlash)));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }

    private void drawHud(Graphics2D g2) {
        int w = getWidth();
        g2.setPaint(new GradientPaint(0, 0, new Color(0x2A3142), 0, 56, new Color(0x1B2030)));
        g2.fillRect(0, 0, w, 56);
        g2.setColor(new Color(0x4A5169));
        g2.drawLine(0, 55, w, 55);

        g2.setColor(new Color(0xC8CDDC));
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        g2.drawString("PLAYER:", 16, 22);
        g2.setColor(new Color(0xE8EAF2));
        g2.drawString(username, 16, 42);

        long elapsed = System.currentTimeMillis() - runStartMillis;
        String t = String.format("%02d:%02d.%01d",
            (elapsed / 60000), (elapsed / 1000) % 60, (elapsed / 100) % 10);

        g2.setColor(new Color(0xC8CDDC));
        g2.drawString("STAGE", w / 2 - 80, 22);
        g2.drawString("TIME",  w / 2 - 10, 22);
        g2.drawString("SCORE", w / 2 + 70, 22);

        g2.setColor(new Color(0xE8EAF2));
        g2.drawString((stageIndex + 1) + " / " + stages.size(), w / 2 - 80, 42);
        g2.drawString(t, w / 2 - 10, 42);
        g2.drawString(String.valueOf(score), w / 2 + 70, 42);

        g2.setColor(new Color(0xA8B0C8));
        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        g2.drawString("WASD / ARROWS to move    ESC to quit", w - 320, 34);

        g2.setColor(new Color(0xE8C871));
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        g2.drawString(difficulty.label.toUpperCase(), w - 80, 14);
    }
}

// ===== GameFrame =====
class GameFrame extends JFrame {
    public static final String CARD_MENU = "MENU";
    public static final String CARD_GAME = "GAME";
    public static final String CARD_GAMEOVER = "GAMEOVER";
    public static final String CARD_LEADERBOARD = "LEADERBOARD";
    public static final String CARD_SETTINGS = "SETTINGS";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final MainMenuPanel mainMenu;
    private final GamePanel gamePanel;
    private final GameOverPanel gameOverPanel;
    private final LeaderboardPanel leaderboardPanel;
    private final SettingsPanel settingsPanel;
    private final SoundManager sound = new SoundManager();
    private final LeaderboardManager leaderboard = new LeaderboardManager();

    public GameFrame() {
        setTitle("Maze Crawler");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        mainMenu = new MainMenuPanel(this);
        gamePanel = new GamePanel(this);
        gameOverPanel = new GameOverPanel(this);
        leaderboardPanel = new LeaderboardPanel(this);
        settingsPanel = new SettingsPanel(this);

        root.add(mainMenu, CARD_MENU);
        root.add(gamePanel, CARD_GAME);
        root.add(gameOverPanel, CARD_GAMEOVER);
        root.add(leaderboardPanel, CARD_LEADERBOARD);
        root.add(settingsPanel, CARD_SETTINGS);

        setContentPane(root);
        showCard(CARD_MENU);
    }

    public void showCard(String name) {
        cards.show(root, name);
        Component current = getCurrentVisibleComponent();
        if (current != null) current.requestFocusInWindow();
        if (CARD_LEADERBOARD.equals(name)) {
            leaderboardPanel.refresh();
        }
    }

    private Component getCurrentVisibleComponent() {
        for (Component c : root.getComponents()) {
            if (c.isVisible()) return c;
        }
        return null;
    }

    public SoundManager getSound() { return sound; }
    public LeaderboardManager getLeaderboard() { return leaderboard; }
    public MainMenuPanel getMainMenu() { return mainMenu; }
    public GamePanel getGamePanel() { return gamePanel; }
    public GameOverPanel getGameOverPanel() { return gameOverPanel; }
}

// ===== Main =====
public class MazeCrawler {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}