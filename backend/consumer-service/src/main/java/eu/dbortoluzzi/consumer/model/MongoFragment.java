package eu.dbortoluzzi.consumer.model;

import eu.dbortoluzzi.commons.model.Fragment;
import eu.dbortoluzzi.commons.model.Payload;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "fragment")
@CompoundIndexes({
        @CompoundIndex(name = "unique_ref", def = "{'payload.metadata.instance' : 1, 'uniqueFileName': 1, 'index': 1}")
})
public class MongoFragment extends Fragment {
    @Id
    protected String id;
    private Date creationDate;
    private String uniqueFileName;
    @Indexed
    private Boolean synced;
    private List<String> instancesSynced;

    public MongoFragment(Fragment fragment, String id, Date creationDate, Boolean synced, List<String> instancesSynced) {
        super(fragment.getPayload(), fragment.getTimestamp(), fragment.getFilename(), fragment.getTotal(), fragment.getIndex());
        this.id = id;
        this.creationDate = creationDate;
        this.uniqueFileName = generateUniqueFileName();
        this.synced = synced;
        this.instancesSynced = instancesSynced;
    }

    public MongoFragment(MongoFragment mongoFragment, Boolean synced, List<String> instancesSynced) {
        this(mongoFragment, mongoFragment.id, mongoFragment.creationDate, synced, instancesSynced);
    }

    private String generateUniqueFileName() {
        return getFilename()+"-"+ getTimestamp().getTime();
    }
}
