package eu.dbortoluzzi.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dbortoluzzi.consumer.model.Atm;
import eu.dbortoluzzi.consumer.model.AtmIndexable;
import eu.dbortoluzzi.consumer.repository.AtmsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class AtmDataSeeder {

	Logger logger = LoggerFactory.getLogger(AtmDataSeeder.class);

	@Autowired
	private AtmsRepository atmsRepository;

	private final String atmsUrl;

	public AtmDataSeeder(AtmsRepository atmsRepository, @Value("${external.services.consumer.url}") String atmsUrl) {
		this.atmsUrl = atmsUrl;
		this.atmsRepository = atmsRepository;
	}

	public void seedIfEmpty(){

		try {
			String jsonArrayString = downloadUsingStream(atmsUrl);
			logger.info("read from url: {}", atmsUrl);

			ObjectMapper mapper = new ObjectMapper();
			List<Atm> atmList = mapper.readValue(jsonArrayString, new TypeReference<List<Atm>>() { });

            atmsRepository.deleteAll();
            long numberOfRecords = atmsRepository.count();
            logger.info("records: {}", numberOfRecords);
            if(numberOfRecords <= 0) {
                for(Atm atm: atmList) {
                    logger.info("save {}", atm.toString());
                    AtmIndexable atmExtended = new AtmIndexable(atm.getDistance(), atm.getType(), atm.getAddress());
                    atmsRepository.insert(atmExtended);
                }
            }
            logger.info("finish");
		}catch (Exception e) {
			logger.error("error in seedIfEmpty", e);
		}
	}


	private String downloadUsingStream(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream fis = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count=0;
		while((count = bis.read(buffer,0,1024)) != -1)
		{
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
		return fis.toString();
	}
}
