package com.myoneweathertest.android.gson;

import java.util.concurrent.locks.Condition;

public class NowBean {
    public String tmp;
    public CondBean cond;

    public class CondBean {
        public String txt;
    }
}
