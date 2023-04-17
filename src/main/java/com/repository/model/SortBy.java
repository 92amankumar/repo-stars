package com.repository.model;

public enum SortBy {
  STARS("stars"),
  FORKS("forks");
  public static final String KEY = "sort";
  private final String value;

  SortBy(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
