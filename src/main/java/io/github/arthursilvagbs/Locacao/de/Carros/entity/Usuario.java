package io.github.arthursilvagbs.Locacao.de.Carros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@NoArgsConstructor
@Getter
public class Usuario {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   private UUID usuarioId;

   @Setter
   @Column(name = "email", nullable = false, unique = true)
   private String email;

   @Setter
   @Column(name = "senha_hash", nullable = false)
   private String senha;

   @Setter
   @Enumerated(value = EnumType.STRING)
   private Role roles;

   @CreationTimestamp
   private LocalDateTime createdAt;

   @UpdateTimestamp
   private LocalDateTime updatedAt;

   public Usuario(String email, String senha, Role roles) {
      this.email = email;
      this.senha = senha;
      this.roles = roles;
   }
}
