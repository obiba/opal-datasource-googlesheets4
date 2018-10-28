package org.obiba.datasource.opal.googlesheets4;

import com.google.common.base.Strings;
import org.obiba.opal.spi.r.AbstractROperation;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;

public class GoogleSheets4ROperation extends AbstractROperation {

  private static final String SPREAD_SHEET_SYMBOL = "ss";

  private final String symbol;

  private final String spreadSheetsId;

  private final String sheetName;

  private final String columnSpecification;

  GoogleSheets4ROperation(String symbol, String spreadSheetsId, String sheetName, String columnSpecification) {
    this.symbol = symbol;
    this.spreadSheetsId = spreadSheetsId;
    this.sheetName = sheetName;
    this.columnSpecification = columnSpecification;
  }

  @Override
  protected void doWithConnection() {
    if(Strings.isNullOrEmpty(spreadSheetsId)) return;
    ensurePackage("tibble");
    eval("library(tibble)", false);
    ensurePackage("googledrive");
    ensurePackage("rematch2");
    ensurePackage("uuid");
    ensureGitHubPackage( "tidyverse", "glue", null);
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
    return String.format("sheets_read(%s$spreadsheet_id, '%s' %s)", SPREAD_SHEET_SYMBOL, sheetName, columnTypes());
  }

  private String columnTypes() {
    return Strings.isNullOrEmpty(columnSpecification) ?  "" : String.format(", col_types = '%s'", columnSpecification);
  }

  @Override
  public String toString() {
    return getCommand();
  }
}
