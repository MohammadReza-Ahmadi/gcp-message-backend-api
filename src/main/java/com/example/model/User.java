package com.example.model;

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
public class User {
    Key key;
    String firstName;
    String lastname;
    Email email;
}
