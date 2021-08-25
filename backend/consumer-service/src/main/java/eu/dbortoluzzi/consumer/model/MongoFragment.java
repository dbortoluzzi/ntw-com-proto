package eu.dbortoluzzi.consumer.model;

import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.model.Payload;
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
    private Boolean synced;

    public MongoFragment(Fragment fragment, String id, Date creationDate) {
        super(fragment.getPayload(), fragment.getTimestamp(), fragment.getFilename(), fragment.getTotal(), fragment.getIndex());
        this.id = id;
        this.creationDate = creationDate;
        this.uniqueFileName = generateUniqueFileName();
    }

    public MongoFragment(MongoFragment mongoFragment, Boolean synced) {
        this(mongoFragment, mongoFragment.id, mongoFragment.creationDate);
        this.synced = synced;
    }

    private String generateUniqueFileName() {
        return getFilename()+"-"+ getTimestamp().getTime();
    }
}
