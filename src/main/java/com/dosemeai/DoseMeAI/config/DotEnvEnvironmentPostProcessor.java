package com.dosemeai.DoseMeAI.config;

import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        File envFile = new File(".env");
        if (envFile.exists()) {
            Properties props = new Properties();
            try (FileInputStream inputStream = new FileInputStream(envFile)) {
                props.load(inputStream);
                environment.getPropertySources().addFirst(new PropertiesPropertySource("dotenv", props));
                System.out.println("Arquivo .env carregado com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao carregar .env: " + e.getMessage());
            }
        } else {
            System.out.println("Arquivo .env não encontrado");
        }
    }
}
