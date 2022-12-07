package curso.api.rest.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "role")
@SequenceGenerator(name = "seq_role", sequenceName = "seq_role", allocationSize = 1, initialValue = 1)
public class Role implements GrantedAuthority{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sql_role")
	private Long id;
	
	private String nomeRole; /* Papel, exemplo ROLE_GERENTE... */

	@ManyToMany(mappedBy = "roles")
	private List<Usuario> usuarios = new ArrayList<>();
	
	@Override
	public String getAuthority() { /*  Retorna o nome do papel, acesso ou autorização. Exemplo ROLE_GERENTE*/
		return this.nomeRole;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeRole() {
		return nomeRole;
	}

	public void setNomeRole(String nomeRole) {
		this.nomeRole = nomeRole;
	}
	
	
}
