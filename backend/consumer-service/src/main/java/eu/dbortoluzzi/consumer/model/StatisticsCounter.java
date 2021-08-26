package eu.dbortoluzzi.consumer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class StatisticsCounter {
    @JsonFormat(pattern="yyyyMMddHHmmss")
    private Date startDate;
    @JsonFormat(pattern="yyyyMMddHHmmss")
    private Date endDate;
    private Long counter;
}
