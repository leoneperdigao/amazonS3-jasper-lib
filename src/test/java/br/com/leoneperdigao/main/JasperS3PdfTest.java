package br.com.leoneperdigao.main;

import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.ContextConfiguration;

import com.amazonaws.services.s3.AmazonS3;

import br.com.leoneperdigao.configurations.TestConfiguration;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.base.JRBaseField;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.fill.JasperReportSource;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.util.ContextClassLoaderObjectInputStream;
import net.sf.jasperreports.engine.util.JRLoader;

@Getter
@Setter
@RunWith(PowerMockRunner.class)
@PrepareForTest({ URL.class, JRLoader.class, JasperFillManager.class, JasperExportManager.class })
@ContextConfiguration(classes = TestConfiguration.class)
public class JasperS3PdfTest {

	private static final String BUCKET_NAME = "BUCKET";
	private static final String PDF_KEY_S3 = "PDF_S3";
	private static final String IMAGE_URL_S3 = "PDF_S3";

	private static final String MAP_KEY_1 = "1";
	private static final String MAP_VALUE_1 = "10";
	private static final String MAP_KEY_2 = "2";
	private static final String MAP_VALUE_2 = "20";
	
	private static final String IMAG1_KEY = "imageKey";
	
	private static final byte BYTE_ARRAY[] = new byte[] {1, 6, 3};

	@Mock
	private AmazonS3 s3;

	@InjectMocks
	private JasperS3PdfImpl interPdf;

	@Mock
	private ContextClassLoaderObjectInputStream cclois;

	private URL url;
	private HttpsURLConnection huc;
	private JRMapCollectionDataSource jmapDs;
	private JasperPrint jasperPrint;
	private JasperReportSource reportSource;
	private PrintPageFormat pageFormat;

	@Before
	public void initMocks() throws Exception {
		PowerMockito.mockStatic(JRLoader.class);
		PowerMockito.mockStatic(JasperFillManager.class);
		PowerMockito.mockStatic(JasperExportManager.class);
		MockitoAnnotations.initMocks(this);
		preapareJasper();
	}

	@Test
	public void testGerarPdfS3Success() throws Exception {
		JRBaseField baseFiled = mock(JRBaseField.class);
		JRField[] fields = new JRField[] {baseFiled, baseFiled};
		JasperReport jasper = mock(JasperReport.class);

		when(getReportSource().getReport()).thenReturn(jasper);
		when(getReportSource().getReport().getPrintOrderValue()).thenReturn(PrintOrderEnum.VERTICAL);
		when(JRLoader.loadObject(getUrl())).thenReturn(jasper);
		when(getS3().getUrl(BUCKET_NAME, PDF_KEY_S3)).thenReturn(getUrl());
		when(getS3().getUrl(BUCKET_NAME, IMAG1_KEY)).thenReturn(getUrl());
		when(getS3().getUrl(BUCKET_NAME, IMAG1_KEY).toExternalForm()).thenReturn(IMAGE_URL_S3);
		when(jasper.getFields()).thenReturn(fields);
		when(JasperFillManager.fillReport(jasper, new HashMap<>(), getJmapDs())).thenReturn(getJasperPrint());
		when(JasperExportManager.exportReportToPdf(getJasperPrint())).thenReturn(BYTE_ARRAY);
		when(interPdf.gerarPdfS3(BUCKET_NAME, PDF_KEY_S3, createMapValues(), IMAG1_KEY)).thenReturn(BYTE_ARRAY);

		byte[] pdf = interPdf.gerarPdfS3(BUCKET_NAME, PDF_KEY_S3, createMapValues(), IMAG1_KEY);

		assertNotNull(pdf);
	}

	private Map<String, String> createMapValues() {
		Map<String, String> mapValues = new HashMap<>();
		mapValues.put(MAP_KEY_1, MAP_VALUE_1);
		mapValues.put(MAP_KEY_2, MAP_VALUE_2);
		return mapValues;
	}

	private void preapareJasper() throws Exception {
		setUrl(mock(URL.class));
		setHuc(mock(HttpsURLConnection.class));
		setJmapDs(mock(JRMapCollectionDataSource.class));
		setJasperPrint(mock(JasperPrint.class));
		setReportSource(mock(JasperReportSource.class));
		setPageFormat(mock(PrintPageFormat.class));
		whenNew(URL.class).withArguments(ArgumentMatchers.anyString()).thenReturn(getUrl());
		when(getUrl().openConnection()).thenReturn(getHuc());
		whenNew(ContextClassLoaderObjectInputStream.class).withAnyArguments().thenReturn(getCclois());
		whenNew(JRMapCollectionDataSource.class).withAnyArguments().thenReturn(getJmapDs());
		when(getJasperPrint().getPageFormat(0)).thenReturn(getPageFormat());
		when(getPageFormat().getOrientation()).thenReturn(OrientationEnum.PORTRAIT);
	}
}
