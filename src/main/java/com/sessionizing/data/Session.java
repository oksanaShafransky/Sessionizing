package com.sessionizing.data;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class Session {
    String visitorId;
    String siteUrl;
    long sessionStart;
    long sessionLastPageTimestamp;
    long sessionLength;
    List<PageView> pageViewList;
    int pageViewCount;
}
