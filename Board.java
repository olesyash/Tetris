import java.util.Random;


public class Board 
{
	// -------------    Variables definition   -------------------
	private int cols, rows, oldX, oldY, cs, ns, x, y, score, linesCounter, level, strike;
	private int[][] board, s;
	private Random randomGenerator; //To create random shape
	private Shape currentShape;
	private boolean stop = false, first = true, gameOver, wasErase, levelUp;
	private int[] erasedLines = {-1,-1,-1,-1};

	public Board(int cols, int rows)
	{
		this.cols = cols;
		this.rows = rows + 4;
		this.randomGenerator = new Random(); //To create random shape
		this.board = new int[this.cols][this.rows]; // board representing game board
		this.level = 1; // Start level
		this.gameOver = false; 
		this.wasErase = false;
		this.levelUp = false;
	}

	//Function to create new random shape 
	public void createNewShape()
	{
		stop = false;
		if(first)
		{
			cs = randomGenerator.nextInt(7) + 1;
			ns = randomGenerator.nextInt(7) + 1;
			currentShape = new Shape(cs);
			first = false;
		}
		else
		{
			cs = ns;
			ns = randomGenerator.nextInt(7) + 1;
			currentShape = new Shape(cs);
		}		
		x = (cols-currentShape.getWidth())/2;
		y = rows-1-currentShape.getHeight();

		if(!checkPossibleMove(x, y))
		{
			gameOver = true;
		}
		oldX = x;
		oldY = y;
	}

	//Each move board need to be updated 
	public void update()
	{
		int i;
		wasErase = false;

		if(!gameOver)
		{
			if(!stop) //If shape should move
			{
				copyShape(oldX, oldY, false); //Erase old shape
				if(checkPossibleMove(this.x,this.y)) //If no collision with other shape
				{
					copyShape(this.x,this.y, true); //Draw it on new x,y
				}
				else	// There is collision - stop the shape from moving and draw on old place
				{
					if(Math.abs(oldX-x) == 0 && (oldY-y) >= 1) // if collision is on bottom stop moving the shape, if by sides continue moving
						stop = true; 

					x = oldX; //if the collision is from sides - do not change x
					copyShape(oldX, oldY, true);
				}
				//Keeping last step: x and y
				oldX=x;
				oldY=y;
			}
			else //If shape arrived to last destination - create new shape and check if need to erase line
			{
				for(i =0;i<4 ;i++)
					erasedLines[i] = -1;
				i = 0;
				linesCounter = -1;
				findFullLines(); // Check if there are full lines

				for(i =0;i<4 ;i++)
				{
					if(erasedLines[i] != -1)
					{
						linesCounter ++;
						wasErase = true;
					}

				}
				if(linesCounter == -1)
					strike = -1;
				else
					strike++;
				updateScore(linesCounter);
				score ++;
				updateLevel();
				createNewShape();		
			}
		}
	}

	//Function that checking if the move is possible - or creates some collision
	private boolean checkPossibleMove(int curX, int curY)
	{
		boolean possibleMove = true, a, b;
		int i,j, newX, newY;

		s = currentShape.getShape();

		if(curX >=0 && (curX + currentShape.getWidth() - 1) < cols && curY-currentShape.getHeight()+1 >= 0)
		{
			for(i=0; i< currentShape.getHeight(); i++)
			{
				for(j = 0; j < currentShape.getWidth(); j++)
				{
					newX = curX+j;
					newY = curY+i-currentShape.getHeight() +1;
					a = (board[newX][newY] != 0);
					b = (s[j][i] != 0);	
					possibleMove = !(a&b);
					if(!possibleMove)
					{
						return possibleMove;
					}			
				}

			}
		}
		return possibleMove;
	}

