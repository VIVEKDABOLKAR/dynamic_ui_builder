package dynamicUi.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"dynamicUi.demo", "dynamicUi/demo/controller", "dynamicUi/demo/service", "dynamicUi/demo/entity", "dynamicUi/demo/repoistory"})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
