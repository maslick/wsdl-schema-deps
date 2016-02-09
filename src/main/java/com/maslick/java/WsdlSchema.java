package com.maslick.java;

import java.util.List;

/**
 * Created by maslick on 08/02/16.
 */
public class WsdlSchema {
    String name;
    List<String> imports;
    List<WsdlSchema> parent;
    int id;
}
