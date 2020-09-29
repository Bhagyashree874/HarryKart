package se.atg.service.harrykart.service;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import se.atg.service.harrykart.entity.HarryKart;
import se.atg.service.harrykart.entity.Rank;
import se.atg.service.harrykart.exception.HarryKartException;
import se.atg.service.harrykart.utils.HarrykartUtils;

@Service
public class HarryKartService {

	private static final double DISTANCE = 1000.0;

	private XmlMapper xmlMapper;
	HarryKart race;

	public HarryKartService() {
		this.xmlMapper = new XmlMapper();
		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// TODO Auto-generated constructor stub
	}

	public HarryKartService(HarryKart race) {
		this.race = race;
	}

	public HarryKart convertFromXML(String xmlString) throws HarryKartException {
		HarrykartUtils utils = new HarrykartUtils();
		// Before de-serializing, check the XML validity
		if (!utils.validateXml(xmlString)) {
			throw new HarryKartException("The Harry Kart input XML is not in a valid format.");
		}

		HarryKart objHarryKart = null;
		try {
			objHarryKart = xmlMapper.readValue(xmlString, HarryKart.class);
			/*
			 * After de-serializing the XML: 1) Verify that the number of loops matches the
			 * specified value 2) Verify that there are at least two race participants
			 */
			if (!utils.validateNumberOfLoops(objHarryKart)) {
				throw new HarryKartException("mismatch in <numberOfLoops> value and number of loops provided");
			}
			if (utils.numberOfParticipants(objHarryKart) < 2) {
				throw new HarryKartException("A race can't happen with only 1 participant");
			}

		} catch (IOException e) {
			System.out.println("IOException while trying to de-serialize input XML");
			System.out.println(e);
		}
		return objHarryKart;
	}

	/**
	 * Calculate the rank of the race participants
	 *
	 * @return List<Rank> List of participants ranked by their order of race
	 *         completion
	 */
	public List<Rank> getRanking() {
		ArrayList<Rank> rankingSequence = new ArrayList<>();

		/*
		 * For each participant 1) Go through each race loop 2) Get the lane where
		 * lane.number = participant.lane 3) baseSpeed += lane.power. baseSpeed <= 0
		 * means the lap is not run and the horse is out of the race. 4) time +=
		 * DISTANCE / baseSpeed
		 */
		race.getStartList().forEach(participant -> {
			Rank rank = new Rank(0, participant.getName(), DISTANCE / participant.getBaseSpeed());
			race.getPowerUps().stream().forEach(loop -> loop.getLanes().stream()
					.filter(lane -> lane.getNumber() == participant.getLane()).forEach(lane -> {
						int loopSpeed = participant.getBaseSpeed() + lane.getPowerValue();
						if (loopSpeed <= 0) {
							rank.setTime(rank.getTime() + Double.MAX_VALUE); // Time set to the highest possible value.
																				// Horse is last place.
						} else {
							participant.setBaseSpeed(participant.getBaseSpeed() + lane.getPowerValue());
							rank.setTime(rank.getTime() + (DISTANCE / participant.getBaseSpeed()));
						}
					}));
			rankingSequence.add(rank); // Add rank to collection
		});

		Collections.sort(rankingSequence); // Sort with lowest times first
		rankingSequence.removeIf(rank -> rank.getTime() >= Double.MAX_VALUE); // Remove participants who do not complete
																				// the
		// race

		// Each participant is given rank according to times.If they complete at same
		// time,they have equal rank.
		int finalPosition = 1;
		rankingSequence.get(0).setPosition(finalPosition);
		for (int indexPosition = 1; indexPosition < rankingSequence.size(); indexPosition++) {
			if (rankingSequence.get(indexPosition).getTime() == rankingSequence.get(indexPosition - 1).getTime()) {
				rankingSequence.get(indexPosition).setPosition(finalPosition);
			} else {
				rankingSequence.get(indexPosition).setPosition(++finalPosition);
			}
		}
		// Display first 3 positions only
		return rankingSequence.stream().filter(rank -> rank.getPosition() <= 3).collect(toList());
	}

}
