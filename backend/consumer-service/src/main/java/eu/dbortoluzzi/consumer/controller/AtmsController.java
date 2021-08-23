package eu.dbortoluzzi.consumer.controller;

import eu.dbortoluzzi.consumer.model.AtmIndexable;
import eu.dbortoluzzi.consumer.repository.AtmsRepository;
import eu.dbortoluzzi.consumer.repository.AtmsRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@RestController
@Slf4j
public class AtmsController {
	@Autowired
	private AtmsRepositoryCustom atmsRepositoryCustom;
	@Autowired
	private AtmsRepository atmsRepository;

	public AtmsController(AtmsRepositoryCustom atmsRepositoryCustom, AtmsRepository atmsRepository) {
		this.atmsRepositoryCustom = atmsRepositoryCustom;
		this.atmsRepository = atmsRepository;
	}

	@RequestMapping(value = "/api/consumer/hello",
			method = RequestMethod.GET,
			produces = MediaType.ALL_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public String hello() throws UnknownHostException {
		return "Hello from " + InetAddress.getLocalHost().getHostAddress() + "aka "+InetAddress.getLocalHost().getHostName();
	}

	@RequestMapping(value = "/api/consumer/checkContainerName/{containerName}",
			method = RequestMethod.GET,
			produces = MediaType.ALL_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public String checkContainerName(@PathVariable  String containerName) throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		InetAddress searchContainerAddress = InetAddress.getByName(containerName);
		log.info("localhost: " + localHost.getHostAddress() + "|" + localHost.getHostName());
		log.info("searchContainerAddress: " + searchContainerAddress.getHostAddress() + "|" + searchContainerAddress.getHostName());
		return String.valueOf(searchContainerAddress.getHostAddress().equals(localHost.getHostAddress()));
	}

	@RequestMapping(value = "/api/consumer",
					method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public AtmsResponse getAtms() {
		List<AtmIndexable> atms = atmsRepository.findAll();

		AtmsResponse response = new AtmsResponse(atms, Long.valueOf(atms.size()));

		return response;
	}

	@GetMapping("/api/consumer/search/{query}/{page}/{size}")
	@ResponseStatus(HttpStatus.OK)
	@CrossOrigin
	public AtmsResponse getAtmsByQuery(@PathVariable  String query, @PathVariable  Integer page, @PathVariable Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<AtmIndexable> pageResponse = atmsRepositoryCustom.search(query, pageRequest);
		pageResponse.getContent();

		AtmsResponse response = new AtmsResponse(pageResponse.getContent(), pageResponse.getTotalElements());
		response.setAtms(pageResponse.getContent());

		return response;
	}
}
