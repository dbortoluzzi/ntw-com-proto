package eu.dbortoluzzi.commons.model;

import lombok.*;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Fragment {
    private Payload payload;
    private Date timestamp;
    private String filename;
    private Long total;
    private Long index;
}