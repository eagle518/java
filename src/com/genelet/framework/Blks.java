/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.util.Map;
import javax.json.JsonObject;

/**
 *
 * @author Peter
 */
public class Blks {
    Map Mail;
    Map Smail;
    
    public Blks(JsonObject v) {
        this.Mail  = (Map) v.get("Mail");
        this.Smail = (Map) v.get("Smail");
    }
}
