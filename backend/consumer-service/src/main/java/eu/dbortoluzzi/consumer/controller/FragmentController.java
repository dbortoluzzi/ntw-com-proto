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
		log.info("sendFragment: for {}, {}", data, checksum);
		try {
			consumerFragmentService.addFragment(data, new Date());
			return new ResponseEntity<>("OK", HttpStatus.OK);
		}catch (Exception e) {
			log.error("error sendFragment", e);
			return new ResponseEntity<>("KO", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
