package src;

import src.presentation.controllers.PresentationCtrl;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		javax.swing.SwingUtilities.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						PresentationCtrl ctrl = new PresentationCtrl();
						ctrl.initializePresentationCtrl();
					}
				}
		);
	}
}
