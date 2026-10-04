package io.github.arthursilvagbs.Locacao.de.Carros.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

   @Bean
   public OpenAPI locadoraOpenApi() {
      return new OpenAPI()
         .info(new Info()
            .title("Locadora API")
            .version("1.0.0")
            .description("API de gestão de locação de veículos. Para testar rotas protegidas, faça login em /auth/login e informe o JWT em Authorize."))
         .schemaRequirement("bearerAuth", new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Token retornado pelo login, sem o prefixo Bearer."));
   }
}
