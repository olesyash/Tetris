/*----------------------SHAPES description: ------------------------
1-blue   2-red   3-yellow   4-green   5-blue     6-purple   7-pink
[][]      []      [][]     []        [][][][]   [][]	     []
[]      [][]        []     [][]                 [][]	   [][][]	
[]      []          []       []

 */


public class Shape 
{
	private static final int seven = 7, six = 6, five = 5, four = 4, three = 3, two = 2, one = 1;
	private int[][] shape;
	private int height, width, counter;

	public Shape(int n)
	{
		this.counter = 0; //rotation counter
		defineShape(n); // create shape
	}

	public Shape(Shape p)
	{
		this.counter = p.counter; //rotation counter
		this.height = p.height;
		this.width = p.width;
		this.shape = new int[p.width][p.height];
		for(int i=0; i<p.width; i++)
			for(int j=0; j<p.height; j++)
				this.shape[i][j] = p.getShape()[i][j];

	}
	//Shape definition described by matrix (2D array) with the number that representing the color
	private void defineShape(int n)
	{
		switch (n) 
		{
		case 1:
			height = three;
			width = two;
			shape = new int[two][three];
			shape[0][0] = one;
			shape[0][1] = one;
			shape[0][2] = one;
			shape[1][2] = one;
			break;
		case 2:
			shape = new int[two][three];
			height = three;
			width = two;
			shape[0][0] = two;
			shape[0][1] = two;
			shape[1][1] = two;
			shape[1][2] = two;
			break;
		case 3:
			shape = new int[two][three];
			height = three;
			width = two;
			shape[0][2] = three;
			shape[1][0] = three;
			shape[1][1] = three;
			shape[1][2] = three;
			break;
		case 4:
			shape = new int[two][three];
			height = three;
			width = two;
			shape[0][1] = four;
			shape[0][2] = four;
			shape[1][0] = four;
			shape[1][1] = four;
			break;
		case 5:
			shape = new int[four][one];
			height = one;
			width = four;
			shape[0][0] = five;
			shape[1][0] = five;
			shape[2][0] = five;
			shape[3][0] = five;
			break;
		case 6:
			shape = new int[two][two];
			height = two;
			width = two;
			shape[0][0] = six;
			shape[0][1] = six;
			shape[1][0] = six;
			shape[1][1] = six;
			break;
		case 7:
			shape = new int[three][two];
			height = two;
			width = three;
			shape[0][0] = seven;
			shape[1][0] = seven;
			shape[2][0] = seven;
			shape[1][1] = seven;
			break;

		default:
			shape = new int[four][four];
			height = four;
			width = four;
			break;
		}
	}
	//Function to rotate shape using array rotation
	private void swap()
	{
		int i, j, temp;
		int[][] shape1 = new int[height][width];	//Create new array with opposite height and width
		for(i=0;i<width;i++)                       //Rotate all array
			for(j=0;j<height;j++)
				shape1[j][i] = shape[i][j]; 

		shape = shape1;              //Update shape
		temp = width;
		this.width = height;; 
		this.height = temp;

		for(j=0;j<height-1;j++)    //mirroring array(to set it right direction) 
		{		
			for(i=0;i<width;i++)
			{
				temp = shape[i][j];		
				shape[i][j] = shape[i][height-j-1];
				shape[i][height-j-1] = temp;
			}
		}
	}

	//Function to rotate shape depends on shape type
	public void rotate(int n)
	{
		if(n == 2 | n == 4 | n == 5) // two sides shapes
		{
			if(counter == 0)
			{
				swap();
				this.counter++;
			}
			else 
			{
				defineShape(n);
				this.counter = 0;
			}
		}
		else if(n == 6); //one side shape - Do nothing
		else //4-sides shapes
		{
			if(counter < 3)
			{
				swap();
				this.counter++;
			}
			else 
			{
				defineShape(n);
				this.counter = 0;
			}
		}
	}

	//--------- Getters and Setters -----------------
	public int[][] getShape() 
	{
		return shape;
	}

	public int getHeight() 
	{
		return height;
	}

	public int getWidth() 
	{
		return width;
	}
	public void setCounter(int c)
	{
		counter = c;
	}
	public int getCounter()
	{
		return counter;
	}

}
