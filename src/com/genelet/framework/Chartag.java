/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import javax.json.JsonObject;

/**
 *
 * @author Peter
 */
public class Chartag {
    private String Content_type;
    private String Short;
    private int Case;
    private String Challenge;
    private String Logged;
    private String Logout;
    private String Failed;

    public Chartag(JsonObject v) {
        this.Content_type = v.getString("Content_type");
        this.Short = v.getString("Short");
        this.Challenge = v.getString("Challenge", "challenge");
        this.Logged = v.getString("Logged", "logged");
        this.Logout = v.getString("Logout", "logout");
        this.Failed = v.getString("Failed", "failed");
        this.Case = v.getInt("Case", 0);
    }
    
    public String call_challenge() { return charcase_string(Challenge); }
    public String call_logged() { return charcase_string(Logged); }
    public String call_logout() { return charcase_string(Logout); }
    public String call_failed() { return charcase_string(Failed); }

    private String charcase_string(String in) {
        if (Case==2) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><data>" + in + "</data>";
        } else if (Case==1) {
            return "{data:\"" + in + "\"}";
        }
        return "";
    }
    
    /**
     * @return the Content_type
     */
    public String getContent_type() {
        return Content_type;
    }

    /**
     * @param Content_type the Content_type to set
     */
    public void setContent_type(String Content_type) {
        this.Content_type = Content_type;
    }

    /**
     * @return the Short
     */
    public String getShort() {
        return Short;
    }

    /**
     * @param Short the Short to set
     */
    public void setShort(String Short) {
        this.Short = Short;
    }

    /**
     * @return the Case
     */
    public int getCase() {
        return Case;
    }

    /**
     * @param Case the Case to set
     */
    public void setCase(int Case) {
        this.Case = Case;
    }

    /**
     * @return the Challenge
     */
    public String getChallenge() {
        return Challenge;
    }

    /**
     * @param Challenge the Challenge to set
     */
    public void setChallenge(String Challenge) {
        this.Challenge = Challenge;
    }

    /**
     * @return the Logged
     */
    public String getLogged() {
        return Logged;
    }

    /**
     * @param Logged the Logged to set
     */
    public void setLogged(String Logged) {
        this.Logged = Logged;
    }

    /**
     * @return the Logout
     */
    public String getLogout() {
        return Logout;
    }

    /**
     * @param Logout the Logout to set
     */
    public void setLogout(String Logout) {
        this.Logout = Logout;
    }

    /**
     * @return the Failed
     */
    public String getFailed() {
        return Failed;
    }

    /**
     * @param Failed the Failed to set
     */
    public void setFailed(String Failed) {
        this.Failed = Failed;
    }
    
    
}
