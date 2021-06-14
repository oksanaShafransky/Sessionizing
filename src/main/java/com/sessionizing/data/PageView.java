package com.sessionizing.data;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PageView {
    String visitorId;
    String siteUrl;
    String pageViewUrl;
    long timestamp;
}
