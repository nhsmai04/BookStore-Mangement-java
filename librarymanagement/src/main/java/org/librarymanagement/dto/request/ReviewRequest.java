package org.librarymanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    public String comment;

    @Min(1)
    @Max(5)
    @NotNull(message = "Vui lòng chọn số sao để đánh giá")
    public Integer star;

}
