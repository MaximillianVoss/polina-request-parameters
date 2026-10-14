package com.example.requestparameters;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class HelloController {

	@GetMapping("/hello")
	public String hello(
			@RequestParam(name = "location") String location,
			@RequestParam(name = "name") String name) {
		return "Welcome to the " + location + " " + name + "!";
	}
}
