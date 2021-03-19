package br.com.generatecsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class CreateCSV implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(CreateCSV.class);
	

	@Override
	public void afterPropertiesSet() throws Exception {

		create02();

	}
	
	private void create02() {

		DecimalFormat formater = new DecimalFormat("0.00");
		formater.setCurrency(Currency.getInstance(new Locale("pt", "BR")));

		List<String[]> dataLines = new ArrayList<>();
		dataLines.add(new String[] { "TP_DOC","DOCUMENTO", "PCT_DSC_CORR", "PCT_ENT", "TAXA", "PCL", "CARENCIA","VLR_ENT","CUSTOMER_ID","CUSTOMER_ID_","RISCOPROVISAO","PCT_DESC","GERENCIAL","CONTABIL","CTB_CORRIGIDO","TELEFONE","ATRASO","FX_ATRASO","VLR_PCL","RESID","COMERCIAL","CELULAR"});

		for (int i = 0; i < 50000; i++) {
			
			Long docNumber = null;
			String docType = null;
			if(getRandomBoolean()) {
				docNumber = generateCPF();
				docType = "CPF";
			}else {
				docNumber = generateCNPJ();
				docType = "CNPJ";
			}
			
			dataLines.add(new String[] {  String.valueOf(docType), String.valueOf(docNumber), formater.format(percentual(100)),
					 formater.format(percentual(50)),  formater.format(taxa(999)), String.valueOf(parcelas()),
					String.valueOf(parcelas()),"#","#","#","#","#","#","#","#","#","#","#","#","#","#","#"});
		}

		File csvOutputFile = new File(new Date().getTime() + ".csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			dataLines.stream().map(this::convertToCSV).forEach(pw::println);
		} catch (FileNotFoundException e) {
			LOG.error("IOException " + e.getMessage());
		}

		// csvOutputFile.deleteOnExit();
	}

	private void create01() {

		DecimalFormat formater = new DecimalFormat("0.00");
		formater.setCurrency(Currency.getInstance(new Locale("pt", "BR")));

		List<String[]> dataLines = new ArrayList<>();
		dataLines.add(new String[] { "TIPO_DOCUMENTO","DOCUMENTO", "PERC_DESCONTO", "PERC_ENTRADA", "TAXA_JUROS", "QTD_PARCELAS", "DIAS_CARENCIA_PRI_PARCELA"});

		for (int i = 0; i < 1000000; i++) {
			
			Long docNumber = null;
			String docType = null;
			if(getRandomBoolean()) {
				docNumber = generateCPF();
				docType = "CPF";
			}else {
				docNumber = generateCNPJ();
				docType = "CNPJ";
			}
			
			dataLines.add(new String[] {  String.valueOf(docType), String.valueOf(docNumber), formater.format(percentual(100)),
					 formater.format(percentual(50)),  formater.format(taxa(999)), String.valueOf(parcelas()),
					String.valueOf(parcelas()) });
		}

		File csvOutputFile = new File(new Date().getTime() + ".csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			dataLines.stream().map(this::convertToCSV).forEach(pw::println);
		} catch (FileNotFoundException e) {
			LOG.error("IOException " + e.getMessage());
		}

		// csvOutputFile.deleteOnExit();
	}	

	public double percentual(int max) {

		Random r = new Random();
		int randomInt = (r.nextInt(max) + 1);
		return truncateDecimal((randomInt * 0.01), 2).doubleValue();

	}
	
	public double taxa(int max) {

		Random r = new Random();
		int randomInt = (r.nextInt(max) + 1);
		return truncateDecimal((randomInt * 0.01), 2).doubleValue();

	}

	public int parcelas() {

		Random r = new Random();
		int t = (r.nextInt(80) + 1);
		return t;
	}

	public long generateCPF() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return random.nextLong(10_000_000_000L, 100_000_000_000L);
	}
	
	public long generateCNPJ() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return random.nextLong(10_000_000_000_000L, 100_000_000_000_000L);
	}

	private static BigDecimal truncateDecimal(double x, int numberofDecimals) {
		if (x > 0) {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
		} else {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
		}
	}

	public boolean getRandomBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}

	public String convertToCSV(String[] data) {
		return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining(";"));
	}

	public String escapeSpecialCharacters(String data) {
		return data.replaceAll("^\"|\"$", "");		
	}
}
