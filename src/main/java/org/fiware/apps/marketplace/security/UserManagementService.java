package org.fiware.apps.marketplace.security;

import org.fiware.apps.marketplace.bo.LocaluserBo;
import org.fiware.apps.marketplace.exceptions.UserNotFoundException;
import org.fiware.apps.marketplace.model.Localuser;
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
	LocaluserBo localuserBo = (LocaluserBo) appContext.getBean("localuserBo");

	@Autowired 
	private Assembler assembler;

	@Override
    public UserDetails loadUserByUsername(String username)
    	     throws UsernameNotFoundException, DataAccessException {
		
		try {
	        Localuser userEntity = localuserBo.findByName(username);
	        return assembler.buildUserFromUserEntity(userEntity);
		} catch (UserNotFoundException ex) {
        	throw new UsernameNotFoundException("user not found");
		}
	}

}
