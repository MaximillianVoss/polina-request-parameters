package com.example.requestparameters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void welcomesJohnToTheMoon() throws Exception {
		mockMvc.perform(get("/hello").param("location", "moon").param("name", "John"))
				.andExpect(status().isOk())
				.andExpect(content().string("Welcome to the moon John!"));
	}

	@Test
	void acceptsQueryParametersInReverseOrder() throws Exception {
		mockMvc.perform(get("/hello?name=John&location=moon"))
				.andExpect(status().isOk())
				.andExpect(content().string("Welcome to the moon John!"));
	}

	@Test
	void preservesSpacesInParameterValues() throws Exception {
		mockMvc.perform(get("/hello").param("location", "New York").param("name", "Mary Jane"))
				.andExpect(status().isOk())
				.andExpect(content().string("Welcome to the New York Mary Jane!"));
	}

	@Test
	void returnsBadRequestWhenLocationIsMissing() throws Exception {
		mockMvc.perform(get("/hello").param("name", "John"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void returnsBadRequestWhenNameIsMissing() throws Exception {
		mockMvc.perform(get("/hello").param("location", "moon"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void ignoresExtraLanguageParameter() throws Exception {
		mockMvc.perform(get("/hello")
				.param("location", "moon")
				.param("name", "John")
				.param("language", "English"))
				.andExpect(status().isOk())
				.andExpect(content().string("Welcome to the moon John!"));
	}
}
