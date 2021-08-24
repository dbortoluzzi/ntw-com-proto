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
    private Date receivingDate;

    public MongoFragment(Fragment fragment, Date receivingDate) {
        super(fragment.getPayload(), fragment.getTimestamp(), fragment.getTotal(), fragment.getIndex());
        this.receivingDate = receivingDate;
    }
}
