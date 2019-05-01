package br.com.leoneperdigao.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;

import br.com.leoneperdigao.interfaces.JasperS3Pdf;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class JasperS3PdfImpl implements JasperS3Pdf {

	@Autowired
	private AmazonS3 s3;

	@Override
	public byte[] gerarPdfS3(String bucketName, String keyName, Map<String, String> mapValues, String... imageKeys)
			throws JRException {
		Object loadedObject = JRLoader.loadObject(s3.getUrl(bucketName, keyName));
		JasperReport jasperReport = (JasperReport) loadedObject;
		JRField[] params = jasperReport.getFields();
		
		Collection<Map<String, ?>> listMap = prepararParametros(bucketName, mapValues, params, imageKeys);
		
		JRMapCollectionDataSource jrMap = new JRMapCollectionDataSource(listMap);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), jrMap);

		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	private Collection<Map<String, ?>> prepararParametros(String bucketName, Map<String, String> mapValues,
			JRField[] params, String... imageKeys) {
		HashMap<String, Object> parameterValueMap = new HashMap<>();
		Collection<Map<String, ?>> listMap = new ArrayList<>();

		Arrays.stream(params).forEach(v -> parameterValueMap.put(v.getName(), mapValues.get(v.getName())));
		Arrays.stream(imageKeys).forEach(v -> parameterValueMap.put(v.split("\\.")[0], s3.getUrl(bucketName, v).toExternalForm()));

		listMap.add(parameterValueMap);
		return listMap;
	}

}
