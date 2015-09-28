import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.xml.soap.Text;


public class TetrisPanel extends JPanel
{
	private JLabel instructions, tetris;
	private JPanel up;

	public TetrisPanel()
	{


		Font font= new Font("Arial",Font.PLAIN,20);
		instructions = new JLabel("To start/pause playing press space", SwingConstants.CENTER );
		instructions.setFont(font);
		instructions.setForeground(Color.red.darker());

		tetris = new JLabel("Tetris", SwingConstants.CENTER );
		tetris.setFont(new Font("Algerian",Font.PLAIN,40));

		Play p = new Play();
		up = new JPanel();

		this.requestFocusInWindow();
		this.addKeyListener(p);
		this.setFocusable(true);
		up.setLayout(new BorderLayout());
		up.add(instructions, BorderLayout.CENTER);
		up.add(tetris, BorderLayout.NORTH);

		setLayout(new BorderLayout());
		add(up, BorderLayout.NORTH);
		add(p, BorderLayout.CENTER);

	}

	private class Play extends JPanel implements ActionListener, KeyListener
	{
		private int SIZE = 20, widthNum = 10, heightNum = 22;
		private int pWidth, pHeight, i,j, height, width, x, y, startPoint;
		private int[][] board;
		private Timer timer;
		private Board gameBoard;
		private boolean running, startGame;


		public Play()
		{
			super();
			gameBoard = new Board(widthNum, heightNum);
			board = gameBoard.getBoard();
			timer = new Timer(400, this);
			this.running = false;
			startGame = true;

		}

		public void keyTyped(KeyEvent event){}
		public void keyReleased(KeyEvent e) {}
		public void keyPressed(KeyEvent event) 
		{	
			if(!gameBoard.getGameOver())
			{
				if(running)
				{
					// Pressed arrow up
					if (event.getKeyCode() == KeyEvent.VK_UP)
					{
						gameBoard.rotate();
					}
					// Pressed arrow left
					else if (event.getKeyCode() == KeyEvent.VK_LEFT)
					{
						gameBoard.moveLeft();
					}
					// Pressed arrow right
					else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						gameBoard.moveRight();
					}
					else if (event.getKeyCode() == KeyEvent.VK_DOWN)
					{
						gameBoard.moveDown();
					}
					else if(event.getKeyCode() == KeyEvent.VK_SPACE)
					{
						running = false;
						timer.stop();
					}

					gameBoard.update();
					repaint();
				}
				else
				{
					if(event.getKeyCode() == KeyEvent.VK_SPACE)
					{
						start();
					}
				}

			}
			else
			{
				if(event.getKeyCode() == KeyEvent.VK_SPACE)
				{
					start();
				}
			}

		}


		public void actionPerformed(ActionEvent arg0)
		{
			if(!gameBoard.getGameOver())
			{
				gameBoard.moveDown();
				gameBoard.update();
				repaint();

			}
			else
			{
				timer.stop();
				//JOptionPane.showMessageDialog(null, "Game Over!", "", JOptionPane.INFORMATION_MESSAGE);
			}

		}
		public void start()
		{
			if(gameBoard.getGameOver()) // Start over the game
			{
				gameBoard = new Board(widthNum, heightNum);
				board = gameBoard.getBoard();
				timer = new Timer(400, this);
				this.running = false;
				startGame = true;
			}
			if(startGame) //Game just started
			{
				running = true;
				startGame = false;
				timer.start();
				gameBoard.createNewShape();
			}
			else // Resume from pause
			{
				running = true;
				timer.start();
			}
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			//Define variables
			pWidth = this.getWidth();
			pHeight = this.getHeight();
			Color blue = new Color(0,0,255);
			x = (pWidth-SIZE*widthNum)/3;
			startPoint = 10;
			y = pHeight - startPoint;
			height = heightNum*SIZE;
			width = SIZE*widthNum;

			//Draw background
			g2d.setColor(new Color(245,245,245));
			g2d.fillRect(0, 0, pWidth, pHeight);

			//Draw game area
			g2d.setColor(blue);
			g2d.fillRect(x-2, pHeight- height - startPoint, 2, height);
			g2d.fillRect(x, y, width, 2);
			g2d.fillRect(x + width, pHeight- height - startPoint , 2, height);

			//Draw game board
			for(i=0; i< board[0].length; i++)
			{
				for(j=0; j<board.length; j++)
				{
					drawSquare(board[j][i], x+j*SIZE, y-i*SIZE, g2d);
				}
			}
			//Draw all information about the game - level, score, next shape
			drawInformation(g2d);
			if(gameBoard.getGameOver())
				drawGameOver(g2d);
			if(gameBoard.getWasErase())
				eraseEffect(g2d);


		}


