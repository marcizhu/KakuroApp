package src;

import src.presentation.controllers.PresentationCtrl;
import src.PopulateDB;

public class Main {
	public static void main(String[] args) {
		try {
			// Uncomment line below to populate Database! (Warning: it will delete everything in it before populating it)
			// PopulateDB.populateDB();
		} catch (Exception e) {
			System.out.println("Whatever...");
		}

		javax.swing.SwingUtilities.invokeLater(
				() -> {
					PresentationCtrl ctrl = new PresentationCtrl();
					ctrl.initializePresentationCtrl();
				}
		);
	}
}
