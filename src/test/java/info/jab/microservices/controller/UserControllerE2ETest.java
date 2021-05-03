package info.jab.microservices.controller;

import info.jab.microservices.model.JwtRequest;
import info.jab.microservices.model.JwtResponse;
import info.jab.microservices.model.UserChangePasswordRequest;
import info.jab.microservices.model.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerE2ETest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private String getToken(String username, String password) {

		//Given
		String address = "http://localhost:" + port + "/api/login";
		JwtRequest jwtRequest = new JwtRequest(username, password);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JwtRequest> request = new HttpEntity<>(jwtRequest, headers);

		//When
		ResponseEntity<JwtResponse> result = this.restTemplate.postForEntity(address, request, JwtResponse.class);

		then(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		then(result.getBody().getJwttoken()).isNotEmpty();

		return result.getBody().getJwttoken();
	}

	@Transactional
	@Test
	//@Sql(scripts= "/data.sql") //to created DB tables and init sample DB data
	public void given_app_when_call_change_password_then_ok() {

		//Given
		String token = getToken("rogers63", "password");

		//When
		String address = "http://localhost:" + port + "/api/users/update-password";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		UserChangePasswordRequest userChangePasswordRequest =
				new UserChangePasswordRequest(
						"password",
						"cereza",
						"cereza");
		HttpEntity<UserChangePasswordRequest> request = new HttpEntity<>(userChangePasswordRequest, headers);
		ResponseEntity<UserDetail> response = restTemplate.postForEntity(address, request, UserDetail.class);

		//Then
		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		String tokenNew = getToken("rogers63", "cereza");
		then(tokenNew).isNotEmpty();
	}

	@Transactional
	@Test
	//@Sql(scripts= "/data.sql")
	public void given_app_when_call_change_password_with_bad_values_then_ko() {

		//Given
		String token = getToken("rogers63", "password");

		//When
		String address = "http://localhost:" + port + "/api/users/update-password";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		UserChangePasswordRequest userChangePasswordRequest =
				new UserChangePasswordRequest(
						"password",
						"cereza",
						"platano");
		HttpEntity<UserChangePasswordRequest> request = new HttpEntity<>(userChangePasswordRequest, headers);
		ResponseEntity<UserDetail> response = restTemplate.postForEntity(address, request, UserDetail.class);

		//Then
		then(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
}