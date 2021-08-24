package eu.dbortoluzzi.consumer.model;

import eu.dbortoluzzi.commons.model.Fragment;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "fragment")
public class MongoFragment extends Fragment {
    @Id
    protected String id;
    private Date creationDate;
    private String uniqueFileName;

    public MongoFragment(Fragment fragment, Date receivingDate) {
        super(fragment.getPayload(), fragment.getTimestamp(), fragment.getFilename(), fragment.getTotal(), fragment.getIndex());
        this.creationDate = receivingDate;
        this.uniqueFileName = generateUniqueFileName();
    }

    private String generateUniqueFileName() {
        return getFilename()+"-"+ getTimestamp().getTime();
    }
}
