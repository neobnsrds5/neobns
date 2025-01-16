package com.example.neobns.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkheadMessage {
    private int maxConcurrentCalls;
    private long maxWaitDuration;
}
