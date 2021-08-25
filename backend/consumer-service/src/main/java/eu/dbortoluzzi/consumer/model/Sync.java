package eu.dbortoluzzi.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "sync")
public class Sync {
    @Id
    protected String id;
    protected Boolean elaborating;
    private Date lastSync;
}
