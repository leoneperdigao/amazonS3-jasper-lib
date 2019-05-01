package br.com.leoneperdigao.interfaces;

import java.util.Map;

import net.sf.jasperreports.engine.JRException;

public interface JasperS3Pdf {

	byte[] gerarPdfS3(String bucketName, String keyName, Map<String, String> mapValues, String... imageKeys)
			throws JRException;
}
