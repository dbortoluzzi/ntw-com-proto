package eu.dbortoluzzi.producer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@RestController
@Slf4j
public class ProducerController {

	@RequestMapping(value = "/api/producer/hello",
			method = RequestMethod.GET,
			produces = MediaType.ALL_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public String hello() throws UnknownHostException {
		return "Hello from producer " + InetAddress.getLocalHost().getHostAddress() + "aka "+InetAddress.getLocalHost().getHostName();
	}

}
