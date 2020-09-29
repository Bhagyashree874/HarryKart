package se.atg.service.harrykart.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.atg.service.harrykart.entity.HarryKart;
import se.atg.service.harrykart.entity.Rank;
import se.atg.service.harrykart.exception.HarryKartException;
import se.atg.service.harrykart.service.HarryKartService;
import se.atg.service.harrykart.utils.HarrykartUtils;

public class ControllerTest {
	ObjectMapper mapper;

	private HarrykartUtils utils;

	private HarryKartService service;

	/**
	 * XML file is not in a valid format: <numberOfLoops> does not match the actual
	 * number of loops listed
	 *
	 * @throws HarryKartException
	 */
	@Test(expected = HarryKartException.class)
	public void invalidXmlFormatTest() throws HarryKartException {
		String inputXML = utils.readFileToString("NotAValidXMLFormat.xml");
		HarryKart hk = service.deserializeFromXML(inputXML);
	}

	/**
	 * Less than 4 participants doesn't make a race (it throws an exception)
	 *
	 * @throws HarryKartException
	 */
	@Test(expected = HarryKartException.class)
	public void minimumParticipantTest() throws HarryKartException {
		String inputXML = utils.readFileToString("MinimumNumberOfParticipants.xml");
		HarryKart hk = service.deserializeFromXML(inputXML);
	}

	/**
	 * When the base power is less than 1 on a loop, the participant hasn't
	 * completed the lap and is out of the race
	 *
	 * @throws HarryKartException
	 */
	@Test
	public void NegativeandZeroPowerUps() throws HarryKartException {
		String inputXML = utils.readFileToString("NegativeandZeroPowerUps.xml");
		System.out.println("Input XML" + inputXML);
		HarryKart hk = service.deserializeFromXML(inputXML);
		List<Rank> actualRanking = new HarryKartService(hk).getResults();
		// Expected race outcome
		ArrayList<Rank> expectedRanking = new ArrayList<>();
		expectedRanking.add(new Rank(1, "WAIKIKI SILVIO", 0.0));
		expectedRanking.add(new Rank(2, "HERCULES BOKO", 0.0));
		// Compare expected and actual JSON results
		String resultJson = utils.convertToJson(actualRanking);
		String expectedJson = utils.convertToJson(expectedRanking);
		assertEquals(resultJson, expectedJson);
	}

	/**
	 *
	 * @param filename XML filename to be read from /resources
	 * @return
	 */

	@Before
	public void setUp() throws Exception {
		service = new HarryKartService();
		mapper = new ObjectMapper();
		utils = new HarrykartUtils();
	}

	/**
	 * Testing the input 0 given in the question
	 *
	 * @throws HarryKartException
	 */
	@Test
	public void testingTheGivenInput0() throws HarryKartException {
		String inputXML = utils.readFileToString("input_0.xml");
		System.out.println("Input XML" + inputXML);
		HarryKart hk = service.deserializeFromXML(inputXML);
		List<Rank> actualRanking = new HarryKartService(hk).getResults();
		// Expected race outcome
		ArrayList<Rank> expectedRanking = new ArrayList<>();
		expectedRanking.add(new Rank(1, "TIMETOBELUCKY", 0.0));
		expectedRanking.add(new Rank(2, "HERCULES BOKO", 0.0));
		expectedRanking.add(new Rank(3, "CARGO DOOR", 0.0));
		// Compare expected and actual JSON results
		String resultJson = utils.convertToJson(actualRanking);
		String expectedJson = utils.convertToJson(expectedRanking);
		assertEquals(resultJson, expectedJson);
	}

	/**
	 * Testing the input 1 given in the question
	 *
	 * @throws HarryKartException
	 */
	@Test
	public void testingTheGivenInput1() throws HarryKartException {
		String inputXML = utils.readFileToString("input_1.xml");
		System.out.println("Input XML" + inputXML);
		HarryKart hk = service.deserializeFromXML(inputXML);
		List<Rank> actualRanking = new HarryKartService(hk).getResults();
		// Expected Output
		ArrayList<Rank> expectedRanking = new ArrayList<>();
		expectedRanking.add(new Rank(1, "WAIKIKI SILVIO", 0.0));
		expectedRanking.add(new Rank(2, "TIMETOBELUCKY", 0.0));
		expectedRanking.add(new Rank(3, "HERCULES BOKO", 0.0));
		// Compare expected and actual JSON results
		String resultJson = utils.convertToJson(actualRanking);
		String expectedJson = utils.convertToJson(expectedRanking);
		assertEquals(resultJson, expectedJson);
	}

	@Test
	public void TieTest() throws HarryKartException {

		String inputXML = utils.readFileToString("TieTest.xml");
		HarryKart hk = service.deserializeFromXML(inputXML);
		// Calculate the race results
		List<Rank> actualRanking = new HarryKartService(hk).getResults();
		// Verify that the finishing position of each participant is #1
		actualRanking.forEach(rank -> assertEquals(rank.getPosition(), 1));
	}

	/**
	 * Two participants finish at the same time
	 *
	 * @throws HarryKartException
	 */
	@Test
	public void twoWayTieTest() throws HarryKartException {
		String inputXML = utils.readFileToString("TieForTwoPositions.xml");
		HarryKart hk = service.deserializeFromXML(inputXML);
		// Calculate the race results
		List<Rank> actualRanking = new HarryKartService(hk).getResults();
		// Verify the finishing position of each participant
		assertEquals(actualRanking.get(0).getPosition(), 1);
		assertEquals(actualRanking.get(0).getHorse(), "WAIKIKI SILVIO");
		assertEquals(actualRanking.get(1).getPosition(), 2);
		assertEquals(actualRanking.get(1).getHorse(), "HERCULES BOKO");
		String thirdPlace = actualRanking.get(2).getHorse();
		assertEquals(actualRanking.get(2).getPosition(), 3);
		assertTrue(thirdPlace.equals("CARGO DOOR") || thirdPlace.equals("TIMETOBELUCKY"));
		String fourthPlace = actualRanking.get(3).getHorse();
		assertEquals(actualRanking.get(3).getPosition(), 3);
		assertTrue(fourthPlace.equals("CARGO DOOR") || fourthPlace.equals("TIMETOBELUCKY"));
	}
}
