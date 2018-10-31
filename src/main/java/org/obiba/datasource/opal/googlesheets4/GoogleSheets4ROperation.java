package org.obiba.datasource.opal.googlesheets4;

import com.google.common.base.Strings;
import org.obiba.opal.spi.r.AbstractROperation;

public class GoogleSheets4ROperation extends AbstractROperation {

  private static final String SPREAD_SHEET_SYMBOL = "ss";

  private final String symbol;

  private final String spreadSheetsId;

  private final String sheetName;

  private final String missingValues;

  private final int rowsToSkip;

  GoogleSheets4ROperation(String symbol, String spreadSheetsId, String sheetName, String missingValues, int rowsToSkip) {
    this.symbol = symbol;
    this.spreadSheetsId = spreadSheetsId;
    this.sheetName = sheetName;
    this.missingValues = missingValues;
    this.rowsToSkip = rowsToSkip;
  }

  @Override
  protected void doWithConnection() {
    ensurePackage("tibble");
    eval("library(tibble)", false);
    ensurePackage("googledrive");
    ensurePackage("rematch2");
    ensurePackage("uuid");
    ensurePackage("debugme");
    ensurePackage( "glue");
    ensureGitHubPackage( "r-lib", "gargle", null);
    ensureGitHubPackage( "tidyverse", "googlesheets4", null);
    eval("library(googlesheets4)", false);

    eval(getSpreadsheetCommand(), false);
    eval(getCommand(), false);
  }

  private String getCommand() {
    return String.format("base::assign('%s', %s)", symbol, getReadSheetCommand());
  }

  private String getSpreadsheetCommand() {
    return String.format("base::assign('%s', sheets_get('%s'))", SPREAD_SHEET_SYMBOL, spreadSheetsId);
  }

  private String getReadSheetCommand() {
    return String.format(
      "sheets_read(%s$spreadsheet_id, %s %s %s)",
      SPREAD_SHEET_SYMBOL,
      sheetName,
      getRowsToSkip(),
      getMissingValues());
  }

  private String getRowsToSkip() {
    return rowsToSkip < 1 ? "" : String.format(", skip = %d", rowsToSkip);
  }

  private String getMissingValues() {
    return Strings.isNullOrEmpty(missingValues) ? "" : String.format(", na = c(%s)", missingValues.trim());
  }

  @Override
  public String toString() {
    return getCommand();
  }
}
