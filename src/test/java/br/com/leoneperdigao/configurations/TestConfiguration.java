package br.com.leoneperdigao.configurations;

import org.mockito.Answers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.amazonaws.services.s3.AmazonS3;

import br.com.leoneperdigao.main.JasperS3PdfImpl;

@Configuration
@ImportResource("classpath:application-context-test.xml")
public class TestConfiguration {

	@MockBean(answer = Answers.CALLS_REAL_METHODS)
	public AmazonS3 s3;

	@MockBean(answer = Answers.CALLS_REAL_METHODS)
	public JasperS3PdfImpl interPdf;
}
