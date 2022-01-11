package com.yang.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PlaceLocation {


    @JsonProperty("status")
    private Integer status;
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("location")
        private LocationDTO location;
        @JsonProperty("precise")
        private Integer precise;
        @JsonProperty("confidence")
        private Integer confidence;
        @JsonProperty("comprehension")
        private Integer comprehension;
        @JsonProperty("level")
        private String level;

        @NoArgsConstructor
        @Data
        public static class LocationDTO {
            @JsonProperty("lng")
            private Double lng;
            @JsonProperty("lat")
            private Double lat;
        }
    }
}
