package org.librarymanagement.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public abstract class StatDto {
     protected Integer currentStat;
     protected Integer previousStat;
     protected Double percentChange;

    public StatDto(Integer currentStat, Integer previousStat, Double percentChange) {
        this.currentStat = currentStat;
        this.previousStat = previousStat;
        this.percentChange = percentChange;
    }

}
