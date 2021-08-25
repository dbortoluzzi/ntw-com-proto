package eu.dbortoluzzi.consumer.controller;

import eu.dbortoluzzi.consumer.service.ConsumerFragmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Slf4j
public class FragmentController {

	@Autowired
	ConsumerFragmentService consumerFragmentService;

	@PostMapping("/api/consumer/fragment/{checksum}")
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public ResponseEntity<String> sendFragment(@RequestBody String data, @PathVariable  String checksum){
		// TODO: check checksum
		log.info("sendFragment: for {}..., {}", data.substring(0, 5), checksum);
		try {
			consumerFragmentService.addFragment(data, new Date());
			return new ResponseEntity<>("OK", HttpStatus.OK);
		}catch (Exception e) {
			log.error("error sendFragment", e);
			return new ResponseEntity<>("KO", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/api/consumer/fragment/sync/{checksum}")
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public ResponseEntity<String> sendFragmentForSync(@RequestBody String data, @PathVariable  String checksum){
		// TODO: check checksum
		log.info("sendFragmentForSync: for {}..., {}", data.substring(0, 5), checksum);
		try {
			consumerFragmentService.addFragment(data, new Date(), true);
			return new ResponseEntity<>("OK", HttpStatus.OK);
		}catch (Exception e) {
			log.error("error sendFragmentForSync", e);
			return new ResponseEntity<>("KO", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
