package com.sessionizing;

import com.sessionizing.dataLoader.DataLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SessionizingApplication {
	static public DataLoader dataLoader = new DataLoader();
	public static void main(String[] args) {
		SpringApplication.run(SessionizingApplication.class, args);
		loadData();
	}

	public static void loadData(){
		dataLoader.loadCSVData();
	}

}
