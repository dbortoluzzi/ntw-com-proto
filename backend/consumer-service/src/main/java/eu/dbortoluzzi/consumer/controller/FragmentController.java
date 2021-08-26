package eu.dbortoluzzi.consumer.controller;

import eu.dbortoluzzi.commons.utils.StringUtils;
import eu.dbortoluzzi.consumer.model.StatisticsCounter;
import eu.dbortoluzzi.consumer.service.ConsumerFragmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class FragmentController {

	public static final String PATTERN_DATETIME = "yyyyMMddHHmmss";
	@Autowired
	ConsumerFragmentService consumerFragmentService;

	@PostMapping("/api/consumer/fragment/{checksum}")
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public ResponseEntity<String> sendFragment(@RequestBody String data, @PathVariable  String checksum){
		if (data == null || !StringUtils.md5sum(data).equals(checksum)) {
			log.error("sendFragment: error for checksum {}", checksum);
			return new ResponseEntity<>("KO", HttpStatus.BAD_REQUEST);
		}
		log.info("sendFragment: for {}", checksum);
		try {
			consumerFragmentService.addFragment(data, new Date());
			return new ResponseEntity<>("OK", HttpStatus.OK);
		}catch (Exception e) {
			log.error("error sendFragment", e);
			return new ResponseEntity<>("KO", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/api/consumer/fragment/sync/{syncedFromInstance}/{checksum}")
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public ResponseEntity<String> sendFragmentForSync(@RequestBody String data, @PathVariable  String syncedFromInstance, @PathVariable  String checksum){
		if (data == null || !StringUtils.md5sum(data).equals(checksum)) {
			log.error("sendFragment: error for checksum {}", checksum);
			return new ResponseEntity<>("KO", HttpStatus.BAD_REQUEST);
		}
		log.info("sendFragmentForSync: from {} for {}", syncedFromInstance, checksum);
		try {
			consumerFragmentService.addFragment(data, new Date(), true, syncedFromInstance);
			return new ResponseEntity<>("OK", HttpStatus.OK);
		}catch (Exception e) {
			log.error("error sendFragmentForSync", e);
			return new ResponseEntity<>("KO", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/api/consumer/fragment/statistics/total/{from}/{to}/{resolution}")
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public ResponseEntity<List<StatisticsCounter>> statisticsFragmentByIntervalAndProducers(
			@PathVariable @DateTimeFormat(pattern= PATTERN_DATETIME) Date from,
			@PathVariable @DateTimeFormat(pattern= PATTERN_DATETIME) Date to,
			@PathVariable Long resolution,
			@RequestParam(required = false) List<String> filterProducers
	){
		if (from == null || to == null || resolution == null || to.before(from)) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
		}
		try {
			List<StatisticsCounter> statisticsCounters = consumerFragmentService.countFragmentsByProducers(from, to, filterProducers, resolution*60);
			return new ResponseEntity<>(statisticsCounters, HttpStatus.OK);
		}catch (Exception e) {
			log.error("error statisticsFragmentByIntervalAndProducers", e);
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/api/consumer/fragment/statistics/detailed/{from}/{to}/{resolution}")
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public ResponseEntity<List<StatisticsCounter>> statisticsFragmentByIntervalProducersAndConsumers(
			@PathVariable @DateTimeFormat(pattern= PATTERN_DATETIME) Date from,
			@PathVariable @DateTimeFormat(pattern= PATTERN_DATETIME) Date to,
			@PathVariable Long resolution,
			@RequestParam(required = false) List<String> filterProducers,
			@RequestParam(required = false) List<String> filterConsumers
	){
		if (from == null || to == null || resolution == null || to.before(from)) {
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
		}
		try {
			List<StatisticsCounter> statisticsCounters = consumerFragmentService.countFragmentsByProducersAndConsumers(from, to, filterProducers, filterConsumers, resolution*60);
			return new ResponseEntity<>(statisticsCounters, HttpStatus.OK);
		}catch (Exception e) {
			log.error("error statisticsFragmentByIntervalProducersAndConsumers", e);
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
