import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Field extends JFrame implements KeyListener, MouseListener, MouseMotionListener {
	
	public static void main(String[] args) {
		Field field = new Field();
		field.setVisible(true);
	}
	
	public boolean running = false; // If the Game of Life is running
	public long gen = 0;
	
	private int c = 0;
	
	GameOfLife gameOfLife = new GameOfLife();
	
	Panel panel = new Panel();
	
	public Field() {
		setTitle("Game Of Life");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, (int)(screen.width/1.5), (int)(screen.height/1.5));
		setLocationRelativeTo(null);
		setContentPane(panel);
		setFocusable(true);
		setFocusTraversalKeysEnabled(true);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		String img =          "                        #\n"
		                    + "                      # #\n"
		                    + "            ##      ##            ##\n"
		                    + "           #   #    ##            ##\n"
		                    + "##        #     #   ##\n"
		                    + "##        #   # ##    # #\n"
		                    + "          #     #       #\n"
		                    + "           #   #\n"
		                    + "            ##\n";
		draw(60, 20, img);
		
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if(c > 10) {
					if(running) gameOfLife.update();
					c = 0;
				} c++;
				repaint();
			}
		}, 0, 5, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int moveSpeed = 6;
		if(e.isShiftDown()) moveSpeed *= 2;
		switch(e.getKeyCode()) {
			case KeyEvent.VK_A: panel.offsetX -= moveSpeed; break;
			case KeyEvent.VK_D: panel.offsetX += moveSpeed; break;
			case KeyEvent.VK_W: panel.offsetY -= moveSpeed; break;
			case KeyEvent.VK_S: panel.offsetY += moveSpeed; break;
			case KeyEvent.VK_SPACE: running = !running; break;
			case KeyEvent.VK_C:
				if(e.isShiftDown() && e.isAltDown() && !running) gameOfLife.cells.clear(); break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
    public void mouseClicked(MouseEvent e) {}
	
	@Override
    public void mousePressed(MouseEvent e) {
		if(!running) {
			int[] f = getField(e.getX(), e.getY());
			if(e.getButton() == MouseEvent.BUTTON1) gameOfLife.add(f[0], f[1]);
			if(e.getButton() == MouseEvent.BUTTON2) System.out.println("x: " + f[0] + ", y: " + f[1] + "\nNearby: " + gameOfLife.nearbyCount(f[0], f[1], gameOfLife.cells));
			if(e.getButton() == MouseEvent.BUTTON3) gameOfLife.remove(f[0], f[1]);
		}
	}

	@Override
    public void mouseReleased(MouseEvent e) {}

	@Override
    public void mouseEntered(MouseEvent e) {}

	@Override
    public void mouseExited(MouseEvent e) {}
	
	@Override
    public void mouseDragged(MouseEvent e) {
		if(!running) {
			int[] f = getField(e.getX(), e.getY());
			if(e.getButton() == MouseEvent.BUTTON1) gameOfLife.add(f[0], f[1]);
			if(e.getButton() == MouseEvent.BUTTON3) gameOfLife.remove(f[0], f[1]);
		}
	}

	@Override
    public void mouseMoved(MouseEvent e) {}
	
	public int[] getField(int xScreen, int yScreen) {
		return new int[] {(xScreen-panel.offsetX)/panel.tileWidth, (yScreen-panel.offsetY)/panel.tileHeight};
	}
	
	public void draw(int x, int y, String structure) {
		String[] lines = structure.split("\n");
		for(int i = 0; i<lines.length; i++) {
			for(int j = 0; j<lines[i].length(); j++) {
				if(lines[i].charAt(j) != ' ') gameOfLife.add(x+j, y+i);
				else gameOfLife.remove(x+j, y+i);
			}
		}
	}
	
	class Panel extends JPanel {
		int tileWidth = 16;
		int tileHeight = 16;
		
		int offsetX = 0;
		int offsetY = 0;
		
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			g2.setColor(Color.black);									// Background
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			g2.setColor(new Color(90, 90, 90));							// Cells
			try {
				for(GameOfLife.Cell c : gameOfLife.cells) {
					g2.fillRect(c.x * tileWidth + offsetX, c.y * tileHeight + offsetY, tileWidth, tileHeight);
				}
			} catch(Exception e) {}
			g2.setColor(Color.gray);									// Grid
			for(int i = 0; i<this.getWidth()+tileWidth; i += tileWidth) g2.drawLine(i + offsetX%tileWidth, 0, i + offsetX%tileWidth, this.getHeight());
			for(int i = 0; i<this.getHeight()+tileHeight; i += tileHeight) g2.drawLine(0, i + offsetY%tileHeight, this.getWidth(), i + offsetY%tileHeight);
			
			if(running) {												// Indicator
				g2.setColor(Color.green);
				g2.setStroke(new BasicStroke(2));
				g2.drawRect(0, 0, this.getWidth(), this.getHeight());
			}
			
			g2.setColor(Color.white);									// Information
			String popString = "Population: " + gameOfLife.cells.size();
			g2.drawString(popString, this.getWidth()-10-g2.getFontMetrics().stringWidth(popString), this.getHeight()-10-2-g2.getFontMetrics().getHeight());
			String genString = "Generation: " + gen;
			g2.drawString(genString, this.getWidth()-10-g2.getFontMetrics().stringWidth(genString), this.getHeight()-10);
		}
	}

}
