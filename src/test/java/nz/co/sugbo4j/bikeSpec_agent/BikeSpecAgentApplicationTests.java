package nz.co.sugbo4j.bikeSpec_agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = { "classpath:application.yml", "classpath:config/retailers.yml" })
class BikeSpecAgentApplicationTests {

	@Test
	void contextLoads() {
	}

}
