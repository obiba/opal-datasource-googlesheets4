package org.obiba.datasource.opal.googlesheets4;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.json.JSONObject;
import org.obiba.magma.Datasource;
import org.obiba.magma.DatasourceFactory;
import org.obiba.opal.spi.datasource.DatasourceUsage;
import org.obiba.opal.spi.r.datasource.AbstractRDatasourceFactory;
import org.obiba.opal.spi.r.datasource.AbstractRDatasourceService;
import org.obiba.opal.spi.r.datasource.RDatasourceFactory;
import org.obiba.opal.spi.r.datasource.magma.RDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleSheets4DatasourceService extends AbstractRDatasourceService {

  private static final Logger log = LoggerFactory.getLogger(GoogleSheets4DatasourceService.class);

  @Override
  public String getName() {
    return "opal-datasource-googlesheets4";
  }

  @Override
  public DatasourceFactory createDatasourceFactory(DatasourceUsage usage, JSONObject parameters) {
    RDatasourceFactory factory = new AbstractRDatasourceFactory() {
      @NotNull
      @Override
      protected Datasource internalCreate() {
        String spreadsheetId = parameters.optString("spreadsheetId");
        String sheetName = parameters.optString("sheetName");
        String na = parameters.optString("na");
        int skip = parameters.optInt("skip");

        String sanitized = StringSanitizer.sanitize(sheetName);
        String symbol = getSymbol(new File(StringSanitizer.unquote(sanitized)));
        // copy file to the R session
        execute(new GoogleSheets4ROperation(symbol, spreadsheetId, sanitized, na, skip));
        return new RDatasource(getName(), getRSessionHandler(), symbol, parameters.optString("entity_type"), parameters.optString("id"));
      }
    };
    factory.setRSessionHandler(getRSessionHandler());
    return factory;
  }

}
