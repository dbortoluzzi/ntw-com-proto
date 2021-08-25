package eu.dbortoluzzi.consumer.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoutingElement {
    private boolean master;
    private String name;
}