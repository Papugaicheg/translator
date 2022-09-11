package com.tinkoff.translator;

import com.tinkoff.translator.db.JDBCUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TranslatorApplication {

	public static void main(String[] args) {

		SpringApplication.run(TranslatorApplication.class, args);
		JDBCUtils.createTables();
	}

}
