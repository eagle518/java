/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.util.List;
import java.util.Map;
import javax.json.JsonObject;

/**
 *
 * @author Peter
 */
public class GeneletFilter extends Base {
    public String action;
    public String component;
    public Map<String,Object> ARGS;
    public Map<String,Map<String,List<String>>> actions;
    public Map<String,List<String>> fks;
    
    public GeneletFilter() {
        super();
    }  
    
    public GeneletFilter(Object obj) {
        super();
        JsonObject item = (JsonObject) obj;
        if (item.containsKey("fks")) {
            this.fks = Config.getMapList(item.getJsonObject("fks"));
        }
        if (item.containsKey("actions")) {
            this.actions = Config.getMapMapList(item.getJsonObject("actions"));
        }
    }
    
    public void initialize(Gate gate, String action, String component, Map<String, Object>ARGS) {
        this.action=action;
        this.component=component;
        this.ARGS = ARGS;
        this.chartag_value = gate.chartag_value;
        this.role_value=gate.role_value;
        this.config=gate.config;
        this.request=gate.request;
        this.response=gate.response;
    }
    
    public Error preset() {
        return null;
    }
            
    public Error before(Object model, List<Map<String,Object>> extras) {
        return null;
    }
    
    public Error after(Object model) {
        return null;
    }
    
    public String get_tmpl() {
        return config.getTemplate() + "/" + role_value + "/" + component + "/" + action + "." + chartag_value;
    }
    
    /**
     * @param component the component to set
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return component;
    }

    /**
     * @return the ARGS
     */
    public Map<String,Object> getARGS() {
        return ARGS;
    }

    /**
     * @param ARGS the ARGS to set
     */
    public void setARGS(Map<String,Object> ARGS) {
        this.ARGS = ARGS;
    }

    /**
     * @return the actions
     */
    public Map<String,Map<String,List<String>>> getActions() {
        
        return actions;
    }

    /**
     * @param actions the actions to set
     */
    public void setActions(Map<String,Map<String,List<String>>> actions) {
        this.actions = actions;
    }

    /**
     * @return the fks
     */
    public Map<String,List<String>> getFks() {
        return fks;
    }

    /**
     * @param fks the fks to set
     */
    public void setFks(Map<String,List<String>> fks) {
        this.fks = fks;
    }
}
