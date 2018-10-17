package com.shopping.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface SecurityMapping
{
  String title();

  String value();

  String rname();

  String rcode();

  int rsequence();

  String rgroup();

  String rtype();

  boolean display();
}