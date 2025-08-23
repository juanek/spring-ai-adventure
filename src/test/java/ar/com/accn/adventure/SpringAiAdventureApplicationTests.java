package ar.com.accn.adventure;

import ar.com.accn.adventure.model.AdventureRequest;
import ar.com.accn.adventure.model.AdventureResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAiAdventureApplicationTests {

	@Test
	void contextLoads() {
		AdventureRequest adventureRequest = new AdventureRequest();
		adventureRequest.setComplexity("s");
		System.out.println(adventureRequest.getGenre());
		AdventureResponse adventureResponse = new AdventureResponse("","","");

	}

}
