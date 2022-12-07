package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/*
	 * @Autowired private UsuarioRepository userRepo;
	 */

	/* TEMPO DE VALIDADE DO TOKEN (NESTE CASO = 2 DIAS) */
	private static final long EXPIRATION_TIME = 172800000;

	/* Uma senha unica para compor a autenticação e ajudar na segurança */
	private static final String SECRET = "SenhaExtremamenteSecreta";

	/* Prefixo padrão de Token */
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";

	/* Gerando token de autenticação e adicionando ao cabeçalho e resposta Http */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		/* Montagem do token */
		String JWT = Jwts.builder()/* Chama o gerador de token */
				.setSubject(username)/* Add o usuario */
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))/* Tempo de expiração */
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /* Compactação e algoritmo de geração de senha */

		/* Junta o token com o prefixo */
		String token = TOKEN_PREFIX + " " + JWT; /* Bearer + 8787s8d7as8da7sd8as7 */

		/* Adiciona o cabeçalho Http */
		response.addHeader(HEADER_STRING, token); /* Authorization: Bearer + 8787s8d7as8da7sd8as7 */

		ApplicationContextLoad.getApplicationContext()
		.getBean(UsuarioRepository.class).atualizaTokenUser(JWT, username);
		
		
		//Liberando resposta para portas diferentes que usam API ou no caso clientes web
		liberacaoCors(response);
		
		/* Escreve token como resposta no corpo Http */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}

	/* Retorna o usuario validado com token ou caso não seja valido retorna null */
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		/* Pega o token enviado no cabeçalho http */
		String token = request.getHeader(HEADER_STRING);

		try {
		if (token != null) {

			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
			/* Faz a validação do token do user na requisição */
			String user = Jwts.parser().setSigningKey(SECRET) // Bearer + 8787s8d7as8da7sd8as7 *
					.parseClaimsJws(tokenLimpo) // 8787s8d7as8da7sd8as7
					.getBody().getSubject();// Retorna o usuario: Ex: João Silva
			if (user != null) {

				// userRepo.findUserByLogin(user);

				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
						.findUserByLogin(user);

				if (usuario != null) {

					if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {

						return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
								usuario.getAuthorities());
					}
				}

			}

		}
		}catch (io.jsonwebtoken.ExpiredJwtException e) {
			try {
				response.getOutputStream().println("Seu TOKEN está expirador, faça o login ou informe um nove TOKEN PARA AUTENTICAÇÃO");
			}catch (IOException e1) {}
		}
		liberacaoCors(response);
		return null; // Não Autorizado
	}

	private void liberacaoCors(HttpServletResponse response) {

		if (response.getHeader("Access-Control-Allow-Origin") == null ) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}

}
