package info.jab.microservices.service.impl;

import java.util.List;

import info.jab.microservices.model.UserDetail;
import info.jab.microservices.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDetailRepository userDetailRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		final UserDetail user = userDetailRepository.getUserDetailByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("No user found with username: " + username);
		}

		return new User(user.getUsername(),
				user.getPassword(),
				List.of((GrantedAuthority) () -> "USER"));
	}
}