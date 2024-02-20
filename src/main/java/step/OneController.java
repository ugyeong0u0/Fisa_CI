package step;

import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class OneController {
	
	@GetMapping("paths")
	public String get() {
		System.out.print("####");
		return "fififisa";
	}	
	
}
