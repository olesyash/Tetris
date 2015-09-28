import javax.swing.JFrame;

public class Tester
{
	 public static void main(String[] args)
	    {
	        JFrame frame = new JFrame("Tetris");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(500, 610);
	        TetrisPanel tp = new TetrisPanel();
	        frame.add(tp);
	       	frame.setVisible(true);
	    }
}