		public void drawInformation(Graphics2D g2d)
		{
			//Draw next shape
			int space = 20;
			int blockH = (height-space*2)/3;
			int blockW = 120;
			Shape ns;
			int[][] nextShape;
			int blockX = x + width + space, blockY = pHeight- height - startPoint + blockH;
			g2d.setColor(new Color(0xB0A3FF));
			for(i =0; i<3; i++)
			{
				g2d.fillRoundRect(x + width + space , pHeight- height - startPoint + blockH*i + i*space, blockW, blockH, 15, 15);
			}

			Font font = new Font("Serif", Font.PLAIN, 20);
			g2d.setFont(font);
			g2d.setColor(Color.WHITE);
			g2d.drawString("Next:", blockX + blockW/3, blockY - blockH + space);
			ns = gameBoard.getNextShape();
			nextShape = ns.getShape();
			for(i=0; i<nextShape[0].length; i++)
			{
				for(j=0; j<nextShape.length; j++)
				{
					drawSquare(nextShape[j][i], blockX + (blockW-ns.getWidth()*SIZE)/2 + j*SIZE, blockY - i*SIZE - (blockH-ns.getHeight()*SIZE)/2 , g2d);
				}
			}

			//Draw scores
			g2d.setColor(Color.WHITE);
			FontMetrics fm = g2d.getFontMetrics();
			g2d.drawString("Score:", blockX + blockW/3, blockY + space*3);
			g2d.drawString(""+gameBoard.getScore(), blockX + (blockW-fm.stringWidth(""+gameBoard.getScore()))/2 , blockY + space*5);


			//Draw level
			g2d.drawString("Level:", blockX + blockW/3, blockY + blockH + space*4);
			g2d.drawString(""+gameBoard.getLevel(), blockX + (blockW-fm.stringWidth(""+gameBoard.getLevel()))/2 , blockY + blockH+ space*6);
		}

		//Function to draw one square depends on color's shape
		public void drawSquare(int c, int x, int y, Graphics g)
		{
			Color squareColor;
			int[] arr = getColor(c);
			if(arr.length == 0)
				return;
			squareColor = new Color(arr[0], arr[1], arr[2]);
			g.setColor(squareColor.darker());
			g.fillRect(x, y - SIZE, SIZE, SIZE);
			g.setColor(squareColor);
			g.fillRect(x + 1, y - SIZE+1, SIZE-3, SIZE-3);
		}


		public void drawGameOver(Graphics2D g2d)
		{
			int widthR = 100, heightR = 30;
			g2d.setColor(new Color(176, 163, 255, 180));
			g2d.fillRoundRect(x + (width-widthR)/2, y - height + 20, widthR, heightR, 15, 15);
			Font font = new Font("Serif", Font.PLAIN, 18);
			g2d.setFont(font);
			g2d.setColor(new Color(0,0,0, 140));
			g2d.drawString("Game Over",x + (width-widthR)/2 +6, y - height + 40);

		}

		public void eraseEffect(Graphics2D g2d)
		{
			int[] lines, arr;
			Color squareColor;
			int k;
			lines = gameBoard.getErasedLines();
			for(k =0; k< 5; k++)
			{
				for(i=0;i<4;i++)
				{
					if(lines[i]!= -1)
					{
						for(j=0; j<widthNum; j++)
						{
							//board[i][lines[i]];
							arr = getColor(board[j][lines[i]]);
							squareColor = new Color(arr[0],arr[1],arr[2], 255 - i*50);
							g2d.setColor(squareColor.darker());
							g2d.fillRect(x, y - SIZE, SIZE, SIZE);
							g2d.setColor(squareColor);
							g2d.fillRect(x + 1, y - SIZE+1, SIZE-3, SIZE-3);
						}
					}
				}
			    try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}


		//Return color array
		public int[] getColor(int c)
		{
			switch (c) 
			{
			case 1:
				int[] n1 = {0,255,255}; //Light Blue
				return n1;
			case 2:
				int[] n2 = {255,99,71}; //Tomato
				return n2;
			case 3:
				int[] n3 = {255,215,0}; //Gold
				return n3;
			case 4:
				int[] n4 = {124,252,0}; //Green
				return n4;
			case 5:
				int[] n5 = {30,144,255}; //Blue
				return n5;
			case 6:
				int[] n6 = {186,85,211}; // Orchid
				return n6;
			case 7:
				int[] n7 = {255,160,122}; //Peach
				return n7;
			default:
				int[] n = {};
				return n;
			}
		}
	}
}