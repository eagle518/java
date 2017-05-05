/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.test;

/**
 *
 * @author Peter
 */
public class ClassInvoke {
   public String x;

   public ClassInvoke() {
   }
   
   public String class1Method1() {
      return "*** Class 1, Method1 ***";
   }

   public String class1Method2() {
      return "### Class 1, Method2 ###";
   }

   public String class1Method3() {
      return x;
   }

   public void setX(String str) {
      this.x = str;
   }
}
