package hn.emeka.prestamo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Microservicio Prestamo",
        version = "1.0.0",
        description = "API para gestionar préstamos",
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        contact = @Contact(
            name = "Soporte de Microservicio",
            email = "soporte@emeka.hn",
            url = "http://emeka.hn"
        )
    ),
    servers = {
        @Server(description = "Ambiente Local", url = "http://localhost:8080/"),
        @Server(description = "Ambiente Dev expuesto por Apigateway", url = "http://dev.api.emeka.hn"),
        @Server(description = "Ambiente QA expuesto por Apigateway", url = "http://qa.api.emeka.hn"),
        @Server(description = "Ambiente Prod expuesto por Apigateway", url = "http://api.emeka.hn")
    }
)
@SpringBootApplication
public class PrestamoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrestamoApplication.class, args);
    }
}
