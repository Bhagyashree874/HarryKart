package se.atg.service.harrykart.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.atg.service.harrykart.entity.HarryKart;
import se.atg.service.harrykart.entity.Rank;
import se.atg.service.harrykart.exception.HarryKartException;
import se.atg.service.harrykart.service.HarryKartService;
import se.atg.service.harrykart.utils.HarrykartUtils;

@RestController
@RequestMapping("/api")
public class HarryKartController {

	@Autowired
	HarryKartService service;

	@Autowired
	HarrykartUtils utils;

	@RequestMapping(method = RequestMethod.POST, path = "/play", consumes = "application/xml", produces = "application/json")

	public String playHarryKart(@RequestBody String inputXML) {

		try {

			// Creating object from raw xml (Deserialization)
			HarryKart hk = service.deserializeFromXML(inputXML);
			// Calculate the race results
			List<Rank> ranking = new HarryKartService(hk).getResults();
			// building JSON response(Serialization)
			return utils.convertToJson(ranking);
		} catch (HarryKartException e) {

			return "{\"message\": " + e.getMessage() + " }";

		}

	}

}