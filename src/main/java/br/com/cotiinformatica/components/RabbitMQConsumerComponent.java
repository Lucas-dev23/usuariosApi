package br.com.cotiinformatica.components;

import java.util.Date;
import java.util.UUID;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.cotiinformatica.collections.LogMensageria;
import br.com.cotiinformatica.dtos.MensagemUsuarioDto;
import br.com.cotiinformatica.repositories.LogMensageriaRepository;

@Component
public class RabbitMQConsumerComponent {

	@Autowired
	private MailSenderComponent mailSenderComponent;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private LogMensageriaRepository logMensageriaRepository;

	/*
	 * Método para ler e processar a fila 'mensagens' do RabbitMQ
	 */
	//@RabbitListener(queues = { "mensagens" }) // nome da fila
	public void proccessMessage(@Payload String message) throws Exception {

		// ler cada mensagem contida na fila e deserializar o seu conteúdo
		// DESERIALIZAR => transformar o conteudo de JSON para Objeto Java
		MensagemUsuarioDto dto = objectMapper.readValue(message, MensagemUsuarioDto.class);

		LogMensageria logMensageria = new LogMensageria();

		logMensageria.setId(UUID.randomUUID());
		logMensageria.setEmailUsuario(dto.getEmailUsuario());
		logMensageria.setOperacao("LEITURA DE MSG NA FILA E ENVIO DE EMAIL.");
		logMensageria.setDataHora(new Date());

		try {
			// capturando os dados da mensagem..
			String to = dto.getEmailUsuario();
			String subject = dto.getAssunto();
			String body = dto.getTexto();

			// enviar a mensagem por email
			mailSenderComponent.sendMessage(to, subject, body);

			logMensageria.setDescricao("Mensagem lida com sucesso na fila e enviada por email.");
		} catch (Exception e) {
			logMensageria.setDescricao("Erro ao ler mensagem e enviar email: " + e.getMessage());
		} finally {
			logMensageriaRepository.save(logMensageria);
		}
	}
}
