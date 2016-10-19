import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.logging.*;



/**
 * ColorPicker shows the RGB (red, green, and blue) values associated with any of the 16,777,216 colors
 * that a user may pick by sliding any of three scroll bars or by entering a value 0..255 in the three
 * input boxes.
 *
 * The user may start the program in one of three ways:
 * <ol type="1">
 * <li>java ColorPicker
 * <li>java ColorPicker -x
 * <li>java ColorPicker hex
 * </ol>
 * 1 begins in decimal mode; 2 and 3 begin in hexadecimal mode.
 *
 * <p>In decimal mode, three values (in the range 0..255) are displayed and represent the RGB values.
 *
 * In hexadecimal mode, one value (in the range 0..FFFFFF) is displayed; it represents the RGB values as RRGGBB.
 */


@SuppressWarnings("serial")
public class ColorPicker extends JFrame
{
	
	private Logger logger = Logger.getLogger("colorpicker.CS3230");
	
	//------------------------------------------------------- Output to display numeric values
	private	JTextField	redOutput = new JTextField("0", 10);
	private	JTextField	greenOutput = new JTextField("0", 10);
	private	JTextField	blueOutput = new JTextField("0", 10);

	//-------------------------------------------------------- Color adjustment scroll bars
	private	JScrollBar	redScroll = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 255);
	private	JScrollBar	greenScroll = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 255);
	private	JScrollBar	blueScroll = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 255);

	//-------------------------------------------------------- Color preview area
	private	JPanel		colorPreview = new JPanel();
	private	TitledBorder	border = BorderFactory.createTitledBorder("Decimal");

	//-------------------------------------------------------- Control panels
	private	JPanel		colorControl = new JPanel(new GridLayout(3, 2));

	//-------------------------------------------------------- Numeric display and run mode: Decimal or Hexadecimal
	private	int		mode;

	//-------------------------------------------------------- Contol buttons
	private	JButton		toggleMode = new JButton("Hex");
	private	JButton		exit = new JButton("Exit");

	//-------------------------------------------------------- Symbolic constants used to denote display and run mode
	public	static	final	int	DEC = 0;	// denotes decimal mode for testing and passing to methods
	public	static	final	int	HEX = 1;	// denotes hexadecimal mode for testing and passing to methods





	/**
	 * Initializes a new ColorPicker object.
	 * Sets up the title, size, event handling, and layout.
	 * @param mode the initial run mode: decimal or hexadecimal.
	 */

	public ColorPicker(int mode)
	{
		
		assert Reporter.createTrace();
		this.mode = mode;
		
		logger.setLevel(Level.FINE);
		Handler handler;
		try{
			handler = new FileHandler("C:\\Users\\a6c81zz\\Desktop\\logs\\Errorlog.log");
			handler.setLevel(Level.FINE);
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
		}catch(java.io.IOException e){
			System.err.println(e);
			logger.severe("Unable to open log file");
		}
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.OFF);//this switches of logging

		setTitle("Color Picker Application");
		setSize(300, 200);

		// Window Listener -- close program with "X" button on top right of frame
		addWindowListener ( new WindowAdapter()		// anonymous inner class
			{	public void windowClosing(WindowEvent e)
				{ System.exit(0); }
			} );


		// make the color control panel (next method below)
		makeColorControl();


		// initialize and place the color preview panel
		// (this is where the colors are previewed)
		colorPreview.setBackground(Color.black);		// initial color is 0, 0, 0
		getContentPane().add(colorPreview, BorderLayout.CENTER);


		// initialize buttons
		JPanel	controls = new JPanel();
		controls.add(toggleMode);
		controls.add(exit);
		getContentPane().add(controls, BorderLayout.NORTH);
		exit.addActionListener ( new ActionListener()		// anonymous inner class
			{	public void actionPerformed(ActionEvent e)
				{ System.exit(0); }
			} );
		toggleMode.addActionListener ( new ActionListener()		// anonymous inner class
			{	public void actionPerformed(ActionEvent event)
				{ toggleButton(); }
			} );


		// Listener object that responds the "Enter" key in any of the three text fields
		ActionListener	listener = new ActionListener()		// anonymous inner class
			{	public void actionPerformed(ActionEvent event)
				{
					readText(event);
				}
			};

		// set the text fields as event sources
		redOutput.addActionListener(listener);
		greenOutput.addActionListener(listener);
		blueOutput.addActionListener(listener);

		setVisible(true);
	}







	/**
	 * Makes the color selection control panel.
	 * <ol type=1">
	 * <li>Default mode is decimal; a few things are changed if ColorPicker is started in hexadecimal mode.
	 * <li>Builds a panel with 3 x 2 grid.  Output is in the left column; input scroll bars are in the right column.
	 * <li>A labeled border is drawn around the controls to indicate the current mode.
	 * <li>Sets up the event handling for the scroll bars.
	 * <li>The panel is placed at the bottom of a frame.
	 * </ol>
	 */

	public void makeColorControl()			// fill and place color control panel; setup event handling
	{
		// put the border around the color control panel
		colorControl.setBorder(border);


		// things to change if the program starts in hex mode
		if (mode == HEX)
		{
			redOutput.setText("");		// red and blue are not used,
			blueOutput.setText("");		// use green for the hex output
			border.setTitle("Hexadecimal");	// change border title for hex mode
			toggleMode.setText("DEC");	// FUTURE EXPANSION-- set the text on the button to "DEC"
		}


		// initialize scroll bars
		redScroll.setBlockIncrement(16);
		greenScroll.setBlockIncrement(16);
		blueScroll.setBlockIncrement(16);
		redScroll.setBackground(Color.red);
		greenScroll.setBackground(Color.green);
		blueScroll.setBackground(Color.blue);


		// add scroll bars and output fields to color control panel
		colorControl.add(redOutput);
		colorControl.add(redScroll);
		colorControl.add(greenOutput);
		colorControl.add(greenScroll);
		colorControl.add(blueOutput);
		colorControl.add(blueScroll);


		// put color control panel in frame at bottom
		getContentPane().add(colorControl, BorderLayout.SOUTH);


		// create an object to listen for scroll bar movement
		AdjustmentListener	scrollListener = new AdjustmentListener()	// anonymous inner class
			{	public void adjustmentValueChanged(AdjustmentEvent event)
				{
					adjustColor();
				}
			};

		// make the scroll bars event sources
		redScroll.addAdjustmentListener(scrollListener);
		blueScroll.addAdjustmentListener(scrollListener);
		greenScroll.addAdjustmentListener(scrollListener);
	}







	/**
	 * Adjust the color in the color preview panel.
	 * Handles the scroll bar events, read values from the scroll bars, and changes
	 * the color of the color preview panel.
	 */

	public void adjustColor()
	{
		if (mode == DEC)
		{
			// get RGB color values from all 3 scroll bars and set numeric decimal values in output
			redOutput.setText(redScroll.getValue() + "");
			greenOutput.setText(greenScroll.getValue() + "");
			blueOutput.setText(blueScroll.getValue() + "");

			// change color in color preview area
			colorPreview.setBackground(new Color(redScroll.getValue(),
				greenScroll.getValue(), blueScroll.getValue()));
		}
		else	// mode == HEX
		{
			// get RGB color values from all 3 scroll bars but set the numeric hexadecimal value in green output
			int hexColor = (redScroll.getValue() << 16) +
				(greenScroll.getValue() << 8) + blueScroll.getValue();
			greenOutput.setText(Integer.toHexString(hexColor).toUpperCase());

			// change color in color preview area
			colorPreview.setBackground(new Color(hexColor));
		}
	}






	/**
	 * Toggles (i.e., switches) between decimal and hexadecimal modes.
	 */

	private void toggleButton()
	{
		String	label = toggleMode.getText();

		if (label.equals("Hex"))		// in DEC mode, going to HEX
		{
			toggleMode.setText("Dec");
			mode = HEX;
			redOutput.setText("");
			redOutput.setEditable(false);
			blueOutput.setText("");
			blueOutput.setEditable(false);
			border.setTitle("Hexadecimal");
		}
		else					// in HEX mode, going to DEC
		{
			toggleMode.setText("Hex");
			mode = DEC;
			redOutput.setEditable(true);
			blueOutput.setEditable(true);
			border.setTitle("Decimal");
		}
		adjustColor();
		repaint();
	}

	@SuppressWarnings("unused")
	private void error(JTextField inputField, String colorString){
		inputField.setBackground(Color.RED);
		inputField.setText("");
		JOptionPane.showMessageDialog(this, colorString + " is not a valid color", "Input Error", JOptionPane.ERROR_MESSAGE);
		
	}





	/**
	 * Processes data entered into a text field (i.e., is the text field event handler).
	 * <ol>
	 * <li>Gets the source (i.e., textfield) of the event.
	 * <li>Gets the text from the text field.
	 * <li>Converts the text into an integer based on the radix (10 or 16).
	 * <li>Makes sure the value is within range: 0.. 255 or 0..FFFFFF.
	 * <li>Sets the value in the appropriate scroll bar (i.e., positions the scroll bar handle).
	 * </ol>
	 *
	 * @param event a JTextField event.
	 */

	private void readText(ActionEvent event)
	{
		
		assert Reporter.enter(this);
		
		logger.entering("ColorPicker", "readText");
		
		JTextField	colorField	= (JTextField)event.getSource();
		String		colorString	= colorField.getText();
		
		colorField.setBackground(Color.WHITE);
		
//		if(mode == DEC && !colorString.matches("\\p{Digit}{1,3}") || mode == HEX && !colorString.matches("\\p{XDigit}{1,6}")){
//			error(colorField, colorString);
//			return;
//		}

		int base = (mode == DEC) ? 10 : 16;		// radix is either 10 or 16
		//int colorValue = Integer.parseInt(colorString, base);
		int colorValue;
		
		try{
			colorValue = Integer.parseInt(colorString, base);
		}catch(NumberFormatException e){
			
			logger.fine("Error converting" + colorString + "to an integer");
			
			//Reporter.report(this,"Error converting" + colorString + "to an integer", e);
			
			//error(colorField, colorString);
			return;
		
		}
		

		if (mode == DEC)
		{	
			if (colorValue < 0)
				colorValue = 0;
			else if (colorValue > 255)
				colorValue = 255;
			colorField.setText(colorValue + "");	// in case the value was out of bounds

			if (colorField == redOutput)
				redScroll.setValue(colorValue);
			else if (colorField == greenOutput)
				greenScroll.setValue(colorValue);
			else
				blueScroll.setValue(colorValue);
		}
		else
		{	
			if (colorValue < 0)
				colorValue = 0;
			else if (colorValue > 0xFFFFFF)
				colorValue = 0xFFFFFF;

			redScroll.setValue(colorValue >> 16);
			greenScroll.setValue((colorValue >> 8) & 0xFF);
			blueScroll.setValue(colorValue & 0xFF);
		}
		
		assert Reporter.exit(this);
		logger.exiting("ColorPicker", "readText");
		
	}






	/**
	 * Starts the program running.
	 * The correct run mode is selected through the constructor argument.
	 */

	public static void main(String[] args)
	{
		if (args.length == 1 && (args[0].equals("-x") || args[0].equals("hex")))
			new ColorPicker(HEX);
		else
			new ColorPicker(DEC);
	}
}

