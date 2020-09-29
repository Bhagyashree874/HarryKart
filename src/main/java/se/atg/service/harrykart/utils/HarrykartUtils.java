package se.atg.service.harrykart.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.atg.service.harrykart.entity.HarryKart;
import se.atg.service.harrykart.entity.Rank;

@Component
public class HarrykartUtils {
	private static final String XSD_FILE = "input.xsd";

	public String convertToJson(List<Rank> ranking) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonRankings = "[]";
		try {
			jsonRankings = mapper.writeValueAsString(ranking);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{\"Processing Error From Json\": " + e.getMessage() + " }";
		}
		return "{\"ranking\": " + jsonRankings + " }";
	}

	private String getResource(String filename) throws FileNotFoundException {
		URL resource = getClass().getClassLoader().getResource(filename);
		Objects.requireNonNull(resource);
		return resource.getFile();
	}

	public int numberOfParticipants(HarryKart hk) {
		return hk.getStartList().size();
	}

	public String readFileToString(String filename) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
		Objects.requireNonNull(in);
		String inputXmlString = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			inputXmlString = br.lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			e.printStackTrace();
			return inputXmlString;
		}
		return inputXmlString;
	}

	public boolean validateNumberOfLoops(HarryKart hk) {
		return hk.getNumberOfLoops() == hk.getPowerUps().size() + 1;
	}

	public boolean validateXml(String inputXmlString) {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = schemaFactory.newSchema(new File(getResource(XSD_FILE)));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new StringReader(inputXmlString)));
			return true;
		} catch (SAXException | IOException e) {
			return false;
		}
	}
}
