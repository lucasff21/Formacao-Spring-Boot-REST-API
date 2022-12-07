package curso.api.rest.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.UsuarioRepository;


@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository userRepo;
	
	@GetMapping(value = "/{id}/relatoriopdf")
	public ResponseEntity<UsuarioDTO> relatorio(@PathVariable (value = "id") Long id){
		
		Optional<Usuario> usuario = userRepo.findById(id);
		
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
		
	}
	
	//@CrossOrigin //FICA LIBERADO PARA QUALQUER URL
	@GetMapping(value = "/{id}")
	@CacheEvict(value="cacheuser", allEntries = true) //Remove cache não utilizado por um longo periodo
	@CachePut("cacheuser") // Pega e verfica mudanças no banco de dados
	public ResponseEntity<UsuarioDTO> init(@PathVariable (value = "id") Long id){
		
		Optional<Usuario> usuario = userRepo.findById(id);
		
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
		
	}
	
	//@CrossOrigin(origins = "www.EXEMPLOURL.com.br")
	@GetMapping(value = "/", produces = "application/json")
	//@Cacheable("cacheusuarios")
	@CacheEvict(value="cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> getAll() throws InterruptedException{
		
		List<Usuario> list = userRepo.findAll();
		
		//Thread.sleep(6000); //Segura o codigo por 6 segundos simulando um processo lento
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);       
	}
	

	//@CrossOrigin(origins = "www.EXEMPLOURL222.com.br")
	@PostMapping(value="/")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws IOException{
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUser(usuario);
		}
		
		//Consumindo API EXTERNA
		URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		//<><><><><><><><><><><>
		
		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);
		Usuario user  = userRepo.save(usuario);
		
		return new ResponseEntity<Usuario>(user, HttpStatus.OK);
	}
	
	
	@PutMapping(value = "/")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario){
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUser(usuario);
		}
		
		Usuario userTemporario = userRepo.findById(usuario.getId()).get();
		
		if(!userTemporario.getSenha().equals(usuario.getSenha())) {
			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);
		}
		
		Usuario user = userRepo.save(usuario);
		
		return new ResponseEntity<Usuario>(user, HttpStatus.OK); 
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable (value = "id") Long id){
		
		userRepo.deleteById(id);
		
		return ResponseEntity.ok().build();
	}
	
	
}
