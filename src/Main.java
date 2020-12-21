package src;

import src.presentation.controllers.PresentationCtrl;

public class Main {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(
				() -> {
					PresentationCtrl ctrl = new PresentationCtrl();
					ctrl.initializePresentationCtrl();
				}
		);
	}
}
