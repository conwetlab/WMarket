package org.fiware.apps.marketplace.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("assembler")
public class Assembler {
	
	@Transactional(readOnly = true)
	User buildUserFromUserEntity(org.fiware.apps.marketplace.model.User user) {

		String username = user.getUserName();
		String password = user.getPassword();

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		/*
		for (SecurityRoleEntity role : user.getRoles()) {
			authorities.add(new GrantedAuthorityImpl(role.getRoleName()));
		}
		 */
		
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		User springUser = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		return springUser;
	}

}
