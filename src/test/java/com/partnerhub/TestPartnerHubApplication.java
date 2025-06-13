package com.partnerhub;

import org.springframework.boot.SpringApplication;

public class TestPartnerHubApplication {

	public static void main(String[] args) {
		SpringApplication.from(PartnerHubApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
