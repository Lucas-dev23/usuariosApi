package br.com.cotiinformatica.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_usuario")
@Data
public class Usuario {

	@Id
	private UUID id;	
	
	@Column(length = 150, nullable = false)
	private String nome;
	
	@Column(length = 50, nullable = false, unique = true)
	private String email;
	
	@Column(length = 100, nullable = false)
	private String senha;
}
