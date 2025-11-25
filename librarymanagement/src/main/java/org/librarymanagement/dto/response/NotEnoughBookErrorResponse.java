package org.librarymanagement.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotEnoughBookErrorResponse{
        private int status;
        private String error;
        private Map<Integer, String>fieldErrors;
}
