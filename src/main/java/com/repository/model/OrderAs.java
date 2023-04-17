package com.repository.model;

public enum OrderAs {
  DESC("desc"),
  ASC("asc");
  public static final String KEY = "order";
  private final String value;

  OrderAs(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
