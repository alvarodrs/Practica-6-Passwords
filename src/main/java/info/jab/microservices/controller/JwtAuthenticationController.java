package info.jab.microservices.controller;

import info.jab.microservices.model.JwtRequest;
import info.jab.microservices.model.JwtResponse;
import info.jab.microservices.model.UserChangePasswordRequest;
import info.jab.microservices.model.UserDetail;
import info.jab.microservices.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import info.jab.microservices.config.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService jwtInMemoryUserDetailsService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserDetailRepository userDetailRepository;

	@PostMapping(value = "/api/login")
	public ResponseEntity<?> login(@Valid @RequestBody JwtRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = jwtInMemoryUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok().body(new JwtResponse(token));
	}

	private void authenticate(String username, String password) throws Exception {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@PostMapping(value = "/api/users/update-password")
	public ResponseEntity<UserDetail> change_Password(@RequestBody UserChangePasswordRequest ucpr) {

		if (!ucpr.getNewPassword().equals(ucpr.getNewPassword2()))
			//return ResponseEntity.badRequest().body(new UserChangePasswordResponse("failure", "Passwords are not equal"));
			return ResponseEntity.badRequest().body(null);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String Name = authentication.getName();
		UserDetails ud = jwtInMemoryUserDetailsService.loadUserByUsername(Name);

		UserDetail user = changePassword(ud, ucpr.getNewPassword(), ucpr.getCurrentPassword());

		if (user == null) //return ResponseEntity.badRequest().body(new UserChangePasswordResponse("failure", "Old password is not correct!"));
			return ResponseEntity.badRequest().body(user);

		return ResponseEntity.ok().body(user);

	}


	private UserDetail changePassword(UserDetails userDetails, String newPassword, String oldPassword) {

		if (!passwordEncoder.matches(oldPassword, userDetails.getPassword()))
			return null;

		String passwordEncoded = passwordEncoder.encode(newPassword);
		UserDetail user = userDetailRepository.getUserDetailByUserName(userDetails.getUsername());
		user.setPassword(passwordEncoded);

		userDetailRepository.save(user);

		return user;

	}

}
