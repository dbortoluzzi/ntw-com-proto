package eu.dbortoluzzi.commons.model;

import lombok.*;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Payload {
    private Metadata metadata;
    private String text;

}