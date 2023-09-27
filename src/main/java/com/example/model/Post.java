/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.model;

import com.google.cloud.Timestamp;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {

    String id;
    String author;
    String subject;
    String body;
    Timestamp createDate;
    Timestamp changeDate;
    List<Comment> comments;


    public enum FIELDS{
        AUTHOR("author"),
        SUBJECT("subject"),
        BODY("body"),
        CREATE_DATE("createDate"),
        CHANGE_DATE("changeDate"),
        COMMENTS("comments");

        private String name;
        FIELDS(String name) {
            this.name = name;
        }

        public String getName(){
            return name;
        }

    }
}