	//This function allows to copy shape to new location - that given as parameters [x,y]
	private boolean copyShape(int curX, int curY, boolean color)
	{
		int i, j, newX, newY;
		boolean possibleMove = true;
		boolean a, b;

		s = currentShape.getShape();

		if(curX >=0 && (curX + currentShape.getWidth() - 1) < cols && curY-currentShape.getHeight()+1 >= 0)
		{

			for(j = 0; j < currentShape.getWidth(); j++)
			{
				for(i=0; i< currentShape.getHeight(); i++)
				{
					newX = curX+j;
					newY = curY+i-currentShape.getHeight() +1;
					a = (board[newX][newY] != 0);
					b = (s[j][i] != 0);	
					possibleMove = !(a&b);
					if(color)
					{
						if(s[j][i] !=0)
							board[newX][newY] = s[j][i];
					}
					else 
					{
						if(s[j][i]!=0)
							board[newX][newY] = 0;
					}
				}

			}

		}
		if(curY == currentShape.getHeight()-1)
		{
			stop = true;
		}
		return possibleMove;
	}

	//Function responsible for shapes rotation - after check boundaries 
	public void rotate()
	{
		if(!stop) //If shape is moving
		{
			Shape old = new Shape(currentShape);

			copyShape(x, y, false); //Erase old shape
			currentShape.rotate(cs);

			if(!checkPossibleMove(this.x, this.y)) //Check collision with other shapes
			{
				currentShape = old;
				copyShape(this.x, this.y, true); //draw old shape
			}
			else if(this.y - currentShape.getHeight() == 0) //Shape arrived to the bottom
			{
				currentShape = old;
				copyShape(this.x, this.y, true);
			}
			else //Possible to rotate bit there is walls on the way
			{
				while(this.x + currentShape.getWidth() > cols) //If there is collision with wall
				{
					this.x--;
				}		
				while(this.y-currentShape.getHeight() +1  < 0) //If there collison with floor 
					this.y ++;
			}
		}

	}

	//Function locate all filled lines that should be erased and save them in array erasedLines[4] (max lines can be erased each time is 4)
	private void findFullLines()
	{
		boolean full = true;
		int i, j;
		int k=0;

		for(j=0; j<rows;j++)
		{
			full = true;
			for(i=0; i<cols; i++)
			{
				if(board[i][j] == 0)
					full = false;
			}
			if(full == true)
			{
				erasedLines[k] = j;
				k++;
			}
		}
	}

	//This function allows to erase specific line from the board and reorganize the board
	public void eraseLine(int lineNum)
	{
		int i, j;

		for(i=0; i<cols; i++)
		{
			board[i][lineNum] = 0;	
		}

		for(i = 0; i<cols; i++)
		{
			for(j = lineNum; j+1<rows;j++)
			{
				board[i][j] = board[i][j+1];
			}
		}	
		for(i=0; i<cols; i++)
		{
			board[i][rows-1] = 0;	
		}
	}

	//Update scores if lines was erased, depends on strikes too
	private void updateScore(int linesCounter)
	{
		if(linesCounter != -1)
			score += (Math.pow(2,linesCounter)*100)+level*strike*50; 
	}

	//Update level depends on scores
	private void updateLevel()
	{
		if(score > Math.pow(2, (level-1))*1000)
		{
			level ++;
			levelUp = true;
		}
	}

	//---------------- Shape moves -------------------
	public void moveDown()
	{
		if(!stop)
			y--;
	}

	public void moveLeft()
	{
		if (x > 0)
			x--;
	}
	public void moveRight()
	{
		if(x < cols - currentShape.getWidth())
			x++;
	}

	//----------Getters----------
	public int[][] getBoard()
	{
		return board;
	}

	public Shape getNextShape()
	{
		return new Shape(ns);
	}

	public int getScore()
	{
		return score;
	}

	public int getLevel()
	{
		return level;
	}

	public boolean getGameOver()
	{
		return gameOver;
	}

	public int[] getErasedLines()
	{
		return erasedLines;
	}
	public boolean getWasErase()
	{
		return wasErase;
	}
	public boolean getStop()
	{
		return stop;
	}
	public boolean getLevelUp()
	{
		return levelUp;
	}
	public void setLevelUp(boolean t)
	{
		levelUp = t;
	}
}
