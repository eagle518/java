/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.util.List;

/**
 *
 * @author Peter
 */
public class Table {
    private String name;
    private String alias;
    private String type;
    private String using;
    private String on;
    
    public Table(String n, String a, String t, String u, String o) {
        this.name = n;
        this.alias = a;
        this.type = t;
        this.using = u;
        this.on = o;
    }
    
    public static String table_string(List<Table> tables) {
        String sql = "";
        int i = 0;
        for (Table table : tables) {
            String name = table.getName();
            if (!"".equals(table.getAlias())) {
                name += " " + table.getAlias();
            }
            if (i==0) {
                sql = name;
            } else if (!"".equals(table.getUsing())) {
                sql += "\n" + table.getType() + " JOIN " + name + " USING (" + table.getUsing() + ")";
            } else {
                sql += "\n" + table.getType() + " JOIN " + name + " ON (" + table.getOn() + ")";
            }
            i++;
        }

        return sql;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the using
     */
    public String getUsing() {
        return using;
    }

    /**
     * @param using the using to set
     */
    public void setUsing(String using) {
        this.using = using;
    }

    /**
     * @return the on
     */
    public String getOn() {
        return on;
    }

    /**
     * @param on the on to set
     */
    public void setOn(String on) {
        this.on = on;
    }
}
