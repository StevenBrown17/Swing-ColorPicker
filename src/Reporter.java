
//package	common;


import	java.awt.*;
import	java.awt.event.*;
import	javax.swing.*;


/**
 * The reporter class provides static methods useful for reporting errors and tracing program execution.
 * Each public method returns "true" so that it can be used with assertions:
 * <p><code> assert Reporter.report("Instantiation error", e);</code>
 * <p><code> assert Reporter.enter(this);</code>
 * <p>This makes it possible to leave the code in place and activate it by enabling assertions:
 * <p><code>java -ea MyClass</code>
 */


public class Reporter
{
	private	static	Container	parent = null;
	private	static	JFrame		traceFrame = null;
	private	static	JTextArea	traceArea = null;



	// Disallow instances of this class

	private Reporter() {}


	/**
	 * Displays a caller-supplied error message in a dialog.
	 * @param message a brief, unique error description.
	 * @return This method always returns true.
	 */

	public static boolean report(String message)
	{
		JOptionPane.showMessageDialog(parent, message,
				"Error", JOptionPane.ERROR_MESSAGE);

		return true;
	}


	/**
	 * Displays a caller-supplied error message in a dialog.
	 * @param message a brief, unique error description.
	 * @return This method always returns true.
	 */

	public static boolean report(String message, String title)
	{
		JOptionPane.showMessageDialog(parent, message,
				title, JOptionPane.ERROR_MESSAGE);

		return true;
	}


	/**
	 * Displays a caller-supplied message in a dialog.
	 * @param message a brief, unique error description.
	 * @return This method always returns true.
	 */

	public static boolean message(String message)
	{
		JOptionPane.showMessageDialog(parent, message,
				"Notification", JOptionPane.INFORMATION_MESSAGE);

		return true;
	}


	/**
	 * Displays a caller-supplied message in a dialog.
	 * @param message a brief, unique error description.
	 * @return This method always returns true.
	 */

	public static boolean message(String message, String title)
	{
		JOptionPane.showMessageDialog(parent, message,
				title, JOptionPane.INFORMATION_MESSAGE);

		return true;
	}


	/**
	 * Reports an error from within a catch block.  The error report includes a
	 * caller supplied message, the specific exception, and a stack trace.
	 * @param message a brief, unique error description.
	 * @param e the exception that triggered the error report.
	 * @return This method always returns true.
	 */

	public static boolean report(String message, Throwable e)
	{
		Box	error = Box.createVerticalBox();

		error.add(makeMessage(message));
		error.add(makeException(e));
		error.add(makeTrace(e));

		JOptionPane.showMessageDialog(parent, error,
				"Exception Details", JOptionPane.ERROR_MESSAGE);

		return true;
	}



	/**
	 * Reports an error from within a catch block.  The error report includes a
	 * caller supplied mssage, the specific exception, and full location information.
	 * @param o the object that triggered the error report.
	 * @param message a brief, unique error description.
	 * @param t the throwable (exception) being reported.
	 * @return This method always returns true.
	 */

	public static boolean report(Object o, String message, Throwable t)
	{
		Box	error = Box.createVerticalBox();

		error.add(makeMessage(message));
		error.add(makeException(t));
		error.add(makeInfo(o, t.getStackTrace()));

		JOptionPane.showMessageDialog(parent, error,
				"Exception Details", JOptionPane.ERROR_MESSAGE);

		return true;
	}


	/**
	 * Sets the parent for error option pane error report.
	 * Option panes are centered on the parent if it is set; otherwise they
	 * centered in the screen.
	 * @param parent the parent on which the option panes are centered.
	 * @return This method always returns true.
	 */

	public static boolean setParent(Container parent)
	{
		Reporter.parent = parent;
		return true;
	}
	

	/**
	 * Create a window to display method/statment traces.
	 * @return This method always returns true.
	 */

	public static boolean createTrace()
	{
		traceFrame = new JFrame();
		traceArea = new JTextArea();

		traceFrame.add(new JScrollPane(traceArea));
		traceFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		traceFrame.setSize(600, 400);
		traceFrame.setTitle("Execution Trace");
		traceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		Dimension	screen = Toolkit.getDefaultToolkit().getScreenSize();
		traceFrame.setLocation(screen.width - 600, 0);

		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener()
				{ public void actionPerformed(ActionEvent event)
					{ traceArea.setText(""); }
				});
		JPanel	panel = new JPanel();
		panel.add(clear);
		traceFrame.add(panel, BorderLayout.SOUTH);

		traceFrame.setVisible(true);

		return true;
	}
	

	/**
	 * Adds a trace message to the trace output window.
	 * @param message the trace message that will be added to the trace window.
	 * @return This method always returns true.
	 */

	synchronized public static boolean trace(String message)
	{
		if (traceArea == null)
			createTrace();
			/*JOptionPane.showMessageDialog(parent,
					"You must call createTrace before using this method",
					"Trace Error", JOptionPane.ERROR_MESSAGE);*/
		traceArea.append(message + "\n");
		return true;
	}


	/**
	 * Adds an "Entering method" trace message to the trace output window.
	 * @param o The object in which the traced method is defined.
	 * @return This method always returns true.
	 */

	synchronized public static boolean enter(Object o)
	{
		trace("Entering " + getInfo(o, Thread.currentThread().getStackTrace()));

		return true;
	}


	/**
	 * Adds an "Exiting method" trace message to the trace output window.
	 * @param o The object in which the traced method is defined.
	 * @return This method always returns true.
	 */

	synchronized public static boolean exit(Object o)
	{
		trace("Exiting " + getInfo(o, Thread.currentThread().getStackTrace()));

		return true;
	}


	// makes a message panel to display the user message

	private static JPanel makeMessage(String message)
	{
		JPanel	messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		messagePanel.add(new JLabel(message));
		messagePanel.setBorder(BorderFactory.createTitledBorder("Message"));

		return messagePanel;
	}


	// make an exception panel to display the exception string

	private static JPanel makeException(Throwable e)
	{
		JPanel	exceptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		exceptionPanel.add(new JLabel(e.toString()));
		exceptionPanel.setBorder(BorderFactory.createTitledBorder("Exception"));

		return exceptionPanel;
	}


	// make an execption panel to display the file, method, and line number

	private static JPanel makeInfo(Object o, StackTraceElement[] elements)
	{
		JPanel	infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		infoPanel.setBorder(BorderFactory.createTitledBorder("Location"));

		infoPanel.add(new JLabel(getInfo(o, elements)));

		return infoPanel;
	}


	private static String getInfo(Object o, StackTraceElement[] elements)
	{
		for (StackTraceElement ste : elements)
		{
			if (o.getClass().getName() == ste.getClassName())
				return "File: " + ste.getFileName() +
					",  Method: " + ste.getMethodName() +
					",  Line " + ste.getLineNumber();
		}
		return "NOT FOUND";
	}


	// make a panel to display the stack trace

	private static JPanel makeTrace(Throwable e)
	{
		JPanel		tracePanel = new JPanel();
		JTextArea	traceArea = new JTextArea(10, 30);
		JScrollPane	scroller = new JScrollPane(traceArea);

		tracePanel.add(scroller);
		tracePanel.setBorder(BorderFactory.createTitledBorder("Stack Trace"));
		traceArea.setEditable(false);
		for (StackTraceElement ste : e.getStackTrace())
			traceArea.append(ste.toString() + "\n");

		return tracePanel;
	}
}

