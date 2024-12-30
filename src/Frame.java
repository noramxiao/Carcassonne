import javax.swing.JFrame;

public class Frame extends JFrame
{
	private static final int WIDTH = 1100;
	private static final int HEIGHT = 825;
	
	public Frame(String name)
	{
		super(name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		add(new Panel());
		setVisible(true);
	}
}
