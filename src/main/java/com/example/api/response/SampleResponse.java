package com.example.api.response;

import com.google.appengine.api.datastore.Email;
import com.google.cloud.datastore.Key;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SampleResponse {
    String message;
}
