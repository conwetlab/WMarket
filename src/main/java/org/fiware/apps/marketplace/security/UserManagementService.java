package org.fiware.apps.marketplace.security;

import org.fiware.apps.marketplace.bo.UserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.User;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userManagementService")
public class UserManagementService implements UserDetailsService{

	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();	
	UserBo userBo = (UserBo) appContext.getBean("userBo");

	@Autowired 
	private Assembler assembler;

	@Override
    public UserDetails loadUserByUsername(String username)
    	     throws UsernameNotFoundException, DataAccessException {
		
		try {
	        User userEntity = userBo.findByName(username);
	        return assembler.buildUserFromUserEntity(userEntity);
		} catch (UserNotFoundException ex) {
        	throw new UsernameNotFoundException("user not found");
		}
	}

}
