/**
 * 
 */
package di.inout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;

/**
 * @author Georg Dick
 * 
 */
public class ExportzielWaehlenDialog extends JDialog {
	private final JFileChooser fc = new JFileChooser();
	private final JPanel contentPanel = new JPanel();
	private String basisDirectory = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ExportzielWaehlenDialog dialog = new ExportzielWaehlenDialog(null,
					100, 100);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getExportielDurchDialog(String alteBasis, int x, int y) {
		ExportzielWaehlenDialog ewd = new ExportzielWaehlenDialog(alteBasis, x,
				y);
		ewd.setVisible(true);
		String bd = ewd.getBasisDirectory();
		ewd.dispose();
		return bd;
	}

	public String getBasisDirectory() {
		// TODO Auto-generated method stub
		return basisDirectory;
	}

	/**
	 * Create the dialog.
	 */
	public ExportzielWaehlenDialog(String alteBasis, int x, int y) {
		setResizable(false);
		setModal(true);
		setBounds(x, y, 290, 109);
		basisDirectory = alteBasis;
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
//		Localisation.setUebersetzung("Legen Sie den Ordner für den Java-Export fest", "Please choose directory for Java-Export", "englisch");
		JLabel lblLegenSieDen = new JLabel("Legen Sie den Ordner für den Java-Export fest");
		lblLegenSieDen.setBounds(12, 13, 287, 16);
		contentPanel.add(lblLegenSieDen);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				basisDirectory = findeOrdner(basisDirectory);
				setVisible(false);
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
//		Localisation.setUebersetzung("Abbruch", "Cancel", "englisch");
		JButton cancelButton = new JButton("Abbruch");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				basisDirectory = null;
				setVisible(false);
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

	}

	// Ein- Ausgabe
	public String findeOrdner(String alteBasis) {
		FileOutputStream file;

		// fc.setFileFilter(new FileFilter() {
		// public boolean accept(File f) {
		// return f.isDirectory();
		// }
		//
		// public String getDescription() {
		// return "Verzeichnis für Quelltextbasis";
		// }
		// });
		if (alteBasis == null || alteBasis.equals("")) {
			alteBasis = ".";
		}
		fc.setCurrentDirectory(new java.io.File(alteBasis));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogType(JFileChooser.CUSTOM_DIALOG);
//		Localisation.setUebersetzung("Quelltextbasisordner wählen", "Choose directory", "englisch");
		int returnVal = fc.showDialog(this, "Quelltextbasisordner wählen");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// System.out.println(fc.getCurrentDirectory());
			// System.out.println(fc.getSelectedFile().getName().toLowerCase());
			// System.out.println(fc.getSelectedFile().getAbsolutePath());

			return fc.getSelectedFile().getAbsolutePath();
		}
		return null;
	}
}
