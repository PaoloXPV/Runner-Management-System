package com.ElVacio.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class RunnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RunnerApplication.class, args);
	}
    @Bean
    public CommandLineRunner pruebaDeConexion(DataSource dataSource) {
        return args -> {
            try {
                // Test database connection
                Connection conexion = dataSource.getConnection();

                System.out.println("----------------------------------------");
                System.out.println("Database connection successful");
                System.out.println("Database: " + conexion.getMetaData().getDatabaseProductName());
                System.out.println("----------------------------------------");

            } catch (Exception e) {
                System.out.println("----------------------------------------");
                System.out.println("Database connection failed");
                System.out.println("Cause: " + e.getMessage());
                System.out.println("----------------------------------------");
            }
        };
    }
}
