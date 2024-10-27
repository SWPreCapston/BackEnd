package com.precapston.precapston;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precapston.precapston.dto.ImageDTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@OpenAPIDefinition(
		servers = {
				@Server(url = "https://localhost:8080", description = "Default Server url")
		}
)

@SpringBootApplication
public class PrecapstonApplication {

	public static void main(String[] args) {

		SpringApplication.run(PrecapstonApplication.class, args);



	}
}

