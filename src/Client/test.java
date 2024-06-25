package Client;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class test extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int historyDisplayedCounter = 0;
	private final int MAX_HISTORY_DISPLAYED = 128;
	private JPanel historyGridPanel, historyHolderPanel;
	private JScrollPane scroller;
	/**
	 * Create the panel.
	 */
	public test() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0};
		gridBagLayout.columnWeights = new double[]{Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		historyHolderPanel = new JPanel(new GridBagLayout());
		historyHolderPanel.setPreferredSize(new Dimension(0,0));
		historyGridPanel = new JPanel(new GridBagLayout());
		scroller = new JScrollPane(historyGridPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(null);
		historyHolderPanel.add(scroller, putConstraints(0, 0, 1.0, 1.0));
	}
	private GridBagConstraints putConstraints(int gridx, int gridy, double weightx, double weighty) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		return constraints;
	}
	public void addHistoryLine() {
		if(historyDisplayedCounter >= MAX_HISTORY_DISPLAYED) {
//			clearHistoryFields();
		}
		historyDisplayedCounter++;
		int gridypos = MAX_HISTORY_DISPLAYED - historyDisplayedCounter;
		JPanel historyLine = new JPanel(new GridBagLayout());
		historyLine.setPreferredSize(new Dimension(0, (int)(historyHolderPanel.getSize().height * 0.5)));
		
//		fillHistoryLine(historyLine);

		historyGridPanel.add(historyLine, putConstraints(0, gridypos, 1.0, 0.0));
		historyGridPanel.revalidate();
		historyGridPanel.repaint();
		scroller.getVerticalScrollBar().setValue(0); // scroll back to top
	}
	private void fillHistoryLine(JPanel historyLine) {
		
	}
}
