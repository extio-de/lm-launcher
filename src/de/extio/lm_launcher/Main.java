package de.extio.lm_launcher;

import java.awt.EventQueue;

public class Main {
	
	public static void main(final String[] args) {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					final LaunchFrame frame = new LaunchFrame();
					frame.setVisible(true);
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
