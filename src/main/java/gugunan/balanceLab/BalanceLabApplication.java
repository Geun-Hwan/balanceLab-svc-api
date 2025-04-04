package gugunan.balanceLab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = { "gugunan.balanceLab" })
@EnableAsync
public class BalanceLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(BalanceLabApplication.class, args);
	}

}
