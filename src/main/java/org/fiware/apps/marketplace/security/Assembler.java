package org.fiware.apps.marketplace.security;

import java.util.ArrayList;
import java.util.Collection;

import org.fiware.apps.marketplace.model.Localuser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("assembler")
public class Assembler {
	
	@Transactional(readOnly = true)
	User buildUserFromUserEntity(Localuser user) {

		String username = user.getUsername();
		String password = user.getPassword();

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		/*for (SecurityRoleEntity role : user.getRoles()) {
			authorities.add(new GrantedAuthorityImpl(role.getRoleName()));
		}
		 */
		
		GrantedAuthority role = new GrantedAuthorityImpl("ROLE_USER");
		authorities.add(role);
		User springUser = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		return springUser;
	}

}
