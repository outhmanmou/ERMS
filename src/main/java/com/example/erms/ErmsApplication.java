package com.example.erms;

import javax.swing.SwingUtilities;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.erms.ui.MainFrame;

@SpringBootApplication
public class ErmsApplication {

	public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ErmsApplication.class);

//		SpringApplication.run(ErmsApplication.class, args);	
		 builder.headless(false);
	        ConfigurableApplicationContext context = builder.run(args);

	        // Start the Swing UI in the Event Dispatch Thread
	        SwingUtilities.invokeLater(() -> {
	            MainFrame mainFrame = context.getBean(MainFrame.class);
	            mainFrame.setVisible(true);
	        });
	}

}
