package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplementacaoUserDetailsService implement;
	
	/*Configura as solicitações de acesso por Http*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/*Ativiando proteção contra users que não estão validados por token*/
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		/*Ativando restrição a URL */
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		
		
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		
		/*URL DE Logout - após o user deslogar do sistema*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		/*Mapeia a URL de logout e invalida o Usuario*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/*Filtra as requisições de login para autenticação*/
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
		
		//Filtra demais requisições para verifica a presença do TOKEN JWT no HEADER HTTP
		.addFilterBefore(new JWTApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		/*Service que irá consultar o usuario no banco de dados*/
		auth.userDetailsService(implement)
		/*Padrão de codificação de senha*/
		.passwordEncoder(new BCryptPasswordEncoder());
	}
}
